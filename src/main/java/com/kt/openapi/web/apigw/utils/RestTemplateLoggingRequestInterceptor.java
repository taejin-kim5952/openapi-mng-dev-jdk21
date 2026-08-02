package com.kt.openapi.web.apigw.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;

public class RestTemplateLoggingRequestInterceptor implements ClientHttpRequestInterceptor {
    public static final Logger log = LoggerFactory.getLogger(RestTemplateLoggingRequestInterceptor.class);
    private ObjectMapper mapper = new ObjectMapper();
    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        URI uri = httpRequest.getURI();

        this.traceRequest(httpRequest, bytes);
        StopWatch watch = new StopWatch();
        watch.start();
        ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
        watch.stop();
        this.traceResponse(response, uri, watch.getTime());
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) {
        // test
        String json = new String(body, UTF_8);
        String jsonString = json;
        String headerString = request.getHeaders().toString();
        try {
            Object object = mapper.readValue(json, Object.class);
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            headerString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request.getHeaders());
        } catch (IOException e) {
          //-- [2023:codeeyes][empty_block issue]
        }

        String requestLog = "[REQUEST] " +
                "Uri : " + request.getURI() +
                "\n, Method : " + request.getMethod() +
//                "\n, Headers : " + request.getHeaders().toString() +
                "\n, Headers : \n" + headerString +
                "\n, Request Body : \n" + jsonString;
        log.info(requestLog);
    }

    private void traceResponse(ClientHttpResponse response, URI uri, long time) throws IOException {
        // test
        String json = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
        String jsonString = json;
        try {
            Object object = mapper.readValue(json, Object.class);
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (IOException e) {
          //-- [2023:codeeyes][empty_block issue]
        }
        StringBuilder responseLog = new StringBuilder();
        responseLog.append("[RESPONSE] ")
                .append("Uri : ").append(uri)
                .append("\n, Status code : ").append(response.getStatusCode())
                .append("\n, elapsed time : ").append("%.3f sec".formatted(time / 1000f))
                .append("\n, Response Body : \n")
                .append(jsonString);
        log.info(responseLog.toString());
    }

}
