package com.kt.openapi.web.apigw.entity.api.manager;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApiResponseEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = 7823581590122127745L;

    private List<ApiParameter> parameters = new ArrayList<>();

    public List<ApiParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ApiParameter> parameters) {
        this.parameters = parameters;
    }
}
