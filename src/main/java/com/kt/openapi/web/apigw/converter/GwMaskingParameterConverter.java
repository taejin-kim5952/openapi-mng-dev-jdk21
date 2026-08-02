package com.kt.openapi.web.apigw.converter;

import com.kt.openapi.web.apigw.entity.api.manager.ApiParameter;
import com.kt.openapi.web.apigw.exception.ConversionException;
import com.kt.openapi.web.apigw.utils.AbstractDataConverter;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class GwMaskingParameterConverter extends AbstractDataConverter<List<ApiParameter>, List<String>> {
    @Override
    protected List<String> createTarget() {
        return new ArrayList<>();
    }

    @Override
    public List<String> convert(List<ApiParameter> source, List<String> target) throws ConversionException {
        for (ApiParameter parameter : source) {
            this.addSecured(parameter, target);
        }
        return target;
    }

    private void addSecured(ApiParameter parameter, List<String> target) {
        if (parameter.isPersonalData()) {
            target.add(parameter.getName());
        }

        if (!CollectionUtils.isEmpty(parameter.getChildren())) {
            for (ApiParameter child : parameter.getChildren()) {
                this.addSecured(child, target);
            }
        }
    }
}
