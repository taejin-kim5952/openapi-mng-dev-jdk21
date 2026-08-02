package com.kt.openapi.web.apigw.entity.lamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class LampResponseBody implements Serializable {
	@Serial
	private static final long serialVersionUID = -5687747565806740447L;

    // 로그생성시간(yyyy-MM-ddTHH:mm:ss.SSSZ)
    private String timestamp;

    // 서비스코드
    //--[tag:SR-20210214][chg][fix serviceCode -> service]
    @JsonProperty("service")
    private String serviceCode;

    // 오퍼레이션
    private String operation;

    // 비즈니스 거래번호
    private String bizTransactionId;

    // 거래번호
    private String transactionId;

    // 로그유형(IN_REQ, IN_RES, OUT_REQ, OUT_RES)
    private String logType;

    // log payload
    private String payload;

    // 요청정보(channel / channelIp)
    private Map<String, String> caller;

    //--[tag:SR-20210214][add]
    // 호스트정보 (name / ip)
    private Map<String, String> host;

    // 응답정보 (type / code / desc / duration)
    private Map<String, String> response;

    // 사용자 정보 (id / ip / type)
    private Map<String, String> user;

    // 단말정보
    private Map<String, String> device;

    // 목적지정보 (name / ip)
    private Map<String, String> destination;

    // 사용자접속 URL
    private String url;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getBizTransactionId() {
        return bizTransactionId;
    }

    public void setBizTransactionId(String bizTransactionId) {
        this.bizTransactionId = bizTransactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Map<String, String> getCaller() {
        return caller;
    }

    public void setCaller(Map<String, String> caller) {
        this.caller = caller;
    }

    public Map<String, String> getHost() {
        return host;
    }

    public void setHost(Map<String, String> host) {
        this.host = host;
    }

    public Map<String, String> getResponse() {
        return response;
    }

    public void setResponse(Map<String, String> response) {
        this.response = response;
    }

    public Map<String, String> getUser() {
        return user;
    }

    public void setUser(Map<String, String> user) {
        this.user = user;
    }

    public Map<String, String> getDevice() {
        return device;
    }

    public void setDevice(Map<String, String> device) {
        this.device = device;
    }

    public Map<String, String> getDestination() {
        return destination;
    }

    public void setDestination(Map<String, String> destination) {
        this.destination = destination;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
