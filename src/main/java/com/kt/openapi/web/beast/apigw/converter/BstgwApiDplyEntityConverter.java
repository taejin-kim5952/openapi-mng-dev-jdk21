package com.kt.openapi.web.beast.apigw.converter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.openapi.web.apigw.constants.GwConstants;
import com.kt.openapi.web.apigw.constants.GwConstants.REQUEST_KEY;
import com.kt.openapi.web.apigw.constants.GwConstants.RESPONSE_KEY;
import com.kt.openapi.web.apigw.converter.GwApiEntityConverter;
import com.kt.openapi.web.apigw.converter.GwMaskingParameterConverter;
import com.kt.openapi.web.apigw.entity.api.manager.ApiEntity;
import com.kt.openapi.web.apigw.entity.endpoint.EndpointConfig;
import com.kt.openapi.web.apigw.exception.ConversionException;
import com.kt.openapi.web.apigw.type.GwInFormat;
import com.kt.openapi.web.apigw.type.GwOutFormat;
import com.kt.openapi.web.apigw.utils.AbstractDataConverter;
import com.kt.openapi.web.beast.apigw.entity.apidply.AtribEntity;
import com.kt.openapi.web.beast.apigw.entity.apidply.BstgwApiDplyEntity;
import com.kt.openapi.web.beast.apigw.entity.apidply.HndlrOptnEntity;

public class BstgwApiDplyEntityConverter extends AbstractDataConverter<ApiEntity, BstgwApiDplyEntity> {

    @Override
    protected BstgwApiDplyEntity createTarget() {
        return new BstgwApiDplyEntity();
    }

    @Override
    public BstgwApiDplyEntity convert(ApiEntity source, BstgwApiDplyEntity target) throws ConversionException {
        boolean isBlank;

        //-- [i][NN] {
        String dplyDt = "%sT%s".formatted((new SimpleDateFormat("yyyy-MM-dd")).format(new Date()), (new SimpleDateFormat("HH:mm:ss")).format(new Date()));
        target.setDplyDt(dplyDt);
        target.setDplyType(source.getBstgwDplyType());  //-- DPLY/DEL
        target.setSysId(source.getBstgwSysId());    //-- BST_SYNC_ADM_SYS_DPLY.SYS_ID
        String apiId = "%s%s%s".formatted(source.getId(), (StringUtils.isBlank(source.getVersion()) ? "" : "_"), source.getVersion());
        target.setApiId(apiId); //-- KOA_TB_API_DEF.API_NAME + '_' + fmt_version_in_path(KOA_TB_API_DEF.API_PATH)
        target.setIfNo(source.getApiNo());  //-- DEF.API_ID // OIF_nnnnn
        //-- [i][NN] }

        if (!StringUtils.isEmpty(source.getVersion())) {
            target.setVer(source.getVersion());
        }
        String method = ((null != source.getMethod()) ? source.getMethod().toString() : "");
        if (!StringUtils.isEmpty(method)) {
            target.setMeth(Arrays.asList(method));
        }
        if (!StringUtils.isEmpty(source.getUrl())) {
            target.setIn(source.getUrl());
        }
        EndpointConfig endpointConfig = source.getEndpoint();
        if (null != endpointConfig) {
            target.setOut(endpointConfig.getUrl());
        }

        String inFmt = "";
        String outFmt = "";
        String inComnParam = GwConstants.API_COMMON_PARAM_TYPE_B;
        String outComnParam = "";
        switch (source.getHandler()) {
        case ADP_JSON_COMMON:
            inFmt = GwInFormat.ADP_IN_JSON.getKey();
            outFmt = GwOutFormat.ADP_OUT_JSON.getKey();
            outComnParam = GwConstants.API_COMMON_PARAM_TYPE_B;
            break;
        case ANY_JSON_COMMON:
            inFmt = GwInFormat.ANY_IN_JSON.getKey();
            outFmt = GwOutFormat.ANY_OUT_JSON.getKey();
            outComnParam = GwConstants.API_COMMON_PARAM_TYPE_B;
            break;
        case KOS_SOAP_COMMON:
            inFmt = GwInFormat.ADP_IN_JSON.getKey();
            outFmt = GwOutFormat.KOS_OUT_SOAP.getKey();
            break;
        case KOS_JSON_COMMON:
            inFmt = GwInFormat.ADP_IN_JSON.getKey();
            outFmt = GwOutFormat.KOS_OUT_JSON.getKey();
            break;
        //-- [tag:SR-20210222][add] {
        case ADP_SCAP_COMMON:
            inFmt = GwInFormat.ADP_IN_JSON.getKey();
            outFmt = GwOutFormat.SCAP.getKey();
            outComnParam = source.getOutCommonParam();   //-- user-input
            break;
        case ADP_CAPRI_COMMON:
            inFmt = GwInFormat.ADP_IN_JSON.getKey();
            outFmt = GwOutFormat.CAPRI.getKey();
            outComnParam = GwConstants.API_COMMON_PARAM_TYPE_J;
            break;
        case ADP_SB_COMMON:
            inFmt = GwInFormat.ADP_IN_JSON.getKey();
            outFmt = source.getOutFormat(); //-- user-input
            outComnParam = source.getOutCommonParam();   //-- user-input
            break;
        default: break; //-- [2023:codeeyes][swtich_default issue]
        }

        ArrayList<String> reqHndlr = new ArrayList<String>();
        ArrayList<String> resHndlr = new ArrayList<String>();
        String errHndlr = "";

        if (!StringUtils.isBlank(inFmt)) {
            reqHndlr.add("REQ.Parser.infmt-%s".formatted(inFmt));
        }
        reqHndlr.add("REQ.KT-AUTH");
        if (!StringUtils.isBlank(inFmt)) {
            reqHndlr.add("REQ.%s".formatted(source.getHandler().getKey()));
        }

        if (!StringUtils.isBlank(outFmt)) {
            resHndlr.add("RES.Parser.outfmt-%s".formatted(outFmt));
        }
        if (!StringUtils.isBlank(inFmt)) {
            resHndlr.add("RES.%s".formatted(source.getHandler().getKey()));
            errHndlr = "ERR.infmt-%s".formatted(inFmt);
        }
        //--##if (!CollectionUtils.isEmpty(reqHndlr)) {
            target.setReqHndlr(reqHndlr);
        //--##}
        //--##if (!CollectionUtils.isEmpty(resHndlr)) {
            target.setResHndlr(resHndlr);
        //--##}
        if (!StringUtils.isEmpty(errHndlr)) {
            target.setErrHndlr(errHndlr);
        }

        target.setTimeOut(source.getTimeout());
        target.setPrnts(Boolean.FALSE);

        HndlrOptnEntity hndlrOptn = new HndlrOptnEntity();

        GwApiEntityConverter apiEntityConverter = new GwApiEntityConverter();
        Map<String, Object> mapReqParam = apiEntityConverter.generateRequestStageParam(source);
        //-- [i][except][REQUEST_KEY.HANDLER_TYPE 제외]
        mapReqParam.remove(REQUEST_KEY.HANDLER_TYPE);
        Map<String, Object> mapResParam = apiEntityConverter.generateResponseStageParam(source);
        //-- [i][except][RESPONSE_KEY.HANDLER_TYPE 제외]
        mapResParam.remove(RESPONSE_KEY.HANDLER_TYPE);

        ObjectMapper objectMapper = new ObjectMapper();
        String hndlrOptnRequest = "";
        String hndlrOptnResponse = "";
        //-- [i][tag:SR-20230113]
        String hndlrOptnConfig = source.getHdpHndlroptnConfig();
        String hndlrOptnCustom = "";
        try {
        	//-- [i][tag:SR-20230220][empty map이라도 {}설정]
        	boolean b_is_add_empty_obj = true;
        	if (b_is_add_empty_obj || (mapReqParam.size() > 0)) {
            	hndlrOptnRequest = objectMapper.writeValueAsString(mapReqParam);
        	}
        	if (b_is_add_empty_obj || (mapResParam.size() > 0)) {
	            hndlrOptnResponse = objectMapper.writeValueAsString(mapResParam);
        	}
            /*--@@
            //-- [i][encode string]
            JsonStringEncoder encoder = (JsonStringEncoder.getInstance());
            hndlrOptnResponse = new String(encoder.quoteAsString(hndlrOptnResponse));
            hndlrOptnRequest = new String(encoder.quoteAsString(hndlrOptnRequest));
            --*/
        } catch (JsonProcessingException e) {
          //-- [2023:codeeyes][empty_block issue]
        }
        if (!StringUtils.isEmpty(hndlrOptnRequest)) {
            hndlrOptn.setRequest(hndlrOptnRequest);
        }
        if (!StringUtils.isEmpty(hndlrOptnResponse)) {
            hndlrOptn.setResponse(hndlrOptnResponse);
        }
        if (!StringUtils.isEmpty(hndlrOptnConfig)) {
            hndlrOptn.setConfig(hndlrOptnConfig);
        }
        if (!StringUtils.isEmpty(hndlrOptnCustom)) {
            hndlrOptn.setCustom(hndlrOptnCustom);
        }
        target.setHndlrOptn(hndlrOptn);

		/*--@@
        GwApiEntityConverter apiEntityConverter = new GwApiEntityConverter();
        Map<String, Object> hndlrOptnRequest = apiEntityConverter.generateRequestStageParam(source);
        Map<String, Object> hndlrOptnResponse = apiEntityConverter.generateResponseStageParam(source);
        hndlrOptn.setRequest(hndlrOptnRequest);
        hndlrOptn.setResponse(hndlrOptnResponse);
        target.setHndlrOptn(hndlrOptn);
        --*/

        // mask
        List<String> maskingFields = new ArrayList<>();
        GwMaskingParameterConverter maskingParameterConverter = new GwMaskingParameterConverter();
        maskingParameterConverter.convert(source.getRequest().getParameters(), maskingFields);
        maskingParameterConverter.convert(source.getResponse().getParameters(), maskingFields);
        //--##if (!CollectionUtils.isEmpty(maskingFields)) {
            target.setMask(maskingFields);
        //--##}

        AtribEntity atrib = new AtribEntity();
        isBlank = true;
        if (!StringUtils.isBlank(inFmt)) {
            atrib.setInFmt(inFmt);
            isBlank = false;
        }
        if (!StringUtils.isBlank(outFmt)) {
            atrib.setOutFmt(outFmt);
            isBlank = false;
        }
        if (!StringUtils.isBlank(inComnParam)) {
            atrib.setInComnParam(inComnParam);
            isBlank = false;
        }
        //-- [i][230216][outComnParam값이 없을시 inComnParam값과 같게
        outComnParam = (StringUtils.isBlank(outComnParam) ? inComnParam : outComnParam);
        if (!StringUtils.isBlank(outComnParam)) {
            atrib.setOutComnParam(outComnParam);
            isBlank = false;
        }
        
        if (!isBlank) {
            target.setAtrib(atrib);
        }

        return target;
    }

}
