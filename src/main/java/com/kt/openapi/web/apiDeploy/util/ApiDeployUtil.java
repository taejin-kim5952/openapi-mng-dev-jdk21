package com.kt.openapi.web.apiDeploy.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiDeployUtil {

	private static final Logger LOG = LoggerFactory.getLogger(ApiDeployUtil.class);
	
	
	/**
	* <pre>
	* 1. 메소드명 : mvPrivacyMask
	* 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	* 3. 작성자 : tt
	* 4. 설명 : 개인정보 마스킹 처리  
	* </pre>
	*/
	public static String mvPrivacyMask(String str, int splitCnt) {
		
		String maskId = null;
		
		int length = str.length();
		length = length - splitCnt;
		
		maskId  = str.substring(0,length);
		maskId  = maskId + "**";
		
		return maskId;
	}
}
