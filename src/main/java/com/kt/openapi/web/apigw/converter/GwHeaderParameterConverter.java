package com.kt.openapi.web.apigw.converter;

import com.kt.openapi.web.apigw.entity.api.manager.ApiParameter;
import com.kt.openapi.web.apigw.exception.ConversionException;
import com.kt.openapi.web.apigw.utils.AbstractDataConverter;

import java.util.ArrayList;
import java.util.List;

public class GwHeaderParameterConverter extends AbstractDataConverter<List<ApiParameter>, List<String>> {

    @Override
    protected List<String> createTarget() {
        return new ArrayList<>();
    }

    @Override
    public List<String> convert(List<ApiParameter> source, List<String> target) throws ConversionException {
        for (ApiParameter header : source) {
            if (!header.isHidden()) {
                target.add(header.getName());
            }
        }
        return target;
    }
}
