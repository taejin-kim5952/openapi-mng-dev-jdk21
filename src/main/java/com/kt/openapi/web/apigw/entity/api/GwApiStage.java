package com.kt.openapi.web.apigw.entity.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GwApiStage implements Serializable {
	@Serial
	private static final long serialVersionUID = -2525453128565759683L;

	@JsonProperty("stageid")
    private String id;

    @JsonProperty("stageparam")
    private Map<String, Object> params;

    public GwApiStage() {
    }

    public GwApiStage(String id) {
        this.id = id;
    }

    public GwApiStage(String id, Map<String, Object> params) {
        this.id = id;
        this.params = params;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
