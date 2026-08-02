package com.kt.openapi.web.apigw.converter;

import com.kt.openapi.web.apigw.constants.GwConstants;
import com.kt.openapi.web.apigw.entity.api.manager.ApiParameter;
import com.kt.openapi.web.apigw.exception.ConversionException;
import com.kt.openapi.web.apigw.utils.AbstractDataConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GwMappingRuleConverter extends AbstractDataConverter<List<ApiParameter>, Map<String, String>> {
    private String prefix;
    @Override
    protected Map<String, String> createTarget() {
        return new HashMap<>();
    }

    @Override
    public Map<String, String> convert(List<ApiParameter> source, Map<String, String> target) throws ConversionException {
//        for (ApiParameter parameter: source) {
//            if (!StringUtils.isBlank(parameter.getMappingKey())) {
//                target.put(parameter.getName(), parameter.getMappingKey());
//            }
//        }
        for (ApiParameter parameter : source) {
            this.addMapping(prefix, parameter, target);
        }
        return target;
    }

    private void addMapping(final String lineage, ApiParameter parameter, Map<String, String> target) {
        StringBuilder depth = new StringBuilder();
        if (!StringUtils.isBlank(lineage)) {
            depth.append(lineage).append(GwConstants.API_PARAMETER_DEPTH_SEPARATOR);
        }
        depth.append(parameter.getName());
        if (!StringUtils.isBlank(parameter.getMappingKey())) {
            target.put(depth.toString(), parameter.getMappingKey());
        } else {
            if (!CollectionUtils.isEmpty(parameter.getChildren())) {
                for (ApiParameter child : parameter.getChildren()) {
                    this.addMapping(depth.toString(), child, target);
                }
            }
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
