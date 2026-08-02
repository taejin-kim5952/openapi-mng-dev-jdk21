package com.kt.openapi.web.apigw.entity.api.cp;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class CpApiRequest implements Serializable {
	@Serial
	private static final long serialVersionUID = 1120329469620661688L;

    private String apiUrl;

    // 필수 Header(CP/SP규격서 참조) :
    // authorization(인증키),
    // post인 경우 Content-Type(기본 application/json;charset=utf-8)
    private Map<String, String> headers;

    private String transactionId;
    private String sequenceNo;


    private Map<String, Object> request;

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(String sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public Map<String, Object> getRequest() {
        return request;
    }

    public void setRequest(Map<String, Object> request) {
        this.request = request;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}
