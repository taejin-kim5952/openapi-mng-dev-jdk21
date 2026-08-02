package com.kt.openapi.web.apigw.converter;

import static com.kt.openapi.web.apigw.constants.GwConstants.API_COMMON_PARAM_TYPE_B;
import static com.kt.openapi.web.apigw.constants.GwConstants.API_DEFAULT_AUTH_STAGE;
import static com.kt.openapi.web.apigw.constants.GwConstants.API_REQUEST_STAGE;
import static com.kt.openapi.web.apigw.constants.GwConstants.API_RESPONSE_STAGE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.kt.openapi.web.apigw.constants.GwConstants;
import com.kt.openapi.web.apigw.constants.GwConstants.CP_API;
import com.kt.openapi.web.apigw.constants.GwConstants.REQUEST_KEY;
import com.kt.openapi.web.apigw.constants.GwConstants.RESPONSE_KEY;
import com.kt.openapi.web.apigw.converter.GwUrlEncDecTargetParameterConverter.ParameterType;
import com.kt.openapi.web.apigw.converter.GwUrlEncDecTargetParameterConverter.UrlEncDec;
import com.kt.openapi.web.apigw.entity.api.GwApiEntity;
import com.kt.openapi.web.apigw.entity.api.GwApiProxy;
import com.kt.openapi.web.apigw.entity.api.GwApiStage;
import com.kt.openapi.web.apigw.entity.api.GwKosCommonHeader;
import com.kt.openapi.web.apigw.entity.api.GwUrlEncDec;
import com.kt.openapi.web.apigw.entity.api.manager.ApiEntity;
import com.kt.openapi.web.apigw.exception.ConversionException;
import com.kt.openapi.web.apigw.type.GwInFormat;
import com.kt.openapi.web.apigw.type.GwOutFormat;
import com.kt.openapi.web.apigw.type.HandlerType;
import com.kt.openapi.web.apigw.utils.AbstractDataConverter;

public class GwApiEntityConverter extends AbstractDataConverter<ApiEntity, GwApiEntity> {

    @Override
    protected GwApiEntity createTarget() {
        return new GwApiEntity();
    }

    @Override
    public GwApiEntity convert(ApiEntity source, GwApiEntity target) throws ConversionException {
        target.setId(source.getId());
        target.setMethod(source.getMethod());
        target.setTimeout(source.getTimeout());
        target.setThreadHoldTime((int) (source.getTimeout() * .8));
        target.setUri(source.getUrl());
        target.setApiVersion(source.getVersion());
        if (source.getProtocol() != null) {
            target.setProtocol(source.getProtocol());
        }

        // oif name
        target.setApiNo(source.getApiNo());

        // masking
        List<String> maskingFields = new ArrayList<>();
        GwMaskingParameterConverter maskingParameterConverter = new GwMaskingParameterConverter();
        maskingParameterConverter.convert(source.getRequest().getParameters(), maskingFields);
        maskingParameterConverter.convert(source.getResponse().getParameters(), maskingFields);

        if (!CollectionUtils.isEmpty(maskingFields)) {
            String masking = StringUtils.join(maskingFields, ",");
            target.setMasking(masking);
        }


        switch (source.getHandler()) {
        case ADP_JSON_COMMON:
            target.setInFormat(GwInFormat.ADP_IN_JSON);
            target.setOutFormat(GwOutFormat.ADP_OUT_JSON);
            target.setOutCommonParam(API_COMMON_PARAM_TYPE_B);
            break;
        case ANY_JSON_COMMON:
            target.setInFormat(GwInFormat.ANY_IN_JSON);
            target.setOutFormat(GwOutFormat.ANY_OUT_JSON);
            target.setOutCommonParam(API_COMMON_PARAM_TYPE_B);
            break;
        case KOS_SOAP_COMMON:
            target.setInFormat(GwInFormat.ADP_IN_JSON);
            target.setOutFormat(GwOutFormat.KOS_OUT_SOAP);
            break;
        case KOS_JSON_COMMON:
            target.setInFormat(GwInFormat.ADP_IN_JSON);
            target.setOutFormat(GwOutFormat.KOS_OUT_JSON);
            break;
        //-- [tag:SR-20210222][add] {
        case ADP_SCAP_COMMON:
            target.setInFormat(GwInFormat.ADP_IN_JSON);
            target.setOutFormat(GwOutFormat.SCAP);
            target.setOutCommonParam(source.getOutCommonParam());	//-- user-input
            break;
        case ADP_CAPRI_COMMON:
            target.setInFormat(GwInFormat.ADP_IN_JSON);
            target.setOutFormat(GwOutFormat.CAPRI);
            target.setOutCommonParam(GwConstants.API_COMMON_PARAM_TYPE_J);
            break;
        case ADP_SB_COMMON:
            target.setInFormat(GwInFormat.ADP_IN_JSON);
            target.setOutFormat(source.getOutFormat());	//-- user-input
            //--[20210317][add][outCommonParam]
            target.setOutCommonParam(source.getOutCommonParam());	//-- user-input
            break;
        default: break; //-- [2023:codeeyes][swtich_default issue]
        //-- [tag:SR-20210222][add] }
        }

        target.setProxy(this.generateProxyInfo(source));
        return target;
    }

    private GwApiProxy generateProxyInfo(ApiEntity source) {
        GwApiProxy proxy = new GwApiProxy();
        
        // proxy.setEndpointId(source.getId());
        // 2019-07-08 versioning (허수영)
        proxy.setEndpointId("%s_%s".formatted(source.getId(), source.getVersion()));

        //-- [tag:SR-20210222][add] {
        switch (source.getHandler()) {
        case ADP_SCAP_COMMON:
            proxy.setEndpointId(source.getEndpointId());	//-- user-input
            break;
        case ADP_CAPRI_COMMON:
            proxy.setEndpointId(GwConstants.API_ENDPOINT_ID_CAPRI);
            break;
        default:
            break;
        }
        //-- [tag:SR-20210222][add] }

        proxy.setRequest(this.generateRequest(source));
        proxy.setResponse(this.generateResponse(source));

        return proxy;
    }

    private List<GwApiStage> generateRequest(ApiEntity source) {
        List<GwApiStage> request = new ArrayList<>();
        request.add(new GwApiStage(API_DEFAULT_AUTH_STAGE));

        // common
        GwApiStage requestStage = new GwApiStage(API_REQUEST_STAGE);

        Map<String, Object> params = new HashMap<>();

        // Handler Type
        params.put(REQUEST_KEY.HANDLER_TYPE, source.getHandler().getKey());

        // 필수 Parameter 정보 설정
        params.put(REQUEST_KEY.REQUIRED_PARAM, new GwRequiredParameterConverter().convert(source.getRequest().getParameters()));

        // 민감정보 설정
//        GwSecureFieldsConverter secureFieldsConverter = new GwSecureFieldsConverter();
//        secureFieldsConverter.setPrefix(CP_API.REQUEST);
//        params.put(REQUEST_KEY.DECRYPT_TARGET, secureFieldsConverter.convert(source.getRequest().getParameters()));

        if (source.getHandler() == HandlerType.ADP_JSON_COMMON || source.getHandler() == HandlerType.ANY_JSON_COMMON) {
            // Header 고정값 설정
            params.put(REQUEST_KEY.HEADER_FIXED_RULE, new GwFixedParameterConverter().convert(source.getRequest().getHeaders()));

            // client IP rule
            if (!StringUtils.isBlank(source.getClientIpMappingKey())) {
                params.put(REQUEST_KEY.CLIENT_IP_RULE, source.getClientIpMappingKey());	// user-input
            }
        }

        if (source.getHandler() == HandlerType.KOS_SOAP_COMMON) {
            Map<String, GwKosCommonHeader> commonHeaderMap = new HashMap<>();
            commonHeaderMap.put(REQUEST_KEY.FIXED_PARAM_COMMON_HEADER, new GwKosCommonHeaderConverter().convert(source.getRequest().getHeaders()));
            params.put(REQUEST_KEY.FIXED_PARAM, commonHeaderMap);
        } else {
            // Header 설정
            params.put(REQUEST_KEY.HEADER_PARAM, new GwHeaderParameterConverter().convert(source.getRequest().getHeaders()));
        }

        if (source.getHandler() == HandlerType.KOS_JSON_COMMON) {
            params.put(REQUEST_KEY.NULLSET_PARAM, new GwNullSetParameterConverter().convert(source.getRequest().getParameters()));
        }

        //-- [tag:SR-20210222][chg]
        if (source.getHandler() == HandlerType.ANY_JSON_COMMON
            || source.getHandler() == HandlerType.ADP_SCAP_COMMON || source.getHandler() == HandlerType.ADP_CAPRI_COMMON || source.getHandler() == HandlerType.ADP_SB_COMMON) {
            // except field
            GwDoNotSendParameterConverter doNotSendParameterConverter = new GwDoNotSendParameterConverter();
            doNotSendParameterConverter.setPrefix(CP_API.REQUEST);
            params.put(REQUEST_KEY.EXCEPT_PARAM, doNotSendParameterConverter.convert(source.getRequest().getParameters()));
            // Body 고정값 설정
            GwFixedParameterConverter fixedParameterConverter = new GwFixedParameterConverter();
            fixedParameterConverter.setPrefix(CP_API.REQUEST);
            params.put(REQUEST_KEY.BODY_FIXED_RULE, fixedParameterConverter.convert(source.getRequest().getParameters()));
        }
        if (source.getHandler() == HandlerType.ANY_JSON_COMMON) {
            // mapping rule
            GwMappingRuleConverter mappingRuleConverter = new GwMappingRuleConverter();
            mappingRuleConverter.setPrefix(CP_API.REQUEST);
            params.put(REQUEST_KEY.MAPPING_RULE, mappingRuleConverter.convert(source.getRequest().getParameters()));
        }
        
        //-- [tag:SR-20210222][add] {
        if (source.getHandler() == HandlerType.ADP_SCAP_COMMON || source.getHandler() == HandlerType.ADP_SB_COMMON) {
            if (!StringUtils.isBlank(source.getReqApiName())) {
	            params.put(REQUEST_KEY.API_NAME, source.getReqApiName());	// user-input
            }
        }
        if (source.getHandler() == HandlerType.ADP_SCAP_COMMON || source.getHandler() == HandlerType.ADP_CAPRI_COMMON || source.getHandler() == HandlerType.ADP_SB_COMMON) {
            if (!MapUtils.isEmpty(source.getReqConfigToBody())) {
	            params.put(REQUEST_KEY.CONFIG_TO_BODY, source.getReqConfigToBody());	// user-input
            }
            if (!MapUtils.isEmpty(source.getReqHeaderToBody())) {
	            params.put(REQUEST_KEY.HEADER_TO_BODY, source.getReqHeaderToBody());	// user-input
            }
            if (!MapUtils.isEmpty(source.getReqMappingToBody())) {
	            params.put(REQUEST_KEY.MAPPING_TO_BODY, source.getReqMappingToBody());	// user-input
            }
            if (!StringUtils.isBlank(source.getReqUrlDecodeCharset())) {
                GwUrlEncDec gwUrlEncDec = new GwUrlEncDec();
                GwUrlEncDecTargetParameterConverter urlEncDecTargetConverter = new GwUrlEncDecTargetParameterConverter(ParameterType.REQUEST, UrlEncDec.DECODE);
                params.put(REQUEST_KEY.URL_DECODE, urlEncDecTargetConverter.convert(source, gwUrlEncDec));
            }

        }
        if (source.getHandler() == HandlerType.ADP_CAPRI_COMMON) {
            if (!StringUtils.isBlank(source.getReqUrlEncodeCharset())) {
	            GwUrlEncDec gwUrlEncDec = new GwUrlEncDec();
	            GwUrlEncDecTargetParameterConverter urlEncDecTargetConverter = new GwUrlEncDecTargetParameterConverter(ParameterType.REQUEST, UrlEncDec.ENCODE);
	            params.put(REQUEST_KEY.URL_ENCODE, urlEncDecTargetConverter.convert(source, gwUrlEncDec));
            }
        }
        if (source.getHandler() == HandlerType.ADP_SB_COMMON) {
            params.put(REQUEST_KEY.UPLOAD_TARGET, new GwUploadTargetParameterConverter().convert(source.getRequest().getParameters()));
        }
        //-- [tag:SR-20210222][add] }

        requestStage.setParams(params);
        request.add(requestStage);

        return request;
    }

    private List<GwApiStage> generateResponse(ApiEntity source) {
        List<GwApiStage> response = new ArrayList<>();
        // common
        GwApiStage responseStage = new GwApiStage(API_RESPONSE_STAGE);
        Map<String, Object> params = new HashMap<>();
        params.put(RESPONSE_KEY.HANDLER_TYPE, source.getHandler());

//        GwSecureFieldsConverter secureFieldsConverter = new GwSecureFieldsConverter();
//        secureFieldsConverter.setPrefix(CP_API.RESPONSE);
//        params.put(RESPONSE_KEY.ENCRYPT_TARGET, secureFieldsConverter.convert(source.getResponse().getParameters()));

        //-- [tag:SR-20210222][chg]
        if (source.getHandler() == HandlerType.ANY_JSON_COMMON
            || source.getHandler() == HandlerType.ADP_SCAP_COMMON || source.getHandler() == HandlerType.ADP_CAPRI_COMMON || source.getHandler() == HandlerType.ADP_SB_COMMON) {
            // Except Field
            GwDoNotSendParameterConverter doNotSendParameterConverter = new GwDoNotSendParameterConverter();
            doNotSendParameterConverter.setPrefix(CP_API.RESPONSE);
            params.put(RESPONSE_KEY.EXCEPT_PARAM, doNotSendParameterConverter.convert(source.getResponse().getParameters()));
            //-- [tag:SR-20210222][chg][i][prefix response를 지정]
            // Body 고정값 설정
            GwFixedParameterConverter fixedParameterConverter = new GwFixedParameterConverter();
            fixedParameterConverter.setPrefix(CP_API.RESPONSE);
            params.put(REQUEST_KEY.BODY_FIXED_RULE, fixedParameterConverter.convert(source.getResponse().getParameters()));
            //--##params.put(RESPONSE_KEY.BODY_FIXED_RULE, new GwFixedParameterConverter().convert(source.getResponse().getParameters()));
        }
        if (source.getHandler() == HandlerType.ANY_JSON_COMMON) {
            GwMappingRuleConverter mappingRuleConverter = new GwMappingRuleConverter();
            params.put(RESPONSE_KEY.MAPPING_RULE, mappingRuleConverter.convert(source.getResponse().getParameters()));

            // result mapping
            params.put(RESPONSE_KEY.RESULT_MAPPING_RULE, source.getResultMapping());
        }

        if (source.getHandler() == HandlerType.KOS_SOAP_COMMON) {
            params.put(RESPONSE_KEY.JSON_ARRAY_TARGET, new GwJsonArrayParameterConverter().convert(source.getResponse().getParameters()));
        }

        //-- [tag:SR-20210222][add] {
        if (source.getHandler() == HandlerType.ADP_SCAP_COMMON || source.getHandler() == HandlerType.ADP_CAPRI_COMMON || source.getHandler() == HandlerType.ADP_SB_COMMON) {
            if (!MapUtils.isEmpty(source.getResMappingToBody())) {
	            params.put(RESPONSE_KEY.MAPPING_TO_BODY, source.getResMappingToBody());	// user-input
            }
            if (!CollectionUtils.isEmpty(source.getResProvideParam())) {
	            params.put(RESPONSE_KEY.PROVIED_PARAM, source.getResProvideParam());	// user-input
            }
            
            if (!StringUtils.isBlank(source.getResUrlEncodeCharset())) {
	            GwUrlEncDec gwUrlEncDec = new GwUrlEncDec();
	            GwUrlEncDecTargetParameterConverter urlEncDecTargetConverter = new GwUrlEncDecTargetParameterConverter(ParameterType.RESPONSE, UrlEncDec.ENCODE);
	            params.put(RESPONSE_KEY.URL_ENCODE, urlEncDecTargetConverter.convert(source, gwUrlEncDec));
            }
            params.put(RESPONSE_KEY.ARRAY_TARGET, new GwJsonArrayParameterConverter().convert(source.getResponse().getParameters()));
        }
        //-- [tag:SR-20210222][add] }

        responseStage.setParams(params);
        response.add(responseStage);
        return response;
    }

    //-- [tag:PRJ-20220901] {
    public Map<String, Object> generateRequestStageParam(ApiEntity source) {
        Map<String, Object> params = new HashMap<>();

        List<GwApiStage> gwApiStageList = this.generateRequest(source);
        if (!CollectionUtils.isEmpty(gwApiStageList)) {
            GwApiStage gwApiStage = gwApiStageList.get(gwApiStageList.size() - 1);
            params = gwApiStage.getParams();
        }
    
        return params;
    } 

    public Map<String, Object> generateResponseStageParam(ApiEntity source) {
        Map<String, Object> params = new HashMap<>();

        List<GwApiStage> gwApiStageList = this.generateResponse(source);
        if (!CollectionUtils.isEmpty(gwApiStageList)) {
            GwApiStage gwApiStage = gwApiStageList.get(gwApiStageList.size() - 1);
            params = gwApiStage.getParams();
        }
    
        return params;
    } 
    //-- [tag:PRJ-20220901] }
}
