package com.kt.openapi.web.apigw.entity.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class GwApiRequest implements Serializable {
	@Serial
	private static final long serialVersionUID = -637081569636973368L;

    @JsonProperty("stageid")
    private String stageId = "authStage";
}
