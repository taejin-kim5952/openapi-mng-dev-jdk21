package com.kt.openapi.web.adptran.util;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.adptran.util
 * 2. 타입명 : KsmUtil.java
 * 3. 작성일 : 2018-07-09 16:41:01
 * 4. 작성자 : drm
 * 5. 설명 : adptran작업 일반 공통함수
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

	// -- [drm][for test] {
	public static void printParameter(HttpServletRequest request) {
		Enumeration<?> params = request.getParameterNames();
		LOGGER.info("\t--- request.getParameterNames() ---");
		while (params.hasMoreElements()) {
			String name = (String)params.nextElement();
			LOGGER.info("\t[" + name + "]:[" + request.getParameter(name) + "]");
		}
		LOGGER.info("\t-----------------------------------");
	}
	// -- [drm][for test] }

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
		String fmt_value = value;
		int mask_len;
		if (true == "fmt_version_in_path".equalsIgnoreCase(tag)) {
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
		else if (true == "fmt_ip4_cc_range_regexp".equalsIgnoreCase(tag)) {	//-- 1.2.3.0~1.2.3.255 => 1.2.3[0-255]	
			fmt_value = value;
			final String regex = "^\\s*(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.)(\\d{1,3})\\s*[~|-]\\s*(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.)(\\d{1,3})\\s*$";
			Pattern pattern = Pattern.compile(regex);
			Matcher match = pattern.matcher(StringUtils.deleteWhitespace(value));
			if (match.find() == true) {
				//-- e.g. 1.2.3.0~1.2.3.255
				//-- group1: 1.2.3., group2: 0, group3: 1.2.3., group4: 255
				String ccIp4_from = match.group(1);
				String ip_from = match.group(2);
				String ccIp4_to = match.group(3);
				String ip_to = match.group(4);
				if (ccIp4_from.equals(ccIp4_to)) {
					fmt_value = "%s[%s-%s]".formatted(ccIp4_from, ip_from, ip_to);
				}
			}
		}
		else if (true == "fmt_mask_id".equalsIgnoreCase(tag)) {	//-- not used yet
			mask_len = 3;
			if (value.length() > mask_len) {
				int pos = value.length() - mask_len;
				fmt_value = value.substring(0, pos) + value.substring(pos).replaceAll(".", "*");
			}
		}
		else if (true == "fmt_mask_name".equalsIgnoreCase(tag)) {	//-- not used yet
			mask_len = 1;
			if (value.length() > mask_len) {
				int pos = value.length() - mask_len;
				fmt_value = value.substring(0, pos) + value.substring(pos).replaceAll(".", "*");
			}
		}
		else if (true == "fmt_mask_phoneno".equalsIgnoreCase(tag)) {	//-- not used yet
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
		return fmt_value;
	}
	
	//-- url의 유효여부 // http(s):uname:pass@//domain.com:1234/xxx check
	public static boolean isValudUrl(String url) {
		final String regex = "^(http[s]?)\\:\\/\\/(\\w+:{0,1}\\w*@)?([\\w-]+(\\.[\\w-]+)+)+(:([0-9]+))?(\\/|\\/([\\w#!:.?+=&%@!\\-\\/])+)$";
		return url.matches(regex);
	}

	//-- url의 부분 group
	//-- e.g. https://user:pass@www.abc.com:1234/dir/file#123
	//-- group 0: https://www.abc.com:1234/dir/file#123
	//-- group 1: https
	//-- group 2: user:pass@
	//-- group 3: www.abc.com
	//-- group 4: .com
	//-- group 5: :1234
	//-- group 6: 1234
	//-- group 7: /dir/file#123
	//-- group 8: 3
	public static String fmt_parseUrl(String url, String cmd) {
		String s_ret = "";
		final String regex = "^(http[s]?)\\:\\/\\/(\\w+:{0,1}\\w*@)?([\\w-]+(\\.[\\w-]+)+)+(:([0-9]+))?(\\/|\\/([\\w#!:.?+=&%@!\\-\\/])+)$";
		Pattern pattern = Pattern.compile(regex);
		Matcher match = pattern.matcher(url);
		if (match.find() == true) {
			if ("protocol".equals(cmd) == true) { s_ret = match.group(1); }
			else if ("auth".equals(cmd) == true) { s_ret = match.group(2); }
			else if ("domain".equals(cmd) == true) { s_ret = match.group(3); }
			else if ("port".equals(cmd) == true) { s_ret = match.group(6); }
			else if ("path".equals(cmd) == true) { s_ret = match.group(7); }
		}
		return fnSafeStr(s_ret);
	}
	
	public static Object fmt_dec_HTMLTagFilter(Object value) {
		Object fmt_value = value;
		if ((null != value) && (value instanceof String)) {
			String str_value = value.toString();
			if (str_value.indexOf("&") != -1) {
				str_value = str_value.replaceAll("&#34;", "\"");
				str_value = str_value.replaceAll("&#39;", "\'");
				str_value = str_value.replaceAll("&quot;", "\"");
				str_value = str_value.replaceAll("&apos;", "\'");
				str_value = str_value.replaceAll("&gt;", ">");
				str_value = str_value.replaceAll("&lt;", "<");
				str_value = str_value.replaceAll("&amp;", "&");
				fmt_value = str_value;
			}
		}
		return fmt_value;
	}

	public static Object fmt_dec_HTMLTagFilter_vo(Object vo) {
		Object obj = vo;
		for (Field field : obj.getClass().getDeclaredFields()){
			field.setAccessible(true);
			final Class<?> type = field.getType();
			if (type.equals(String.class)) {
				try {
					field.set(obj, fmt_dec_HTMLTagFilter(field.get(obj)));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					LOGGER.error("KsmUtil fmt_dec_HTMLTagFilter_vo() :: [field: {}][e: {}]", field, e);
				}
			}
		}
		return obj;
	}
	
	public static String md5(String input) throws NoSuchAlgorithmException {
		String toReturn = null;
		try {
		    MessageDigest digest = MessageDigest.getInstance("MD5");
		    digest.reset();
		    digest.update(input.getBytes("utf8"));
		    toReturn = "%032x".formatted(new BigInteger(1, digest.digest()));
		} catch (Exception e) {
		    LOGGER.error("KsmUtil md5() :: [input: {}][e: {}]", input, e);
		}
		return toReturn;
	}

	public static String sha256(String input)  throws NoSuchAlgorithmException {
		String toReturn = null;
		try {
		    MessageDigest digest = MessageDigest.getInstance("SHA-256");
		    digest.reset();
		    digest.update(input.getBytes("utf8"));
		    toReturn = "%064x".formatted(new BigInteger(1, digest.digest()));
		} catch (Exception e) {
		    LOGGER.error("KsmUtil sha256() :: [input: {}][e: {}]", input, e);
		}
		return toReturn;
	}

	public static String sha512(String input)  throws NoSuchAlgorithmException {
		String toReturn = null;
		try {
		    MessageDigest digest = MessageDigest.getInstance("SHA-512");
		    digest.reset();
		    digest.update(input.getBytes("utf8"));
		    toReturn = "%0128x".formatted(new BigInteger(1, digest.digest()));
		} catch (Exception e) {
		    LOGGER.error("KsmUtil sha512() :: [input: {}][e: {}]", input, e);
		}
		return toReturn;
	}
	
	//-- [tag:SR-20210515]
	public static String fnGetExtProp(String propKey, String extProp) {
		String propVal = null;
		propKey = KsmUtil.fnSafeStr(propKey).trim();
		extProp = KsmUtil.fnSafeStr(extProp);
		String[] arrExtProp = extProp.split("\n");
		
		for (int n_ii = 0; n_ii < arrExtProp.length; n_ii++) {
			String propLine = arrExtProp[n_ii].trim();
			String[] arrPropLine = propLine.split("=", 2);
			if ((arrPropLine.length == 2) && (true == propKey.equalsIgnoreCase(arrPropLine[0].trim()))) {
				propVal = arrPropLine[1];
				break;
			}
		}

		return propVal;
	}
}
