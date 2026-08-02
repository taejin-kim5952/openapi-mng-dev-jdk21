package com.kt.openapi.web.util;

import com.jcabi.aspects.RetryOnFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
* 1. 패키지명 : com.kt.openapi.mng.schduler.util
* 2. 타입명 : JsonInfoUtil.java
* 3. 작성일 : 2017. 12. 6. 오후 4:22:47
* 4. 작성자 : user
* 5. 설명 : url 로부터 전해 받은 json 정보 parser
 * </pre>
 */
@Component
public class JsonInfoUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonInfoUtil.class);

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}


	public static Map<String, Object> readJsonFromUrl(String urlStr) throws KeyManagementException, NoSuchAlgorithmException, IOException  {
		int defaultRetry = 1;
		
		return readJsonFromUrl(urlStr, defaultRetry);
	}


//	public static Map<String, Object> readJsonFromUrlBack(String urlStr) throws KeyManagementException, NoSuchAlgorithmException, IOException  {
//		int defaultRetry = 1;
//
//		return readJsonFromUrl(urlStr.replaceAll("\\+", "^"), defaultRetry);
//	}

	/**
	 * <pre>
	* 1. 메소드명 : readJsonFromUrl
	* 2. 작성일 : 2017. 12. 6. 오후 4:22:43
	* 3. 작성자 : user
	* 4. 설명 :
	 * </pre>
	 * 
	 * @param urlStr
	 * @param retryReq : 재실행 횟수
	 * @return
	 * @throws KeyManagementException 
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 * @throws JSONException
	 */
	public static Map<String, Object> readJsonFromUrl(String urlStr, int retryReq)  {
		LOGGER.debug("실행 횟수 체크 !!!!!");
		HttpURLConnection conn = null;
		Map<String, Object> map = new HashMap<>();
		String jsonText = null;
		InputStreamReader in = null;
		BufferedReader br = null;
	    int rescd = -1;
		String resmsg = null;
		boolean isError = false;
		int retryCnt = 0;
		
//		// 호출 반복
//		do {
			retryCnt++;
			isError = false;
			
			LOGGER.debug("---------------------------------------------");
			LOGGER.debug("readJsonFromUrl [URL = %s, isError=%s, retryCnt=%d] ".formatted(urlStr, isError, retryCnt));
			
			try {
				//Git Code scanning 취약점 조치
				/*
				TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(X509Certificate[] certs, String authType) {
					}
					public void checkServerTrusted(X509Certificate[] certs, String authType) {
					}
				} };
				SSLContext sc = SSLContext.getInstance("SSL");
				
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
			    
			    HostnameVerifier allHostsValid = new HostnameVerifier() {
			        public boolean verify(String hostname, SSLSession session) {
			        	if(hostname.isEmpty()) {
							return false;
						} else {
							return true;
						}
						//Sparrow 검사 항상 true 를 반환 host이름이 있을 때 true 반환하도록 수정
						//return true;
			        }
			    };
			    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			    */
				URL url = new URL(urlStr);
				
				conn = load(url);

		    	rescd = conn.getResponseCode();
				resmsg = conn.getResponseMessage();
		    	LOGGER.debug("rescd  : {}" , rescd);
		    	LOGGER.debug("resmsg  : {}" , resmsg);
				
				in = new InputStreamReader((InputStream) conn.getContent(), Charset.forName("UTF-8"));
				br = new BufferedReader(in);
				
				jsonText = readAll(br);
			} catch (MalformedURLException e) {
				LOGGER.error("MalformedURLException error  : {}" , e);
				isError = true;
			} catch (IOException e) {
				LOGGER.error("IOException error  : {}" , e);
				isError = true;
			} catch(Exception e) {
				LOGGER.error("Exception error  : {}" , e);
				isError = true;
			}finally {
				if(br != null) {
					try {
						br.close();
					} catch (IOException e) {
						LOGGER.error("finally IOException error br  : {}" , e);
						isError = true;
					}
				}
				if(in != null) {
					try {
						in.close();
					} catch (IOException e) {
						LOGGER.error("finally IOException error in  : {}" , e);
						isError = true;
					}
				}
				conn.disconnect();
			}
//		} while(isError && retryCnt < retryReq);

		LOGGER.debug("jsonText  : {}" , jsonText);
    	map.put("jsonText", jsonText);
    	map.put("rescd", StringUtil.isNullToString(rescd) );
    	map.put("resmsg", StringUtil.isNullToString(resmsg));
    	LOGGER.debug("map  : {}" , map);
    	
		return map;
	}
	
	@RetryOnFailure(attempts = 3, delay = 10 , verbose = false)
	public static HttpURLConnection load(URL url) throws IOException {
		LOGGER.debug("retry test gogogogogogo ###################################" );
		return (HttpURLConnection) url.openConnection();
	}

/*	public static void main(String[] args) throws IOException, JSONException {
		String date = "20170101";
		String url="https://10.217.24.234:8585/loginfra/tblog/stats/api?"+"startDate="+date+"0000"+"&endDate="+date+"2400"+"&reportType=day&groupType=service";
		 String url  ="http://10.214.188.95:8081/apimng/apimock/shub.json?";
		 String url="https://api.ucloudbiz.olleh.com/loginfra/api/v1/stat/system?startDate="+date+"&endDate="+date;
		String url = "https://api.gigagenie.ai/api/v1/aiportal/serviceSdkApiUsage?" + "queryDate=" + date;

		// https://api.gigagenie.ai/api/v1/aiportal/serviceSdkApiUsage?queryDate=20171213
		Map<String, Object> json = readJsonFromUrl(url);
		LOGGER.debug("json  data: {}", json);
	}*/

}
