package com.kt.openapi.web.util;


import jakarta.servlet.http.HttpSession;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;


@Service
public class RsaMakeKey {
	
	private static Logger logger = LoggerFactory.getLogger(RsaMakeKey.class);
	
	private ServletRequestAttributes attr = null;
	private HttpSession session = null;
	
	/**
	 * Key 생성 값으로 생성 모바일용 데이터
	 * 
	 * @return
	 */
	public void generator() {

		PublicKey publicKey1   = null;
		PrivateKey privateKey1 = null;

		SecureRandom secureRandom = new SecureRandom();
		KeyPairGenerator keyPairGenerator;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048, secureRandom);

			KeyPair keyPair = keyPairGenerator.genKeyPair();
			publicKey1  = keyPair.getPublic();
			privateKey1 = keyPair.getPrivate();

		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
		}

		byte[] bPublicKey1 = null;
		if(publicKey1 != null) {
			bPublicKey1 = publicKey1.getEncoded();
		}
		
		String szPublicKey1 = "";
		try {
			//szPublicKey1 = new String(Base64.encodeBase64(bPublicKey1),"EUC-KR");
			szPublicKey1 = new String(Base64.encodeBase64(bPublicKey1),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}

		byte[] bPrivateKey1 = null;
		if(privateKey1 != null) {
			bPrivateKey1 = privateKey1.getEncoded();
		}
		
		String szPrivateKey1 = "";
		try {
			//szPrivateKey1 = new String(Base64.encodeBase64(bPrivateKey1),"EUC-KR");
			szPrivateKey1 = new String(Base64.encodeBase64(bPrivateKey1),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
		
// 임시 주석 처리 CYD - 2023.06.18
//		try {
//			//RSA 암호화 공개키 값 저장
//			this.save(szPublicKey1 , "public_key.crt");
//			//RSA 암호화 비공개키 값 저장
//			this.save(szPrivateKey1, "private_key.crt");
//		} catch (IOException e) {
//			logger.error(e.getMessage());
//		}
		
		this.attr 	 = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		this.session = this.attr.getRequest().getSession();

		HashMap<String, String> returnMap = new HashMap<String, String>();
		returnMap.put("szPublicKey" , szPublicKey1);
		returnMap.put("szPrivateKey", szPrivateKey1);
		
		this.session.setAttribute("RSA", returnMap);


		//return returnMap;
	}

	/**
	 * 생성된 공개키로 WEB 화면에 전달 할 값 생성 및 resources 파일에 저장'
	 * (Modulus,Exponent)
	 * 
	 * @param request
	 */
	@SuppressWarnings({ "unchecked" })
	public HashMap<String, String> makeModules() {
		HashMap<String, String> returnMap  = null;
		
		try {
			this.attr 	 = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
			this.session = this.attr.getRequest().getSession();
			
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			
			logger.debug("public_key.crt File is reading");
			// 임시주석 처리 CYD - 2023.06.18
			//String szPublicKey = this.read("public_key.crt");
			generator();
			returnMap = (HashMap<String, String>) this.session.getAttribute("RSA");
			
//			if(returnMap == null) {
//				generator();
//				returnMap = (HashMap<String, String>) this.session.getAttribute("RSA");
//			}
			
			String szPublicKey = returnMap.get("szPublicKey");

			// 공개키 Key
			byte[] bPublicKey = null;
			if(szPublicKey != null) {
				bPublicKey = Base64.decodeBase64(szPublicKey.getBytes());
			}
			
			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bPublicKey);
			PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

			RSAPublicKeySpec publicSpec = (RSAPublicKeySpec) keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
			String publicKeyModulus  = publicSpec.getModulus().toString(16);
			String publicKeyExponent = publicSpec.getPublicExponent().toString(16);

			//웹용 RSA 암호화 공개키 Modulus And Exponent 값 저장
			//this.save(publicKeyModulus + "||" + publicKeyExponent, "web_modulu_exp.crt");

			returnMap.put("RSAModulus" , publicKeyModulus);
			returnMap.put("RSAExponent", publicKeyExponent);
			
			this.session.setAttribute("RSA", returnMap);

		} catch (NullPointerException e) {
			logger.error(e.getMessage());
		} catch (InvalidKeySpecException e) {
			logger.error(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
		}
		
		return returnMap;
	}
	
	private void save(String contents, String filename) throws IOException {
		try {
			URL fPath 	   = getClass().getResource("/");
			String newPath = null;

			if(fPath != null) {
				// 리소스경로 읽어 올 때 인코딩 적용 되어 폴더 혹은 파일명에 공백이 들어간 경우 %20으로 치완되어 잘못 된 경로로 인식되어 디코딩처리
				newPath = URLDecoder.decode(fPath.getPath(), "UTF-8") + filename;
			}

			OutputStream bos = null;
			byte[] bytes = contents.getBytes();
			try {
				bos = new FileOutputStream(new File(newPath));
				bos.write(bytes);
				//bos.close();
			} catch(IOException e) {
				logger.error(e.getMessage());
			} finally {
				bos.close();
				//logger.debug(filename + " Key: " , contents);
			}
		} catch(NullPointerException e) {
			logger.error(e.getMessage());
		}
	}
	
	private String read(String filename) {
		String szReturnValue = null;

		BufferedReader in = null;
		try {
			URL fPath 	   = getClass().getResource("/");
			String newPath = null;

			if(fPath != null) {
				// 리소스경로 읽어 올 때 인코딩 적용 되어 폴더 혹은 파일명에 공백이 들어간 경우 %20으로 치완되어 잘못 된 경로로 인식되어 디코딩처리
				newPath = URLDecoder.decode(fPath.getPath(), "UTF-8") + filename;
			}
			//logger.debug(newPath);
			File f = new File(newPath);
			if(f.isFile() && f.canRead()) {
				in = new BufferedReader(new FileReader(f));
				String s;
				while((s = in.readLine()) != null) {
					//logger.debug(s);
					szReturnValue = s;
				}
				in.close();
			} else {
				logger.debug("public_key.crt File is not exists");
			}
		} catch(NullPointerException | IOException e) {
			logger.error(e.getMessage());
		}
		finally {
      //-- [2023:codeeyes][File 자원 해제 검사 필수 issue]
	    try {
				if (in != null) { in.close(); }
	    } catch (IOException e) {
	      //-- [2023:codeeyes][empty_block issue]
	      //e.printStackTrace();
	    }
		}
		
		return szReturnValue;
	}

}
