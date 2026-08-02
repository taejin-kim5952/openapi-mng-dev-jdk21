package com.kt.openapi.web.apigw.entity.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.openapi.web.apigw.type.URLScheme;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpMethod;

import java.io.Serial;
import java.io.Serializable;

public class EndpointConfig implements Serializable {
	@Serial
	private static final long serialVersionUID = 6254162914531773912L;

    public static final String JSON_PREFIX = "commonHttpHttps-";

    @NotEmpty(message = "Endpoint URL should not be empty")
    @JsonProperty(JSON_PREFIX + "url")
    private String url;

    @NotEmpty(message = "Endpoint system name should not be empty")
    @JsonProperty(JSON_PREFIX + "system")
    private String system;

    @NotNull(message = "Endpoint method is required")
    @JsonProperty(JSON_PREFIX + "method")
    private HttpMethod method;

    @JsonProperty(JSON_PREFIX + "protocol")
    @NotNull(message = "Endpoint protocol is required")
    private URLScheme protocol = URLScheme.HTTP;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public URLScheme getProtocol() {
        return protocol;
    }

    public void setProtocol(URLScheme protocol) {
        this.protocol = protocol;
    }
}
