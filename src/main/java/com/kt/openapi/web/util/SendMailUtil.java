package com.kt.openapi.web.util;

import java.nio.charset.Charset;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.kt.openapi.web.Entity.BaseResponse;


@Component
public class SendMailUtil {
	// mail Rest api
	@Value("${apiLink.host.url}")
	private String restUrl; 
	
	@Value("${apilink.mail.key}")
	private String mailKey; 
	
	private final static Logger LOG = LoggerFactory.getLogger("SendMailUtil.class");
	
	public void sendMailcall(Map<String,String> mailMap) {
		String input = mailJsonMake(mailMap);

		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
	    HttpEntity param= new HttpEntity(input, headers);
	    LOG.debug("restUrl: {}", restUrl);
	    RestTemplate restTemplate = new RestTemplate();
	    
	    BaseResponse baseResponse = restTemplate.postForObject(restUrl, param, BaseResponse.class);
	    LOG.debug("메일발송: {}", baseResponse.toString());
	}
	public String mailJsonMake(Map<String,String> mailMap) {
		StringBuffer sb = new StringBuffer();
		sb.append("{\n");
		sb.append("\"templateId\":\""+mailMap.get("tempId")+"\",\n");//100000014
		sb.append("\"inData\":{\n");
		sb.append("\"mailKey\":\"" + mailKey.substring(0, 20) + "\",\n");
		sb.append("\"title\":\""+mailMap.get("title")+"\",\n");//API Link 권한 신청 알림메일
		sb.append("\"contents\":\""+mailMap.get("content")+"\",\n");
		sb.append("\"toMail\":\""+mailMap.get("toMail")+"\"\n");
		sb.append("}\n");
	    sb.append("}");
		return sb.toString(); 
	}
	/*public static void main(String ars[]) {
		Map<String,String> map = new HashMap<>();
		map.put("tempId", "100000014");
		map.put("title", "API Link 권한 신청 알림메일1111");
		map.put("content", "권한 신청이 요청 되었습니다.");
		map.put("toMail", "roseyoung@gmail.com");
		sendMailcall(map);
	}*/
}
