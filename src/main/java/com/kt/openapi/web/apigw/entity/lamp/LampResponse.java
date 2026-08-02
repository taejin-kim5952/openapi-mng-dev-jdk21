package com.kt.openapi.web.apigw.entity.lamp;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class LampResponse implements Serializable {
	@Serial
	private static final long serialVersionUID = -8865449743172946677L;

    // 결과 : SUCCESS / FAIL
    private LampResult status;

    // 결과 메시지
    private String message;

    private List<LampResponseBody> data;

    public enum LampResult {
        SUCCESS,
        FAIL,
        ;
    }

    public LampResult getStatus() {
        return status;
    }

    public void setStatus(LampResult status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<LampResponseBody> getData() {
        return data;
    }

    public void setData(List<LampResponseBody> data) {
        this.data = data;
    }
}
