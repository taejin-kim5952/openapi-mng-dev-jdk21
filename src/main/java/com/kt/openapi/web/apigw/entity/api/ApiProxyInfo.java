//-- [tag:SR-20210222][i][not used]
package com.kt.openapi.web.apigw.entity.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class ApiProxyInfo implements Serializable {
	@Serial
	private static final long serialVersionUID = -5227662743595399582L;
    @JsonProperty("endpoint-id")
    private String endpointId;

    public String getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }
}
