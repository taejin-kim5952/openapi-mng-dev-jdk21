package com.kt.openapi.web.apigw.entity.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class GwApiProxy implements Serializable {
    private List<GwApiStage> request;
    private List<GwApiStage> response;
    @JsonProperty("endpoint-id")
    private String endpointId;

    public List<GwApiStage> getRequest() {
        return request;
    }

    public void setRequest(List<GwApiStage> request) {
        this.request = request;
    }

    public List<GwApiStage> getResponse() {
        return response;
    }

    public void setResponse(List<GwApiStage> response) {
        this.response = response;
    }

    public String getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }
}
