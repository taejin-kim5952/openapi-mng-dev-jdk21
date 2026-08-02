//-- [tag:SR-20210222][i][mpybe_not_used]
package com.kt.openapi.web.apigw.entity.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.openapi.web.apigw.type.InOutFormat;
import com.kt.openapi.web.apigw.type.URLScheme;
import org.springframework.http.HttpMethod;

import java.io.Serial;
import java.io.Serializable;

public class ApiEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = -5683206568782521817L;

    @JsonProperty("apiid")
    private String id;

    private HttpMethod method;
    private int timeout;
    @JsonProperty("threadhold-time")
    private int theadholdTime;

    private URLScheme protocol;

    private String url;

    @JsonProperty("in-format")
    private InOutFormat inFormat;

    @JsonProperty("out-format")
    private InOutFormat outFormat;

    private String devision = "ALONE";
    private String parent;

    @JsonProperty("in-common-param")
    private String inCommonParam = "TYPE_B";

    @JsonProperty("out-common-param")
    private String outCommonParam;
}
