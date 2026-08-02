package com.kt.openapi.web.util;

import com.kt.openapi.web.cmmn.ApiException;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;


@Service
public class RsaManager {
	
	@Autowired
	private RsaMakeKey rsaMake;// 코드 값 추출
	
	private ServletRequestAttributes attr = null;
	private HttpSession session = null;

	/**
	 * Resources 폴더에 있는 공개키, 비공개키 값을 조회, 없으면 생성
	 * @throws IOException 
	 */
	private HashMap<String, String> _getKey() throws IOException {
		HashMap<String, String> returnMap = new HashMap<String, String>();
		
		try {
			InputStream fPath  = getClass().getResourceAsStream("/private_key.crt");
			InputStream fPath1 = getClass().getResourceAsStream("/public_key.crt");
			
			returnMap.put("szPrivateKey", IOUtils.toString(fPath , "UTF-8"));
			returnMap.put("szPublicKey" , IOUtils.toString(fPath1, "UTF-8"));
		} catch (NullPointerException e) {
		  //-- [2023:codeeyes][empty_block issue]
			//returnMap = rsaMake.generator();
		}
		
		return returnMap;
	}
	
	@SuppressWarnings("unchecked")
	private HashMap<String, String> getKey() throws IOException {
		HashMap<String, String> returnMap = null;
		
		try {
			this.attr 	 = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
			this.session = this.attr.getRequest().getSession();
			returnMap = (HashMap<String, String>) this.session.getAttribute("RSA");
			
			if(returnMap == null) {
//				if(rsaMake == null) {
//					rsaMake = new RsaMakeKey();
//				}
				rsaMake.generator();
			}

		} catch (NullPointerException e) {
//			if(rsaMake == null) {
//				rsaMake = new RsaMakeKey();
//			}
			rsaMake.generator();
		}
		returnMap = (HashMap<String, String>) this.session.getAttribute("RSA");
		
		return returnMap;
	}

	/**
	 * 대상을 공개키로 암호화 처리
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public String encrypt(String value) throws Exception {
		HashMap<String, String> returnMap = new HashMap<String, String>();
		returnMap = this.getKey();
		String szPublicKey = returnMap.get("szPublicKey");
		byte[] bPublicKey2 = Base64.decodeBase64(szPublicKey.getBytes());
		KeyFactory keyFactory2 = KeyFactory.getInstance("RSA");

		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bPublicKey2);
		PublicKey publicKey = keyFactory2.generatePublic(publicKeySpec);

		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

		// 공개키 이용 암호화
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] bCipher = cipher.doFinal(value.getBytes());
		//String sCipherBase64 = Base64.encodeBase64String(bCipher);
		String sCipherBase64 = new String(Base64.encodeBase64(bCipher),"UTF-8");

		return sCipherBase64;
	}

	/**
	 * 대상을 비공개키로 복호화 처리
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public String decrypt(String value) throws Exception {
		HashMap<String, String> returnMap = new HashMap<String, String>();
		returnMap = this.getKey();
		String szPrivateKey = returnMap.get("szPrivateKey");
		byte[] bPrivateKey  = Base64.decodeBase64(szPrivateKey.getBytes());
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bPrivateKey);
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

		// 개인키 이용 복호화
		byte[] bCipher = Base64.decodeBase64(value.getBytes());
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] bPlain = cipher.doFinal(bCipher);
		String sPlain = new String(bPlain);

		return sPlain;
	}

	/**
	 * WEB UI 용 RSA 복호화
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public String webDecrypt(String value) throws ApiException {
		HashMap<String, String> returnMap = new HashMap<String, String>();
		String sPlain = null; 
		try {
			returnMap = this.getKey();
		
			String szPrivateKey = returnMap.get("szPrivateKey").toString();
			byte[] bPrivateKey  = Base64.decodeBase64(szPrivateKey.getBytes());
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	
			PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bPrivateKey);
			PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
	
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			byte[] encryptBytes = hexToByArray(value);
	
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] bPlain = cipher.doFinal(encryptBytes);
			// 한글깨짐 수정 CYD-2022.08.24
			sPlain = new String(bPlain,"UTF-8").trim();
		
		} catch (IOException e) {
			throw new ApiException(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new ApiException(e.getMessage());
		} catch (InvalidKeySpecException e) {
			throw new ApiException(e.getMessage());
		} catch (NoSuchPaddingException e) {
			throw new ApiException(e.getMessage());
		} catch (InvalidKeyException e) {
			throw new ApiException(e.getMessage());
		} catch (IllegalBlockSizeException e) {
			throw new ApiException(e.getMessage());
		} catch (BadPaddingException e) {
			throw new ApiException(e.getMessage());
		}

		return sPlain;
	}

	private byte[] hexToByArray(String value) {
		if (value == null || value.length() % 2 != 0) {
			return new byte[] {};
		}
		byte[] valByte = new byte[value.length() / 2];
		for (int i = 0; i < value.length(); i += 2) {
			byte val = (byte) Integer.parseInt(value.substring(i, i + 2), 16);
			valByte[(int) Math.floor(i / 2)] = val;
		}
		return valByte;
	}
	
	public HashMap<String, String> getWebModuluExp() throws ApiException {
		HashMap<String, String> returnMap  = null;
		HashMap<String, String> returnMap1 = null;
		String szReturnVal   = null;
		String szExponentVal = "10001";
		
		try {
			returnMap1 = this.getKey();
			
			if(returnMap1 != null) {
				
				if(returnMap == null) {
//					if(rsaMake == null) {
//						rsaMake = new RsaMakeKey();
//					}
					returnMap = rsaMake.makeModules();
				}
				
				szReturnVal   = returnMap.get("RSAModulus");
				szExponentVal = returnMap.get("RSAExponent");
				System.out.println("Web Modulus is " + szReturnVal);
				System.out.println("Web Exponent is " + szExponentVal);
				
				returnMap.put("RSAModulus" , szReturnVal);
				returnMap.put("RSAExponent", szExponentVal);
			}
			
		} catch(NullPointerException | IOException e) {
			throw new ApiException(e, "RSA Error");
		}
		
		return returnMap;
	}
	
	private HashMap<String, String> _getWebModuluExp() {
		HashMap<String, String> returnMap  = new HashMap<String, String>();
		HashMap<String, String> returnMap1 = null;
		String szReturnVal   = null;
		String szExponentVal = "10001";
		
		try {
			returnMap1 = this.getKey();
			
			if(returnMap1 != null) {
				InputStream sslFile = getClass().getResourceAsStream("/web_modulu_exp.crt");
				szReturnVal = IOUtils.toString(sslFile, "UTF-8");
				System.out.println("Web Modulus & Exponent is " + szReturnVal);
				
				String[] arrReturnVal = szReturnVal.split("\\|\\|");
				szReturnVal   = arrReturnVal[0];
				szExponentVal = arrReturnVal[1];
				System.out.println("Web Modulus is " + arrReturnVal[0]);
				System.out.println("Web Exponent is " + arrReturnVal[1]);
				
				returnMap.put("RSAModulus" , szReturnVal);
				returnMap.put("RSAExponent", szExponentVal);
			}
			
		} catch(NullPointerException | IOException e) {
			//returnMap = rsaMake.makeModules();
			returnMap = null;
		}
		
		return returnMap;
	}

}