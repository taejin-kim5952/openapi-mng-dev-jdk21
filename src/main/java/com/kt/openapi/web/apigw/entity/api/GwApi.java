//-- [tag:PRJ-20220901][i][mpybe_not_used]
package com.kt.openapi.web.apigw.entity.api;

import com.kt.openapi.web.apigw.entity.BaseResult;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;

public class GwApi extends BaseResult {
	@Serial
	private static final long serialVersionUID = -6864933989031145992L;

    @JsonProperty("api-version")
    private String apiVersion;

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
}
