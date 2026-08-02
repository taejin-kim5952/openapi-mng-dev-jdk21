package com.kt.openapi.web.apigw.entity;

import com.kt.openapi.web.apigw.type.GWResultType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultEntity<T> implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

    private HttpStatusCode httpStatus;
    private GWResultType result;
    private T response;
    private String errorMessage;

    public ResultEntity(HttpStatusCode httpStatus, T response) {
        this.httpStatus = httpStatus;
        this.result = (httpStatus != null && httpStatus.is2xxSuccessful()) ? GWResultType.OK : GWResultType.FAILURE;
        this.response = response;
    }

    public ResultEntity(GWResultType result, String errorMessage) {
        this.result = result;
        this.errorMessage = errorMessage;
    }
}
