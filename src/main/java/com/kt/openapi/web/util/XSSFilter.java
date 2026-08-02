package com.kt.openapi.web.util;

public class XSSFilter{

	/**
	* <pre>
	* 1. 메소드명 : toText
	* 2. 작성일 : 2018. 1. 11. 오후 7:30:20
	* 3. 작성자 : user
	* 4. 설명 :디비 저장시 특수문자에대한 치환 처리
	* </pre>
	* @param str
	* @return
	*/
	public static String toText(String str) {
		if(str == null) return null;
		String value = str;
	    value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
        value = value.replaceAll("'", "&#39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        return value;
	}
	
	
	/**
	* <pre>
	* 1. 메소드명 : fromXssText
	* 2. 작성일 : 2018. 1. 11. 오후 7:29:45
	* 3. 작성자 : user
	* 4. 설명 : INPUT 박스나 TEXTAREA 에 노출하는 데이터에 대한 치환된 특수문자에 대한 원복 처리
	* </pre>
	* @param str
	* @return
	*/
	public static String fromXssText(String str) {
		if(str == null) return null;
		String returnStr = str;
		returnStr = returnStr.replaceAll("<br>", "\n");
		returnStr = returnStr.replaceAll("&gt;", ">");
		returnStr = returnStr.replaceAll("&lt;", "<");
		returnStr = returnStr.replaceAll("&quot;", "\"");
		returnStr = returnStr.replaceAll("&nbsp;", " ");
		returnStr = returnStr.replaceAll("&amp;", "&");
		returnStr = returnStr.replaceAll("/(?:\r\n|\r|\n)/g", "<br/>");
		returnStr = returnStr.replaceAll("&#40;", "\\("); 
		returnStr = returnStr.replaceAll("&#41;", "\\)"); 
		returnStr = returnStr.replaceAll("&#39;","'"); 
		returnStr = returnStr.replaceAll("&apos;","'"); 

		return returnStr;
	}
	
	
	/**
	 * Description : html을 제거하여 문자열 리턴
	 * Date        : 2013.12.19
	 * @param htmlStr html이 포함된 문자열
	 * @return String 문자열
	 */
	public static String getHtmlDelStr(String htmlStr) {
		String regex1 = "\\<.*?\\>";
		String regex2 = "\\r\\n";			// new line
		String regex3 = "\\t";				// tab
		//String regex2 = "<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>";
		
		try{
			htmlStr = htmlStr.replaceAll(regex1, "");
			htmlStr = htmlStr.replaceAll(regex2, " ");
			htmlStr = htmlStr.replaceAll(regex3, " ");
		}catch(Exception e){
			e.getMessage();
		}
		
		return htmlStr;
	}
	
	/**
	 * nbsp로 시작되는것 자르기
	 * @param str
	 * @return
	 */
	public static String nbspCut(String str){
		if(str == null){
			return "";
		}else{
			int intC = 160;
			char c = (char)intC;
			Character ch = Character.valueOf(c);
			
			// 이상한 공백문자 제거
			while(str.startsWith(ch.toString())){
				str = str.substring(1);
				str = str.trim();
			}
			
			// nbsp 제거
			while(str.startsWith("&nbsp;")){
				str = str.substring(6);
				str = str.trim();
			}
			
			return str;
		}
	}
}
