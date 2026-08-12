package com.kt.openapi.web.util;

import com.initech.safedb.SimpleSafeDB;
import com.kt.openapi.web.adptran.util.KsmUtil;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Base64;
import java.util.regex.Pattern;

/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.util
* 2. 타입명 : CommonFunc.java
* 3. 작성일 : 2017. 11. 30. 오후 3:00:52
* 4. 작성자 : ANEUNTAEK
* 5. 설명 : 암호화 및 복호화
* </pre>
*/
@Component
public class CommonFunc {

	private static final Logger logger = LoggerFactory.getLogger(CommonFunc.class);

	static String userName = "SAFEDB";
	static String tableName = "SAFEDB.POLICY";
	static String columnName = "KT_IDENTITY_NO";
	//static String columnName = "AES128";
	static String passColumnName = "KT_PASSWORD_NO";	//-- [tag:login][drm][add][181027][from apilink]

	private static String apisystemObserverBstmngUserlist;
	@Value("${apisystem.observer.bstmng.userlist}")
	public void setApisystemObserverBstmngUserlist(String apisystemObserverBstmngUserlist) {
		CommonFunc.apisystemObserverBstmngUserlist = apisystemObserverBstmngUserlist;
		logger.debug("[start: {}.{}()][config.runmode: {}]", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), CommonFunc.apisystemObserverBstmngUserlist);
	}
	
	private static String apisystemObserverBstviewerUserlist;
	@Value("${apisystem.observer.bstviewer.userlist}")
	public void setApisystemObserverBstviewerUserlist(String apisystemObserverBstviewerUserlist) {
		CommonFunc.apisystemObserverBstviewerUserlist = apisystemObserverBstviewerUserlist;
		logger.debug("[start: {}.{}()][config.runmode: {}]", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), CommonFunc.apisystemObserverBstviewerUserlist);
	}

	// SafeDB Agent에 접근할 수 없는 로컬 개발 환경용 우회 스위치 (기본 false, 실배포 영향 없음)
	private static boolean safeDbMockEnabled = false;
	@Value("${safedb.mock.enabled:false}")
	public void setSafeDbMockEnabled(boolean safeDbMockEnabled) {
		CommonFunc.safeDbMockEnabled = safeDbMockEnabled;
		if (safeDbMockEnabled) {
			logger.warn("[SafeDB MOCK MODE] 실제 SafeDB 암/복호화 호출이 평문 통과로 대체됩니다. 로컬 개발 전용으로만 사용하세요.");
		}
	}

	/**
	* <pre>
	* 1. 메소드명 : safeDbEncrypt
	* 2. 작성일 : 2017. 11. 30. 오후 3:00:10
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 암호화 처리
	* </pre>
	* @param str
	* @return
	* @throws Exception
	*/
	public static String safeDbEncrypt(String str) throws Exception {
		if (safeDbMockEnabled) {
			return str;
		}
		//--##logger.debug("safeDbEncrypt Para :  {}" , str);
		SimpleSafeDB safedb = SimpleSafeDB.getInstance();
		boolean loginResult = false;
		if (!safedb.login()) {
			loginResult = safedb.getSafeDBConfigMgr().isLoginCheck();
		}
		//--##logger.debug("safeDbEncrypt() loginResult: {}", loginResult);

		byte[] plainData = str.getBytes("EUC-KR");
		byte[] dataBytes = "".getBytes("EUC-KR");
		//--##logger.debug("Encrypt plainData : {}", plainData);
		try {
			dataBytes = safedb.encrypt(userName, tableName, columnName, plainData);
			//--##logger.debug("Encrypt Data : " + new String(dataBytes, "EUC-KR"));
		} catch (Exception e) {
			dataBytes = "".getBytes("EUC-KR");
			logger.error("safeDbEncrypt()-[Exception: Exception][e: {}]", e.getMessage(), e);
		}

		String strEnc = ((dataBytes != null) ? new String(dataBytes, "EUC-KR") : "");
		logger.debug("safeDbEncrypt()-[login: {}][srcStr: {}][encStr: {}][srcByte: {}][encByte: {}]" , loginResult, str, strEnc, plainData, dataBytes);

		return strEnc;
	}

	
	/**
	* <pre>
	* 1. 메소드명 : safeDbDecrypt
	* 2. 작성일 : 2017. 11. 30. 오후 3:00:34
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 복호화 처리
	* </pre>
	* @param str
	* @return
	* @throws Exception
	*/
	public static String safeDbDecrypt(String str) throws Exception {
		if (safeDbMockEnabled) {
			return str;
		}
		//--##logger.debug("safeDbDecrypt Para :  {}" , str);
		SimpleSafeDB safedb = SimpleSafeDB.getInstance();
		boolean loginResult = false;
		if (!safedb.login()) {
			loginResult = safedb.getSafeDBConfigMgr().isLoginCheck();
		}
		//--##logger.debug("safeDbDecrypt() loginResult: {}", loginResult);
		
		byte[] plainData = str.getBytes("EUC-KR");
		byte[] dataBytes = "".getBytes("EUC-KR");
		//--##logger.debug("Decrypt plainData : {}", plainData);
		try {
			dataBytes = safedb.decrypt(userName, tableName, columnName, plainData);
			//--##logger.debug("Decrypt Data : " + new String(dataBytes, "EUC-KR"));
		} catch (Exception e) {
			dataBytes = "".getBytes("EUC-KR");
			logger.error("safeDbDecrypt()-[Exception: Exception][e: {}]", e.getMessage(), e);
		}
		
		String strDec = ((dataBytes != null) ? new String(dataBytes, "EUC-KR") : "");
		logger.debug("safeDbDecrypt()-[login: {}][srcStr: {}][decStr: {}][srcByte: {}][decByte: {}]" , loginResult, str, strDec, plainData, dataBytes);

		return strDec;
	}
	
	/**
	* <pre>

	//-- [tag:login][drm][add][181027][from apilink] 
	* 1. 메소드명 : safeDbPassEncrypt
	* 2. 작성일 : 2018. 7. 30. 오전 10:02:51
	* 3. 작성자 : jj
	* 4. 설명 : 암호화 처리(비밀번호) : 단반향
	
	* </pre>
	* @param str
	* @return
	* @throws Exception
	* @throws UnsupportedEncodingException
	*/
	public static String safeDbPassEncrypt(String str) throws Exception, UnsupportedEncodingException {
		if(str == null || "".equals(str)) {
			return str;
		}
		if (safeDbMockEnabled) {
			return str;
		}
		//--##logger.debug("safeDbPassEncrypt Para :  {}" , str);
		SimpleSafeDB safedb = SimpleSafeDB.getInstance();
		boolean loginResult = false;
		if (!safedb.login()) {
			loginResult = safedb.getSafeDBConfigMgr().isLoginCheck();
		}
		//--##logger.debug("safeDbPassEncrypt() loginResult : {}", loginResult);
		
		byte[] plainData = str.getBytes("EUC-KR");
		byte[] dataBytes = "".getBytes("EUC-KR");
		//--##logger.debug("Password Encrypt  plainData : {}", plainData);
		try {
			dataBytes = safedb.encrypt(userName, tableName, passColumnName, plainData);
			//--##logger.debug("Password Encrypt Data : " + new String(dataBytes));
		} catch (Exception e) {
			dataBytes = "".getBytes("EUC-KR");
			logger.error("safeDbPassEncrypt()-[Exception: Exception][e: {}]", e.getMessage(), e);
		}

		String strEnc = ((dataBytes != null) ? new String(dataBytes, "EUC-KR") : "");
		logger.debug("safeDbPassEncrypt()-[login: {}][srcStr: {}][encStr: {}][srcByte: {}][encByte: {}]" , loginResult, str, strEnc, plainData, dataBytes);

		return strEnc;
	}

	public static String urlDecodeStr(String str) {
		
		String rtnStr = "";
		try {
			if(str.contains("%")) {
				rtnStr = URLDecoder.decode(str,"UTF-8");
			}else {
				rtnStr = str;
			}
			
			logger.debug("디코드이전값: {}, 디코드값: {}",str,rtnStr); 
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.error("URLDecoder error : " + e.getMessage()+","+str);
		}
		
		return rtnStr;
	}
	
	/**
	* <pre>
	* 1. 메소드명 : setSession
	* 2. 작성일 : 2018. 7. 30. 오전 10:02:51
	* 3. 작성자 : jj
	* 4. 설명 :
	
	* </pre>
	* @param str
	*/
	public static void setSession(String sessionNm, String str, HttpSession session ) {
		logger.debug("setSession()-[key: {}][value: {}] " , sessionNm, str);

		session.setAttribute(sessionNm,str);
	}
	
	/**
	* <pre>
	* 1. 메소드명 : getSession
	* 2. 작성일 : 2018. 7. 30. 오전 10:02:51
	* 3. 작성자 : jj
	* 4. 설명 :
	
	* </pre>
	* @param str
	*/
	public static int getSession(String sessionNm , HttpSession session) {
		logger.debug("getSession()-[key: {}][value: {}] " , sessionNm, session.getAttribute(sessionNm));

		return Integer.parseInt((String)session.getAttribute(sessionNm));
	}
	
	//-- [tag:PRJ-20220901]
	public static boolean isRunmodeTag(String tag) {
		ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = servletRequestAttribute.getRequest().getSession(true);
	
		String devRunmodeTagValue = KsmUtil.fnSafeStr(session.getAttribute("dev.runmode.tag"));
		tag = KsmUtil.fnSafeStr(tag);

		return ((tag.length() > 0) && (";" + devRunmodeTagValue + ";").contains(";" + tag + ";"));
	}

	//--[tag:PRJ-20220901][i][기능지정 user를 구분]
	public static boolean isSpecificUser(String tag) {
		ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = servletRequestAttribute.getRequest().getSession(true);
		
		boolean isSpecificUser = false;
		UserJoinVO ssUserVo = (UserJoinVO)session.getAttribute("ssUserVo");
		if (null == ssUserVo) { return isSpecificUser; }
	
		String ssMbrId = KsmUtil.fnSafeStr(ssUserVo.getMbrId());
		if (ssMbrId.length() == 0) { return isSpecificUser; }

    String userList = "";
    String strGub = ",";

		tag = KsmUtil.fnSafeStr(tag);
		if ("bstgw.manager".equalsIgnoreCase(tag)) {
			userList = KsmUtil.fnSafeStr(apisystemObserverBstmngUserlist);
		}
		//-- [tag:SR-20230706]
		else if ("bstgw.viewer".equalsIgnoreCase(tag)) { 
      userList = KsmUtil.fnSafeStr(apisystemObserverBstviewerUserlist);
		}
    isSpecificUser = ((userList.length() > 0) && (strGub + userList + strGub).contains(strGub + ssMbrId + strGub));

		// 임시 진단 로그 - BEAST 메뉴 접근 권한이 왜 안 먹히는지 실제 비교값을 눈으로 확인하기 위함
		// (설정이 반영 안 됐는지 / mbrId 값 자체가 기대와 다른지 구분용). 원인 확정되면 제거.
		logger.debug("[isSpecificUser][tag: {}][ssMbrId: {}][userList: {}][result: {}]", tag, ssMbrId, userList, isSpecificUser);

		return isSpecificUser;
	}
	
	//--[tag:PRJ-20220901][i][기능지정 user를 구분]
	public static String getActiveProfile() {
		ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest httpServletRequest = servletRequestAttribute.getRequest();
		//--##HttpSession session = httpServletRequest.getSession(true);
		
		String apimanagerProfilesActive = System.getProperty("spring.profiles.active");
		apimanagerProfilesActive = (((null != apimanagerProfilesActive) && (apimanagerProfilesActive.length() > 0)) ? apimanagerProfilesActive : httpServletRequest.getServletContext().getInitParameter("spring.profiles.active"));
	
		return KsmUtil.fnSafeStr(apimanagerProfilesActive);
	}
	
	//AES256 암호화
	public static String aesEncode(String str, String key) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, NoSuchProviderException {
		byte[] keyData = key.getBytes("UTF-8");
		byte[] ivData  = new byte[16];
		SecretKey secureKey = new SecretKeySpec(keyData,"AES");
		String enStr = "";
		
		try {
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			
			try {
				c.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(ivData));
				
				byte[] encryted = c.doFinal(str.getBytes("UTF-8"));
				enStr = new String(Base64.getEncoder().encode(encryted));
			} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
				logger.error("AES Encode error : " + e.getMessage());
			}
		} catch (NoSuchAlgorithmException e) {
			logger.error("AES Encode error : " + e.getMessage());
		} catch (NoSuchPaddingException e) {
			logger.error("AES Encode error : " + e.getMessage());
		}
		
		return enStr;
	}
	
	//AES256 복호화
	public static String aesDecode(String str, String key) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, NoSuchProviderException {
		byte[] keyData = key.getBytes("UTF-8");
		byte[] ivData  = new byte[16];
		SecretKey secureKey = new SecretKeySpec(keyData,"AES");
		String enStr = "";
		
		try {
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			
			try {
				c.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(ivData));
				
				byte[] byteStr = Base64.getDecoder().decode(str.getBytes("UTF-8"));
				enStr = new String(c.doFinal(byteStr),"UTF-8");
			} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
				logger.error("AES Decode error : " + e.getMessage());
			}
		} catch (NoSuchAlgorithmException e) {
			logger.error("AES Decode error : " + e.getMessage());
		} catch (NoSuchPaddingException e) {
			logger.error("AES Decode error : " + e.getMessage());
		}
		
		return enStr;
	}
	
	public static String strMasking(String value, String type) {
		String strMasking = "";
		//-- [drm][add]
		
		if(value == null || "".equals(value)) {
			return value;
		}
		
		int size = value.length();
		
		if(!value.trim().isEmpty() && size < 4) {
			value = value.substring(0,1) + "*****"; 
		}
		
		if (!value.trim().isEmpty()) {
			if ("phone".equals(type)) {
				strMasking = "%s-****-**%s".formatted(value.substring(0, 3), value.substring(size - 2, size));
			}
			else if ("email".equals(type)) {
				strMasking = "%s***@****".formatted(value.substring(0, 3));
			}
			else if ("id".equals(type)) {
				strMasking = "%s***".formatted(value.substring(0, 4));
			}
			else if ("name".equals(type)) {
				strMasking = "%s*".formatted(value.substring(0, 2));
			}
			else if ("sabun".equals(type)) {
				strMasking = "%s*%s*".formatted(value.substring(0, 2), value.substring(3, 3 + (size - 4)));
			}
			else if ("partname".equals(type)) {
				strMasking = "%s*%s*".formatted(value.substring(0, 2), value.substring(3, 3 + (size - 4)));
			}
			else if ("middle".equals(type)) {
				strMasking = "%s***%s".formatted(value.substring(0, 1), value.substring(size - 1, size));
			}
			else if ("exceptForOne".equals(type)) {							
				strMasking = "%s%s".formatted(value.substring(0, 1), setMaskingStar(size - 1));			
			}			
		}
		return strMasking;
	}
	
	//마스킹 처리 할 별 갯수 셋팅
	public static String setMaskingStar(int starNum) {
		
		String star = "";
		
		for(int i=0; i<starNum; i++) {
			star+="*";
		}
		
		return star;
	}
	//xss 요소 검출
	public static boolean findXSSChars(String input) {
		
		logger.info("findXSSChars 실행");
		
	    if (input == null || input.isEmpty()) {
	        return false;
	    }
	    
	    logger.info("input값 확인 : {}", input);
	    
	    input = input.replace("&apos;", "'"); //StringEscapeUtils.unescapeHtml4는 &apos;를 역치환 하지 못하므로 별도로 replace 해줘야함

	    // XSS 검출 요소
	    String[] xssPatterns = {
	    	    // JS 실행 함수
	    	    "eval\\s*\\(",
	    	    "alert\\s*\\(",
	    	    "prompt\\s*\\(",
	    	    "confirm\\s*\\(",
	    	    "settimeout\\s*\\(",
	    	    "setinterval\\s*\\(",

	    	    // HTML 속성 / 이벤트
	    	    "src\\s*=",
	    	    "href\\s*=",
	    	    "onerror\\s*=",
	    	    "onclick\\s*=",
	    	    "onload\\s*=",

	    	    // script / protocol
	    	    "<\\s*script",
	    	    "javascript\\s*:",

	    	    // 특수문자 (탐지 대상만)
	    	    "<",
	    	    ">",
	    	    "\\\"",      // " 존재
	    	    "\\$",       // $ 존재
	    	    "'",
	    	    ",",
	    	    "\\(",
	    	    "\\)"
	    	}; 

	    StringBuilder regex = new StringBuilder("(?i)(");

	    for (String pattern : xssPatterns) {
	        regex.append(pattern).append("|");
	    }

	    regex.setLength(regex.length() - 1); // 마지막 | 제거
	    regex.append(")");

	    Pattern XSS_PATTERN = Pattern.compile(regex.toString());

	    // XSS 패턴 검출 시 true 리턴
	    return XSS_PATTERN.matcher(input).find();
	}
	
	public static String yamlStrDec(String yamlStr) {
		String newYamlStr = yamlStr;
		newYamlStr = newYamlStr.replaceAll("&quot;", "\"");
		newYamlStr = newYamlStr.replaceAll("&apos;", "'");
		newYamlStr = newYamlStr.replaceAll("&lt;", "<");
		newYamlStr = newYamlStr.replaceAll("&gt;", ">");
		newYamlStr = newYamlStr.replaceAll("&amp;", "&");
		return newYamlStr; 
	}
	
}
