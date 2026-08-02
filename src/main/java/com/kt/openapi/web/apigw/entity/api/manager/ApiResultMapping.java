package com.kt.openapi.web.apigw.entity.api.manager;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.openapi.web.apigw.constants.GwConstants;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ApiResultMapping implements Serializable {
	@Serial
	private static final long serialVersionUID = 1313908621375472655L;

    // 응답코드 필드 매핑
    @JsonProperty(GwConstants.RESPONSE_KEY.SUCCESS_CODE)
    private String resultCode;

    // 응답코드 성공 기준 값
    @JsonProperty(GwConstants.RESPONSE_KEY.SUCCESS_VALUE)
    private List<String> successValue;

    // 에러코드 필드
    @JsonProperty(GwConstants.RESPONSE_KEY.ERROR_CODE)
    private String errorCode;

    // 에러 메시지 필드
    @JsonProperty(GwConstants.RESPONSE_KEY.ERROR_MESSAGE)
    private String errorMessage;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public List<String> getSuccessValue() {
        return successValue;
    }

    public void setSuccessValue(List<String> successValue) {
        this.successValue = successValue;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
