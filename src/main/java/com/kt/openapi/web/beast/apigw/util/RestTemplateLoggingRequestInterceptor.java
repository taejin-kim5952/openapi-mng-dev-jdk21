package com.kt.openapi.web.beast.apigw.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

//-- [tag:PRJ-20220901]
//-- [i][clone from com.kt.openapi.web.apigw.utils]
public class RestTemplateLoggingRequestInterceptor implements ClientHttpRequestInterceptor {
    public static final Logger log = LoggerFactory.getLogger(RestTemplateLoggingRequestInterceptor.class);
    private ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        URI uri = httpRequest.getURI();
        HttpMethod method = httpRequest.getMethod();

        this.traceRequest(httpRequest, bytes);
        StopWatch watch = new StopWatch();
        watch.start();
        ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
        watch.stop();
        this.traceResponse(response, uri, method, watch.getTime());
        
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) {
        String json = new String(body, UTF_8);
        String jsonString = json;
        String headerString = request.getHeaders().toString();
        try {
        	if ((null != json) && (json.length() > 0)) {
            	Object object = mapper.readValue(json, Object.class);
            	jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        	}
            headerString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request.getHeaders());
        } catch (IOException e) {
        	log.error("\n\n### {}.{}() [Message: {}][IOException: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
        }
        StringBuilder requestLog = new StringBuilder();
        int max_len = 1000;
        String strBody = ((jsonString.length() > max_len) ? (jsonString.substring(0, max_len) + "...") : jsonString);
        requestLog.append("[REQUEST] ")
	        .append("Uri : ").append(request.getURI())
	        .append("\n, Mehtod : ").append(request.getMethod())
	        .append("\n, Request Headers : \n")
	        .append(headerString)
	        .append("\n, Request Body : \n")
	        .append(strBody);
        log.info(requestLog.toString());
    }

    private void traceResponse(ClientHttpResponse response, URI uri, HttpMethod method, long time) throws IOException {
        String json = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
        String jsonString = json;
        String headerString = response.getHeaders().toString();
        try {
        	if ((null != json) && (json.length() > 0)) {
	            Object object = mapper.readValue(json, Object.class);
    	        jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        	}
            headerString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.getHeaders());
        } catch (IOException e) {
        	log.error("\n\n### {}.{}() [Message: {}][IOException: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
        }
        StringBuilder responseLog = new StringBuilder();
        int max_len = 1000;
        String strBody = ((jsonString.length() > max_len) ? (jsonString.substring(0, max_len) + "...") : jsonString);
        responseLog.append("[RESPONSE] ")
	        .append("Uri : ").append(uri)
	        .append("\n, Method : ").append(method)
	        .append("\n, elapsed time : ").append("%.3f sec".formatted(time / 1000f))
	        .append("\n, Status Code : ").append(response.getStatusCode())
	        .append("\n, Status Text : ").append(response.getStatusText())
	        .append("\n, Response Headers : \n")
	        .append(headerString)
	        .append("\n, Response Body : \n")
	        .append(strBody);
        log.info(responseLog.toString());
    }

}
