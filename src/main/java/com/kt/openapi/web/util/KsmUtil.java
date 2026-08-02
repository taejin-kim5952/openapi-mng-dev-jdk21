package com.kt.openapi.web.util;

import jakarta.servlet.http.HttpServletRequest;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.util
 * 2. 타입명 : KsmUtil.java
 * 3. 작성일 : 2018-05-09 10:52:25
 * 4. 작성자 : drm
 * 5. 설명 : 공통함수
 * </pre>
 */
public class KsmUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(KsmUtil.class);

	/**
	 * <pre>
	 * 1. 메소드명 : fnSafeStr
	 * 2. 작성일 : 2018-05-09 11:29:47
	 * 3. 작성자 : drm
	 * 4. 설명 : 안전문자열전환
	 * </pre>
	 * 
	 * @param str : Object
	 * @return
	 */
	public static String fnSafeStr(Object obj) {
		//--##if (null == obj) { LOGGER.warn("KsmUtil fnSafeStr() :: [obj: {}]", obj); }
		return ((null == obj) ? "" : obj.toString());
	}

	public static String fnSafeStr(String str) {
		//--##if (null == str) { LOGGER.warn("KsmUtil fnSafeStr() :: [str: {}]", str); }
		return ((null == str) ? "" : str);
	}

	/**
	 * <pre>
	 * 1. 메소드명 : fnSafeObj
	 * 2. 작성일 : 2018-05-09 11:31:07
	 * 3. 작성자 : drm
	 * 4. 설명 : 안전object처리(object null일시 기본값 object 리턴)
	 * </pre>
	 * 
	 * @param obj : 점검대상 object
	 * @param defObj : 기본값 object
	 * @return
	 */
	public static Object fnSafeObj(Object obj, Object defObj) {
		if (null == obj) {
			LOGGER.warn("KsmUtil fnSafeObj() :: [obj: {}][defObj: {}]", obj, defObj);
		}
		return ((null == obj) ? defObj : obj);
	}

	/**
	 * <pre>
	 * 1. 메소드명 : parseInt
	 * 2. 작성일 : 2018-05-09 11:32:06
	 * 3. 작성자 : drm
	 * 4. 설명 : 문자열->integer (null,오류시 기본값)
	 * </pre>
	 * 
	 * @param val
	 * @param defVal
	 * @return
	 */
	public static int parseInt(Object obj, int defVal) {
		int retVal = defVal;
		try {
			retVal = Integer.parseInt(fnSafeStr(obj));
		} catch (NumberFormatException e) {
			LOGGER.warn("KsmUtil parseInt() :: [obj: {}][defVal: {}]", obj, defVal);
		}
		return retVal;
	}

	public static String getRemoteAddr(HttpServletRequest request) {
        String ip = null;

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	        ip = request.getHeader("X-Forwarded-For");
			if (ip != null) {
	        	LOGGER.info("KsmUtil getRemoteAddr() :: X-Forwarded-For :: {}" , ip);
			}
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("Proxy-Client-IP");
            if (ip != null) {
            	LOGGER.info("KsmUtil getRemoteAddr() :: Proxy-Client-IP :: {}" , ip);
            }
        } 
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("WL-Proxy-Client-IP"); 
            if (ip != null) {
	            LOGGER.info("KsmUtil getRemoteAddr() :: WL-Proxy-Client-IP :: {}" , ip);
            }
        } 
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("HTTP_CLIENT_IP"); 
            if (ip != null) {
	            LOGGER.info("KsmUtil getRemoteAddr() :: HTTP_CLIENT_IP :: {}" , ip);
            }
        } 
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("HTTP_X_FORWARDED_FOR"); 
            if (ip != null) {
	            LOGGER.info("KsmUtil getRemoteAddr() :: HTTP_X_FORWARDED_FOR :: {}" , ip);
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("X-Real-IP");
            if (ip != null) {
	            LOGGER.info("KsmUtil getRemoteAddr() :: X-Real-IP :: {}" , ip);
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("X-RealIP"); 
            if (ip != null) {
	            LOGGER.info("KsmUtil getRemoteAddr() :: X-RealIP :: {}" , ip);
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("REMOTE_ADDR");
            if (ip != null) {
	            LOGGER.info("KsmUtil getRemoteAddr() :: REMOTE_ADDR :: {}" , ip);
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getRemoteAddr(); 
            if (ip != null) {
	            LOGGER.info("KsmUtil getRemoteAddr() :: request.getRemoteAddr :: {}" , ip);
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            LOGGER.info("KsmUtil getRemoteAddr() :: FAIL :: {}" , ip);
        }
        return ip;
    }

	/**
	 * <pre>
	 * 1. 메소드명 : fmt_data
	 * 2. 작성일 : 2018-12-31 15:48:01
	 * 3. 작성자 : senasa
	 * 4. 설명 : 문자열의 형식을 변환
	 * </pre>
	 * @param value
	 * @param tag
	 * @return
	 */
	public static String fmt_data(String value, String tag) {
		//-- [tag:SR-20201118][sparrow][correction]
		value = fnSafeStr(value);
		String fmt_value = value;
		int mask_len;
		if (true == "fmt_version_in_path".contentEquals(tag)) {
			//-- e.g. /kos/v1.9/abc/123
			//-- group1: /kos, group2: v1.9, group3: /abc/123
			fmt_value = "";
			final String regex = "^(\\/[\\w-.]+)\\/(v\\d+.\\d+)(\\/[\\w-./]+)$";
			Pattern pattern = Pattern.compile(regex);
			Matcher match = pattern.matcher(value);
			if (match.find() == true) {
				fmt_value = match.group(2);
			}
		}
		else if (true == "fmt_xss_filter".contentEquals(tag)) {
			//-- [tag:SR-20201118][sparrow][correction]
		    value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	        value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
	        value = value.replaceAll("'", "&#39;");
	        value = value.replaceAll("eval\\((.*)\\)", "");
	        value = value.replaceAll("document.cookie", "");
	        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
	        value = value.replaceAll("script", "");
	        fmt_value = value;
		}
		else if (true == "fmt_mask_id".contentEquals(tag)) {	//-- not used yet
			mask_len = 3;
			if (value.length() > mask_len) {
				int pos = value.length() - mask_len;
				fmt_value = value.substring(0, pos) + value.substring(pos).replaceAll(".", "*");
			}
		}
		else if (true == "fmt_mask_name".contentEquals(tag)) {	//-- not used yet
			mask_len = 1;
			if (value.length() > mask_len) {
				int pos = value.length() - mask_len;
				fmt_value = value.substring(0, pos) + value.substring(pos).replaceAll(".", "*");
			}
		}
		else if (true == "fmt_mask_phoneno".contentEquals(tag)) {	//-- not used yet
			mask_len = 3;
			value = value.replaceAll("[^0-9]", "");
			if (value.length() == 10) {
				fmt_value = value.substring(0, 4) + "***" + value.substring(7); 
			}
			else if (value.length() == 11) {
				fmt_value = value.substring(0, 5) + "***" + value.substring(8); 
			}
			else if (value.length() > mask_len) {
				int pos = value.length() - mask_len;
				fmt_value = value.substring(0, pos) + value.substring(pos).replace(".", "*");
			}
		}
		else if (true == "fmt_htmlenc".contentEquals(tag)) {
			value = value.replaceAll("&", "&amp;");
			value = value.replaceAll("<", "&lt;");
			value = value.replaceAll(">", "&gt;");
			value = value.replaceAll("\"", "&quot;");
			value = value.replaceAll("'", "&#39;");
		}
		else if (true == "fmt_js".contentEquals(tag)) {
			value = value.replaceAll("&", "&amp;");
			value = value.replaceAll("<", "&lt;");
			value = value.replaceAll(">", "&gt;");
			value = value.replaceAll("\"", "&quot;");
			value = value.replaceAll("'", "\\'");
		}
		return fmt_value;
	}
	
	// 20250731 CodeScanning
//	//-- url의 유효여부 // http(s):uname:pass@//domain.com:1234/xxx check
//	public static boolean isUrl(String url) {
//		final String regex = "^(http[s]?)\\:\\/\\/(\\w+:{0,1}\\w*@)?([\\w-]+(\\.[\\w-]+)+)+(:([0-9]+))?(\\/|\\/([\\w#!:.?+=&%@!\\-\\/])+)$";
//		return url.matches(regex);
//	}

	// 20250731 CodeScanning
//	//-- url의 부분 group
//	//-- e.g. https://user:pass@www.abc.com:1234/dir/file#123
//	//-- group 0: https://www.abc.com:1234/dir/file#123
//	//-- group 1: https
//	//-- group 2: user:pass@
//	//-- group 3: www.abc.com
//	//-- group 4: .com
//	//-- group 5: :1234
//	//-- group 6: 1234
//	//-- group 7: /dir/file#123
//	//-- group 8: 3
//	public static String fmt_parseUrl(String url, String cmd) {
//		String s_ret = "";
//		final String regex = "^(http[s]?)\\:\\/\\/(\\w+:{0,1}\\w*@)?([\\w-]+(\\.[\\w-]+)+)+(:([0-9]+))?(\\/|\\/([\\w#!:.?+=&%@!\\-\\/])+)$";
//		Pattern pattern = Pattern.compile(regex);
//		Matcher match = pattern.matcher(url);
//		if (match.find() == true) {
//			if ("protocol".equals(cmd) == true) { s_ret = match.group(1); }
//			else if ("auth".equals(cmd) == true) { s_ret = match.group(2); }
//			else if ("domain".equals(cmd) == true) { s_ret = match.group(3); }
//			else if ("port".equals(cmd) == true) { s_ret = match.group(6); }
//			else if ("path".equals(cmd) == true) { s_ret = match.group(7); }
//		}
//		return fnSafeStr(s_ret);
//	}
	
	public static String md5(String input) throws NoSuchAlgorithmException {
		String toReturn = null;
		try {
		    MessageDigest digest = MessageDigest.getInstance("MD5");
		    digest.reset();
		    digest.update(input.getBytes("utf8"));
		    toReturn = "%032x".formatted(new BigInteger(1, digest.digest()));
		} catch (NoSuchAlgorithmException e) {
			//-- [tag:SR-20201118][sparrow][correction]
			LOGGER.debug("\n\n### KsmUtil.md5() [NoSuchAlgorithmException: {}] ###\n", e);
		} catch (UnsupportedEncodingException e) {
			//-- [tag:SR-20201118][sparrow][correction]
			LOGGER.debug("\n\n### KsmUtil.md5() [UnsupportedEncodingException: {}] ###\n", e);
		}
		return toReturn;
	}

	public static String sha256(String input) {
		String toReturn = null;
		try {
		    MessageDigest digest = MessageDigest.getInstance("SHA-256");
		    digest.reset();
		    digest.update(input.getBytes("utf8"));
		    toReturn = "%064x".formatted(new BigInteger(1, digest.digest()));
		} catch (NoSuchAlgorithmException e) {
			//-- [tag:SR-20201118][sparrow][correction]
			LOGGER.debug("\n\n### KsmUtil.sha256() [NoSuchAlgorithmException: {}] ###\n", e);
		} catch (UnsupportedEncodingException e) {
			//-- [tag:SR-20201118][sparrow][correction]
			LOGGER.debug("\n\n### KsmUtil.sha256() [UnsupportedEncodingException: {}] ###\n", e);
		}
		return toReturn;
	}

	public static String sha512(String input) {
		String toReturn = null;
		try {
		    MessageDigest digest = MessageDigest.getInstance("SHA-512");
		    digest.reset();
		    digest.update(input.getBytes("utf8"));
		    toReturn = "%0128x".formatted(new BigInteger(1, digest.digest()));
		} catch (NoSuchAlgorithmException e) {
			//-- [tag:SR-20201118][sparrow][correction]
			LOGGER.debug("\n\n### KsmUtil.sha512() [NoSuchAlgorithmException: {}] ###\n", e);
		} catch (UnsupportedEncodingException e) {
			//-- [tag:SR-20201118][sparrow][correction]
			LOGGER.debug("\n\n### KsmUtil.sha512() [UnsupportedEncodingException: {}] ###\n", e);
		}
		return toReturn;
	}

  //-- [tag:SR-20230808][add] {
  public static JSONObject sfJsonParse(String json) {
    JSONObject jo = new JSONObject();
    try {
      jo = JSONObject.fromObject(json);
    } catch (JSONException e) {
      LOGGER.error("\n\n### KsmUtil.sfJsonParse() [JSONException: {}] ###\n", e);
    }
    return jo;
  }

  public static String sfJsonGetString(JSONObject jso, String key, String defVal) {
    if (!(jso instanceof JSONObject) || !jso.has(key)) { return defVal; }
    Object value = jso.get(key);
    return ((null != value) ? value.toString() : defVal);
  }

  public static Object sfJsonGetType(JSONObject jso, String key, Class<?> valueType, Object defVal) {
    Object value = (KsmUtil.isJsonHasKeyValue(jso, key, valueType) ? jso.get(key) : defVal);
    return value;
  }

  public static boolean isJsonHasKeyValue(JSONObject jso, String key, Class<?> valueType) {
    if (!(jso instanceof JSONObject) || !jso.has(key)) { return false; }
    Object value = jso.get(key);
    return valueType.isInstance(value);
  }
  //-- [tag:SR-20230808][add] }

	//-- [drm][for test] {
	public static void printParameter(HttpServletRequest request) {
		Enumeration<?> params = request.getParameterNames();
		LOGGER.info("\t--- request.getParameterNames() ---");
		while (params.hasMoreElements()) {
			String name = (String)params.nextElement();
			LOGGER.info("\t[" + name + "]:[" + request.getParameter(name) + "]");
		}
		LOGGER.info("\t-----------------------------------");
	}
	//-- [drm][for test] }
}
