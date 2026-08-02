package com.kt.openapi.web.apigw.exception;

import java.io.Serial;

public class DeploymentException extends Exception {
	@Serial
	private static final long serialVersionUID = -3459814137483704566L;

    public DeploymentException(String message) {
        super(message);
    }

    public DeploymentException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeploymentException(Throwable cause) {
        super(cause);
    }
}
