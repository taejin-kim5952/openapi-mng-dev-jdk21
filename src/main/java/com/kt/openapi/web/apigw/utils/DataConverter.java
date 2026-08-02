package com.kt.openapi.web.apigw.utils;

import com.kt.openapi.web.apigw.exception.ConversionException;

public interface DataConverter<Source, Target> {
    Target convert(Source source, Target target) throws ConversionException;
    Target convert(Source source) throws ConversionException;
}
