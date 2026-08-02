package com.kt.openapi.web.apigw.entity.api.cp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class CpApiResponse implements Serializable {
	@Serial
	private static final long serialVersionUID = 6982573454349196779L;

    @JsonProperty("transactionid")
    private String transactionId;

    @JsonProperty("sequenceNo")
    private String sequenceno;

    // 결과코드 (0:Fail / 1: Success
    @JsonProperty("returncode")
    private String returnCode;

    // 결과설명
    @JsonProperty("returndescription")
    private String returnDescription;

    // 에러코드(200001: SHUB 자체오류 - 연동/인증/규격 에러, 200002: SHUB 자체오류 - 유효성 체크 에러)
    // 위 두가지 코드를 제외한 나머지는 Enabler 제공에러코드(Enabler 까지 연동된 이후 오류)
    @JsonProperty("errorcode")
    private String errorCode;

    // 에러설명
    @JsonProperty("errordescription")
    private String errorDescription;

    private Map<String, Object> response;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getSequenceno() {
        return sequenceno;
    }

    public void setSequenceno(String sequenceno) {
        this.sequenceno = sequenceno;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnDescription() {
        return returnDescription;
    }

    public void setReturnDescription(String returnDescription) {
        this.returnDescription = returnDescription;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public Map<String, Object> getResponse() {
        return response;
    }

    public void setResponse(Map<String, Object> response) {
        this.response = response;
    }
}
