package com.kt.openapi.web.util;

import com.google.gson.JsonObject;
import com.kt.openapi.web.adptran.util.KsmUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Component
public class ShubRestApiCallFunction {

	private final static Logger LOG = LoggerFactory.getLogger(ShubRestApiCallFunction.class);
	
	//신규 PSSO API연동을 위한 aes256 인코딩된 cilent key
	private static String aesEncodePssoCilentKey;
	@Value("${new.psso.api.clientkey}")
	public void setAesEncodePssoCilentKey(String aesEncodePssoCilentKey) {
		ShubRestApiCallFunction.aesEncodePssoCilentKey = aesEncodePssoCilentKey;
	}	

	//신규 PSSO API연동을 위한 인증키
	private static String restAuthorization;
	@Value("${rest.authorization}")
	public void setRestAuthorization(String restAuthorization) {
		ShubRestApiCallFunction.restAuthorization = restAuthorization;
	}	
	
	//APILink cus-in authTest API
	private static String apilinkCusInAuthTestUrl;
	@Value("${apilink.cus.in.auth.test.url}")
	public void setApilinkCusInAuthTestUrl(String apilinkCusInAuthTestUrl) {
		ShubRestApiCallFunction.apilinkCusInAuthTestUrl = apilinkCusInAuthTestUrl;
	}
	
	//APILink msg-in authTest API
	private static String apilinkMsgInAuthTestUrl;
	@Value("${apilink.msg.in.auth.test.url}")
	public void setApilinkMsgInAuthTestUrl(String apilinkMsgInAuthTestUrl) {
		ShubRestApiCallFunction.apilinkMsgInAuthTestUrl = apilinkMsgInAuthTestUrl;
	}
		
	//shub common API 호출 용도
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static HashMap<String, Object> autoDistributionApiCall(String requestBody, String authorization, String url) {

		LOG.debug("\n### ShubRestApiCallFunction.autoDistributionApiCall : {}",
				"\n[requestBody] " + requestBody + "\n[인증키정보] " + authorization + "\n[연동 url] " + url);

		String input = "{\n" + "\"request\":{\n" + requestBody + "}\n" + "}";
		// 헤더 셋팅
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		headers.add("Authorization", authorization);

		HttpEntity param = new HttpEntity(input, headers);

		RestTemplate restTemplate = new RestTemplate();

		HashMap<String, Object> map = new HashMap<>();
		// -- [tag:SR-20210903][i][trycatch처리]
		try {
			// hash map에 결과를 담아 리턴
			map = restTemplate.postForObject(url, param, HashMap.class);
			LOG.debug("\n### autoDistributionApiCall shub 연동결과: {}", map);
		} catch (RestClientException e) {
			// -- [tag:SR-20201118][sparrow][correction]
			LOG.debug("autoDistributionApiCall(), [RestClientException: {}]", e);
		} catch (Exception e) {
			LOG.debug("autoDistributionApiCall(), [Exception: {}]", e);
		}

		return map;
	}		

	//shub anycommon API 호출 용도
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static HashMap<String, Object> shubAnyCommonApiCall(HashMap<String, Object> headers, String requestBody, String authorization, String url) {

		LOG.debug("\n### ShubRestApiCallFunction.shubAnyCommonApiCall : {}",
				"\n[추가 header 정보] " + headers +"\n[requestBody] " + requestBody + "\n[인증키정보] " + authorization + "\n[연동 url] " + url);

		HttpHeaders header = new HttpHeaders();
		//ContentType과 인증키는 기본 헤더값으로 셋팅
		header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8"))); 
		header.add("Authorization", authorization);
		
		//추가 헤더값이 있을경우에만 headers에 담긴 키와 값을 header에 추가
		if(headers != null) {
			for(String key : headers.keySet()) {						
				header.add(key,KsmUtil.fnSafeStr(headers.get(key)));
			}
		}		
		
		HttpEntity param = new HttpEntity(requestBody, header);

		RestTemplate restTemplate = new RestTemplate();

		HashMap<String, Object> map = new HashMap<>();

		try {
			map = restTemplate.postForObject(url, param, HashMap.class);
			LOG.info("\n### shubAnyCommonApiCall 연동결과: {}", map);
		} catch (RestClientException e) {
			LOG.debug("shubAnyCommonApiCall(), [RestClientException: {}]", e);
		} catch (Exception e) {
			LOG.debug("shubAnyCommonApiCall(), [Exception: {}]", e);
		}

		return map;
	}		
		
	//APILink를 통한 PSSO API 연동 시 사용되는 공통 메서드(요청바디/인증키/연동URL)
	public static HashMap<String, Object> funcForPsso(HashMap<String, String> contents, String url){

		LOG.debug("ShubRestApiCallFunction > funcForPsso 실행");
		
		JsonObject reqBody = new JsonObject();
		JsonObject reqBodyContents = new JsonObject();
		
		reqBodyContents.addProperty("ClientKey", aesEncodePssoCilentKey); //공통 PSSO ClientKey
		
		if(contents != null) {
			for(String key : contents.keySet()) {						
				reqBodyContents.addProperty(key,contents.get(key));
			}
		}	
		
		reqBody.add("request", reqBodyContents);
					
		HashMap<String, Object> resultMap = new HashMap<>();
		
		try {
			resultMap = ShubRestApiCallFunction.shubAnyCommonApiCall(null,reqBody.toString(), restAuthorization, url);														
			LOG.debug("funcForPsso : {}", resultMap);
		} catch (RestClientException e) {
			LOG.debug("funcForPsso 호출 실패, [RestClientException: {}]", e);
		}
							
		@SuppressWarnings("unchecked")
		HashMap<String, Object> responseCont = (HashMap<String, Object>) resultMap.get("response");

		return responseCont;
	}
	

	//APILink를 통한 PSSO API 연동 시 사용되는 공통 메서드(요청바디/인증키/연동URL)
	public static HashMap<String, Object> apiTestFunc(HashMap<String, String> contents, String url){

		LOG.debug("ShubRestApiCallFunction > apiTestFunc 실행");
		
		HttpHeaders header = new HttpHeaders();
		//ContentType과 인증키는 기본 헤더값으로 셋팅
		header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8"))); 
		
		JsonObject reqBody = new JsonObject();
		JsonObject reqBodyContents = new JsonObject();
		
		reqBodyContents.addProperty("ClientKey", aesEncodePssoCilentKey); //공통 PSSO ClientKey
		
		if(contents != null) {
			for(String key : contents.keySet()) {						
				reqBodyContents.addProperty(key,contents.get(key));
			}
		}	
		
		reqBody.add("request", reqBodyContents);
		
		HttpEntity param = new HttpEntity(reqBody.toString(), header);

		RestTemplate restTemplate = new RestTemplate();

		HashMap<String, Object> map = new HashMap<>();

		try {
			map = restTemplate.postForObject(url, param, HashMap.class);
			LOG.info("\n### shubAnyCommonApiCall 연동결과: {}", map);
		} catch (RestClientException e) {
			LOG.debug("shubAnyCommonApiCall(), [RestClientException: {}]", e);
		} catch (Exception e) {
			LOG.debug("shubAnyCommonApiCall(), [Exception: {}]", e);
		}
							
		@SuppressWarnings("unchecked")
		HashMap<String, Object> responseCont = (HashMap<String, Object>) map.get("response");

		return responseCont;
	}
	
	// 관통테스트
	@SuppressWarnings("unchecked")
	public static Map<String, Object> authTest(String target, String apiId) {
		
		RestTemplate restTemplate = new RestTemplate();
		
		String authKey = restAuthorization.split(" ")[1];
		
		Map<String, Object> map = new HashMap<>();
		String targetUrl = null;
		if("cus".equalsIgnoreCase(target)) {
			targetUrl = apilinkCusInAuthTestUrl;
		} else if("msg".equalsIgnoreCase(target)) {
			targetUrl = apilinkMsgInAuthTestUrl;
		}
		
		if(targetUrl != null) {
			String requestUrl = targetUrl + "?authorization=" + authKey + "&apiId=" + apiId; 
			try {
				map = restTemplate.getForObject(requestUrl, Map.class);
				LOG.debug("\n### authTest shub 연동결과: {}", map);
			} catch (RestClientException e) {
				// -- [tag:SR-20201118][sparrow][correction]
				LOG.debug("authTest(), [RestClientException: {}]", e);
			} 
		}
		
		return map;
	}
}
