package com.kt.openapi.web.apigw.exception;

import java.io.Serial;

public class ConversionException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = -5967531302083160490L;

    public ConversionException(final String message, final Throwable t) {
        super(message, t);
    }

    public ConversionException(final String message) {
        super(message);
    }

    public ConversionException(Throwable t) {
        super(t);
    }
}
