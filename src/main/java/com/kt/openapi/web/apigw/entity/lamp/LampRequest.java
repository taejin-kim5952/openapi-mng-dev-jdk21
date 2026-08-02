package com.kt.openapi.web.apigw.entity.lamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class LampRequest implements Serializable {
	@Serial
	private static final long serialVersionUID = -7123916724871468062L;

    @JsonProperty("crtfcSvcCd")
    private String serviceId;

    @JsonProperty("crtfcKey")
    private String key;

    private String searchDate;

    private String transactionId;

    @JsonProperty("svcCd")
    private String serviceCode;

    @JsonProperty("operId")
    private String apiId;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSearchDate() {
        return searchDate;
    }

    public void setSearchDate(String searchDate) {
        this.searchDate = searchDate;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }
}
