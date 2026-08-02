package com.kt.openapi.web.adptran.api.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
public class CommonUtil implements InitializingBean {
	private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);

	@Override
	public void afterPropertiesSet() throws Exception {
	  //-- [2023:codeeyes][empty_block issue]
	}

	public static void convertQueryStringToMap(String queryString, Map<String, Object> param) {
		try {
			if (StringUtils.isEmpty(queryString)) {
				return;
			}

			String[] parameters = URLDecoder.decode(queryString, "UTF-8").split("&");

			for (String parameter : parameters) {
				String[] keyValuePair = parameter.split("=");
				if(keyValuePair.length >= 2){
					param.put(keyValuePair[0], keyValuePair[1]);
				}
			}
		} catch(ArrayIndexOutOfBoundsException ae){
			logger.info(logger.getName() + "." + "convertQueryStringToMap" + " ==> " + ae.getClass().getName() + "\n" + " 발생원인: " + "Value 미입력");
		} catch (UnsupportedEncodingException e) {
			logger.info(logger.getName() + "." + "convertQueryStringToMap" + " ==> " + e.getClass().getName() + "\n" + " 발생원인: " + e.getMessage());
		}
	}

	public static boolean isNotEmpty(Object obj) {
		if(obj != null){
			if(obj instanceof String[] strings){
				if(strings.length > 0){
					return true;
				}else{
					return false;
				}
			}else if(obj instanceof List<?> list){
				return !list.isEmpty();
			}else if(obj instanceof String){
				return !"".equals(obj);
			}else{
				return true;
			}
		}else{
			return false;
		}
	}

	public static String getString(Object obj) {
		if(obj == null){
			return null;
		}else{
			return obj.toString();
		}
	}

	public static void exLogging(String method, Exception ex, Logger logger){
		try {
			logger.info(logger.getName() + "." + method + " ==> " + ex.getClass().getName() + "\n" + " 발생원인: " + ex.getMessage());
			if(logger.isDebugEnabled()){
				//-- [2023:codeeyes][empty_block issue]
			}
		} catch (Exception e){
			logger.info("======exLogging Exception======");
		}

	}

	public static String getBase64Encoding(boolean withDateTime, String... seedString) {
		if(withDateTime){
			return new String(Base64.getEncoder().encode((String.join("|", seedString) + new Date().getTime()).getBytes()));
		}else{
			return new String(Base64.getEncoder().encode(String.join("|", seedString).getBytes()));
		}
	}

	public static boolean checkRequiredParam(Map<String, Object> param, String... keyNames){
		for(String keyName : keyNames){
			if(!param.containsKey(keyName) || param.get(keyName) == null || "".equals(param.get(keyName))){
				return false;
			}else{
				//All clear!
				return true;
			}
		}
		return false;
	}
	
	public static boolean isEmailString(String EmailExpression) {
		
		if (EmailExpression == null) return false;
		
		String regExpr = "\\w+[@]\\w+\\.\\w+";
		Pattern p = Pattern.compile(regExpr);
		Matcher m = p.matcher(EmailExpression);
		return m.matches();
		
	}
	
	public static String removeSpChar(String str) {
		
		if (str == null) return "";
		
		String regExpr = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
		str = str.replaceAll(regExpr, " ");
		return str;
		
	}
}
