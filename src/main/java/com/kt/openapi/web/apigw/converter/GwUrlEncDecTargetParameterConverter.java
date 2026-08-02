package com.kt.openapi.web.apigw.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.kt.openapi.web.apigw.constants.GwConstants;
import com.kt.openapi.web.apigw.constants.GwConstants.CP_API;
import com.kt.openapi.web.apigw.entity.api.GwUrlEncDec;
import com.kt.openapi.web.apigw.entity.api.manager.ApiEntity;
import com.kt.openapi.web.apigw.entity.api.manager.ApiParameter;
import com.kt.openapi.web.apigw.exception.ConversionException;
import com.kt.openapi.web.apigw.utils.AbstractDataConverter;

public class GwUrlEncDecTargetParameterConverter extends AbstractDataConverter<ApiEntity, GwUrlEncDec> {
	
	private String prefix;
	private ParameterType parameterType = ParameterType.INIT;
    private UrlEncDec urlEncDec = UrlEncDec.INIT;
    
	public enum ParameterType {
		INIT, REQUEST, RESPONSE;
    }

	public enum UrlEncDec {
		INIT, ENCODE, DECODE;
    }

    public GwUrlEncDecTargetParameterConverter(ParameterType parameterType, UrlEncDec urlEncDec) {
		super();
		this.parameterType = parameterType;
		this.urlEncDec = urlEncDec;
	}


    @Override
    protected GwUrlEncDec createTarget() {
        return new GwUrlEncDec();
    }

    @Override
    public GwUrlEncDec convert(ApiEntity source, GwUrlEncDec target) throws ConversionException {
        List<ApiParameter> sourceParameter;
        String charset = null;
        if (ParameterType.REQUEST == parameterType) {
            sourceParameter = source.getRequest().getParameters();
            this.prefix = CP_API.REQUEST;
        }
        else if (ParameterType.RESPONSE == parameterType) {
        	sourceParameter = source.getResponse().getParameters();
            this.prefix = CP_API.RESPONSE;
        }
        else {
        	throw new ConversionException("GwUrlEncDec.parameterType is required");
        }
        if (UrlEncDec.ENCODE == urlEncDec) {
	        if (ParameterType.REQUEST == parameterType) {
    	    	charset = source.getReqUrlEncodeCharset();
	        }
	        else if (ParameterType.RESPONSE == parameterType) {
    	    	charset = source.getResUrlEncodeCharset();
	        }
        }
        else if (UrlEncDec.DECODE == urlEncDec) {
	        if (ParameterType.REQUEST == parameterType) {
	            charset = source.getReqUrlDecodeCharset();
	        }
	        else {
	        	throw new ConversionException("GwUrlEncDec.ResUrlEncode is invalid");
	        }
        }
        else {
        	throw new ConversionException("GwUrlEncDec.urlEncDec is required");
        }
        if (StringUtils.isBlank(charset)) {
        	throw new ConversionException("GwUrlEncDec.charset cannot be blank");
        }

    	List<String> targetList = new ArrayList<>();
        for (ApiParameter parameter : sourceParameter) {
            this.addUrlEncDecTarget(this.prefix, parameter, targetList);
        }
        
        target.setCharset(charset);
        target.setTarget(targetList);

        return target;
    }

	private void addUrlEncDecTarget(final String lineage, ApiParameter parameter, List<String> targetList) {
        StringBuilder depth = new StringBuilder();
        if (!StringUtils.isBlank(lineage)) {
            depth.append(lineage).append(GwConstants.API_PARAMETER_DEPTH_SEPARATOR);
        }
        depth.append(parameter.getName());
        if ((UrlEncDec.ENCODE == this.getUrlEncDec()) && parameter.isUrlEncode()) {
            targetList.add(depth.toString());
        } 
        else if ((UrlEncDec.DECODE == this.getUrlEncDec()) && parameter.isUrlDecode()) {
            targetList.add(depth.toString());
        }
        else {
            if (!CollectionUtils.isEmpty(parameter.getChildren())) {
                for (ApiParameter child : parameter.getChildren()) {
                    this.addUrlEncDecTarget(depth.toString(), child, targetList);
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

    public ParameterType getParameterType() {
		return parameterType;
	}

	public void setParameterType(ParameterType parameterType) {
		this.parameterType = parameterType;
	}

	public UrlEncDec getUrlEncDec() {
		return urlEncDec;
	}

	public void setUrlEncDec(UrlEncDec urlEncDec) {
		this.urlEncDec = urlEncDec;
	}
}
