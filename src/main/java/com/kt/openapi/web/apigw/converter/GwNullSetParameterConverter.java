package com.kt.openapi.web.apigw.converter;

import com.kt.openapi.web.apigw.constants.GwConstants;
import com.kt.openapi.web.apigw.entity.api.manager.ApiParameter;
import com.kt.openapi.web.apigw.exception.ConversionException;
import com.kt.openapi.web.apigw.utils.AbstractDataConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class GwNullSetParameterConverter extends AbstractDataConverter<List<ApiParameter>, List<String>> {
    @Override
    protected List<String> createTarget() {
        return new ArrayList<>();
    }

    @Override
    public List<String> convert(List<ApiParameter> source, List<String> target) throws ConversionException {
        for (ApiParameter parameter : source) {
            this.addHidden(GwConstants.CP_API.REQUEST, parameter, target);
        }
        return target;
    }

    private void addHidden(final String lineage, ApiParameter parameter, List<String> target) {
        StringBuilder depth = new StringBuilder();
        if (!StringUtils.isBlank(lineage)) {
            depth.append(lineage).append(GwConstants.API_PARAMETER_DEPTH_SEPARATOR);
        }
        depth.append(parameter.getName());
        if (parameter.isHidden()) {
            target.add(depth.toString());
        } else {
            if (!CollectionUtils.isEmpty(parameter.getChildren())) {
                for (ApiParameter child : parameter.getChildren()) {
                    this.addHidden(depth.toString(), child, target);
                }
            }
        }

    }
}
