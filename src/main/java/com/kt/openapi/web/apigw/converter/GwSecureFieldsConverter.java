package com.kt.openapi.web.apigw.converter;

import com.kt.openapi.web.apigw.constants.GwConstants;
import com.kt.openapi.web.apigw.entity.api.manager.ApiParameter;
import com.kt.openapi.web.apigw.exception.ConversionException;
import com.kt.openapi.web.apigw.utils.AbstractDataConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class GwSecureFieldsConverter extends AbstractDataConverter<List<ApiParameter>, List<String>> {
    private String prefix;
    @Override
    protected List<String> createTarget() {
        return new ArrayList<>();
    }

    @Override
    public List<String> convert(List<ApiParameter> source, List<String> target) throws ConversionException {
        for (ApiParameter parameter : source) {
            this.addSecured(prefix, parameter, target);
           // 암호화는 kos자체에서 처
        }
        return target;
    }

    private void addSecured(String lineage, ApiParameter parameter, List<String> target) {

        StringBuilder requiredParameter = new StringBuilder();
        if (!StringUtils.isBlank(lineage)) {
            requiredParameter.append(lineage).append(GwConstants.API_PARAMETER_DEPTH_SEPARATOR);
            requiredParameter.append(parameter.getName());
        }

        if (parameter.isPersonalData()) {
            target.add(requiredParameter.toString());
        }

        if (!CollectionUtils.isEmpty(parameter.getChildren())) {
            for (ApiParameter child : parameter.getChildren()) {
                this.addSecured(requiredParameter.toString(), child, target);
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
