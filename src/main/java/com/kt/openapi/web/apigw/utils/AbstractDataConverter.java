package com.kt.openapi.web.apigw.utils;

import com.kt.openapi.web.apigw.exception.ConversionException;

public abstract class AbstractDataConverter<Source, Target> implements DataConverter<Source, Target> {
    @Override
    public Target convert(Source source) throws ConversionException {
        return convert(source, createTarget());
    }

    protected abstract Target createTarget();
}
