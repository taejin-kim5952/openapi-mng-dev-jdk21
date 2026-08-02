package com.kt.openapi.web.apigw.entity.api.manager;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApiRequestEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = -2234185505113947293L;

    private List<ApiParameter> headers = new ArrayList<>();
    private List<ApiParameter> parameters = new ArrayList<>();

    public List<ApiParameter> getHeaders() {
        return headers;
    }

    public void setHeaders(List<ApiParameter> headers) {
        this.headers = headers;
    }

    public List<ApiParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ApiParameter> parameters) {
        this.parameters = parameters;
    }
}
