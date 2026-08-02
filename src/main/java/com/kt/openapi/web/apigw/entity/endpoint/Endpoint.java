package com.kt.openapi.web.apigw.entity.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.openapi.web.apigw.entity.BaseResult;

import java.io.Serial;

public class Endpoint extends BaseResult {
	@Serial
	private static final long serialVersionUID = 8857532129490408482L;

    @JsonProperty("commonHttpHttps-config")
    private EndpointConfig config;

    public Endpoint() {
    }

    public Endpoint(String id, EndpointConfig config) {
        this.setId(id);
        this.config = config;
    }

    public EndpointConfig getConfig() {
        return config;
    }

    public void setConfig(EndpointConfig config) {
        this.config = config;
    }
}
