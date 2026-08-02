package com.kt.openapi.web.apigw.entity.api;

import jakarta.validation.constraints.NotEmpty;

import java.io.Serial;
import java.io.Serializable;

public class GwKosCommonHeader implements Serializable {
	@Serial
	private static final long serialVersionUID = -1029634632974904321L;

    @NotEmpty(message = "[appName] field is required form KOS Soap")
    private String appName;
    @NotEmpty(message = "[svcName] field is required form KOS Soap")
    private String svcName;
    @NotEmpty(message = "[fnName] field is required form KOS Soap")
    private String fnName;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getSvcName() {
        return svcName;
    }

    public void setSvcName(String svcName) {
        this.svcName = svcName;
    }

    public String getFnName() {
        return fnName;
    }

    public void setFnName(String fnName) {
        this.fnName = fnName;
    }

}
