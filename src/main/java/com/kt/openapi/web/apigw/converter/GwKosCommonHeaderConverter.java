package com.kt.openapi.web.apigw.converter;

import com.kt.openapi.web.apigw.constants.GwConstants;
import com.kt.openapi.web.apigw.entity.api.GwKosCommonHeader;
import com.kt.openapi.web.apigw.entity.api.manager.ApiParameter;
import com.kt.openapi.web.apigw.exception.ConversionException;
import com.kt.openapi.web.apigw.utils.AbstractDataConverter;

import java.util.List;

import static com.kt.openapi.web.apigw.constants.GwConstants.*;

public class GwKosCommonHeaderConverter extends AbstractDataConverter<List<ApiParameter>, GwKosCommonHeader> {
    @Override
    protected GwKosCommonHeader createTarget() {
        return new GwKosCommonHeader();
    }

    @Override
    public GwKosCommonHeader convert(List<ApiParameter> source, GwKosCommonHeader target) throws ConversionException {
        ApiParameter commonHeader = source.stream()
                .filter(parameter -> parameter.getName().equals(REQUEST_KEY.FIXED_PARAM_COMMON_HEADER))
                .findFirst().orElse(null);
        if (commonHeader != null) {
            for (ApiParameter parameter : commonHeader.getChildren()) {
                if (parameter.getName() == null) continue;
                switch (parameter.getName()) {
                    case GwConstants.REQUEST_KEY.FIXED_PARAM_APP_NAME :
                        target.setAppName(parameter.getFixedValue());
                        break;
                    case GwConstants.REQUEST_KEY.FIXED_PARAM_SVC_NAME :
                        target.setSvcName(parameter.getFixedValue());
                        break;
                    case GwConstants.REQUEST_KEY.FIXED_PARAM_FN_NAME:
                        target.setFnName(parameter.getFixedValue());
                        break;
//                    case GwConstants.REQUEST_KEY.FIXED_PARAM_CHANNEL_TYPE:
//                        target.setChnlType(parameter.getFixedValue());
//                        break;
                    default: break; //-- [2023:codeeyes][swtich_default issue]
                }
            }
        } else {
            for (ApiParameter parameter : source) {
                if (parameter.getName() == null) continue;
                switch (parameter.getName()) {
                    case GwConstants.REQUEST_KEY.FIXED_PARAM_APP_NAME :
                        target.setAppName(parameter.getFixedValue());
                        break;
                    case GwConstants.REQUEST_KEY.FIXED_PARAM_SVC_NAME :
                        target.setSvcName(parameter.getFixedValue());
                        break;
                    case GwConstants.REQUEST_KEY.FIXED_PARAM_FN_NAME:
                        target.setFnName(parameter.getFixedValue());
                        break;
                    default: break; //-- [2023:codeeyes][swtich_default issue]
                }
            }

        }

        return target;
    }
}
