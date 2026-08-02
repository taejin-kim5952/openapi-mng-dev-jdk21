package com.kt.openapi.web.apigw.exception;

import java.io.Serial;

public class ServiceException extends Exception {
	@Serial
	private static final long serialVersionUID = -3459814137483704566L;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }
}
