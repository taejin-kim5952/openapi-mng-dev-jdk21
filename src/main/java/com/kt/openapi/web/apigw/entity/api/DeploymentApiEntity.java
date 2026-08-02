package com.kt.openapi.web.apigw.entity.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class DeploymentApiEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = -9044507845261466766L;

    @JsonProperty("apiid")
    private String id;
    
    @JsonProperty("api-version")
    private String version;
    
    public DeploymentApiEntity() {
    }

    public DeploymentApiEntity(String id, String version) {
        this.id = id;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
