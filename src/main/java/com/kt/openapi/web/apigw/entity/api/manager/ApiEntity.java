package com.kt.openapi.web.apigw.entity.api.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.openapi.web.apigw.entity.endpoint.EndpointConfig;
import com.kt.openapi.web.apigw.type.HandlerType;
import com.kt.openapi.web.apigw.type.URLScheme;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.http.HttpMethod;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = -3564026595628060105L;

    @NotEmpty(message = "API id should not be empty")
    private String id;

    private int timeout;
    @NotEmpty(message = "URL is should not be empty")
    private String url;

    @NotNull(message = "Handler type is required")
    private HandlerType handler;
    @NotNull(message = "Method is required")
    private HttpMethod method;

    @Valid
    //-- [tag:SR-20210222][cmt]
    //-- [i]SCAP,CAPRI는 endpoint정보를 설정하지 않음 
    //--##@NotNull(message = "Endpoint is required")
    private EndpointConfig endpoint;

    // COMMON, ANYCOMMON // 해당 값 설정 시 client IP 정보를 해당 키로 Header 설정
    private String clientIpMappingKey;

    // ANYCOMMON // Enabler 에서 전달한 성공/실패(에러 상세) 정보를 추출
    private ApiResultMapping resultMapping;

    private ApiRequestEntity request;
    private ApiResponseEntity response;

    // oif api no (2019-06-07)
    private String apiNo;

    // version
    private String version;

    private URLScheme protocol = URLScheme.HTTP;

    //-- [tag:SR-20210222][add] {
    private String outFormat;		//-- SB // user-input
    private String outCommonParam;	//-- SCAP // user-input
    private String endpointId;		//-- SCAP // user-input
    
    //-- request user-input
    private String reqApiName;	//-- SCAP // user-input
    private Map<String, Object> reqConfigToBody;	// SCAP, CAPRI, SB // user-input
    private Map<String, Object> reqHeaderToBody;	// SCAP, CAPRI, SB // user-input
    private Map<String, Object> reqMappingToBody;	// SCAP, CAPRI, SB // user-input
    private String reqUrlDecodeCharset;	// SCAP, CAPRI, SB // user-input
    private String reqUrlEncodeCharset;	// CAPRI // user-input

    //-- response user-input
    private Map<String, Object> resMappingToBody;	// SCAP, CAPRI, SB // user-input
    private List<String> resProvideParam;	// SCAP, CAPRI, SB // user-input
    private String resUrlEncodeCharset;	// SCAP, CAPRI, SB // user-input
    //-- [tag:SR-20210222][add] }
    
    //-- [tag:PRJ-20220901] {
    private String bstgwDplyType;
    private String bstgwSysId;
    //-- [tag:PRJ-20220901] }
    
    //-- [tag:SR-20230113]
    private String hdpHndlroptnConfig;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HandlerType getHandler() {
        return handler;
    }

    public void setHandler(HandlerType handler) {
        this.handler = handler;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public EndpointConfig getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(EndpointConfig endpoint) {
        this.endpoint = endpoint;
    }

    public String getClientIpMappingKey() {
        return clientIpMappingKey;
    }

    public void setClientIpMappingKey(String clientIpMappingKey) {
        this.clientIpMappingKey = clientIpMappingKey;
    }

    public ApiResultMapping getResultMapping() {
        return resultMapping;
    }

    public void setResultMapping(ApiResultMapping resultMapping) {
        this.resultMapping = resultMapping;
    }

    public ApiRequestEntity getRequest() {
        return request;
    }

    public void setRequest(ApiRequestEntity request) {
        this.request = request;
    }

    public ApiResponseEntity getResponse() {
        return response;
    }

    public void setResponse(ApiResponseEntity response) {
        this.response = response;
    }

    public String getApiNo() {
        return apiNo;
    }

    public void setApiNo(String apiNo) {
        this.apiNo = apiNo;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public URLScheme getProtocol() {
        return protocol;
    }

    public void setProtocol(URLScheme protocol) {
        this.protocol = protocol;
    }

	//-- [tag:SR-20210222][add] {
	public String getOutFormat() { return outFormat; }
	public void setOutFormat(String outFormat) { this.outFormat = outFormat; }
	public String getOutCommonParam() { return outCommonParam; }
	public void setOutCommonParam(String outCommonParam) { this.outCommonParam = outCommonParam; }
	public String getEndpointId() { return endpointId; }
	public void setEndpointId(String endpointId) { this.endpointId = endpointId; }

	public String getReqApiName() { return reqApiName; }
	public void setReqApiName(String reqApiName) { this.reqApiName = reqApiName; }
	public Map<String, Object> getReqConfigToBody() { return reqConfigToBody; }
	public void setReqConfigToBody(Map<String, Object> reqConfigToBody) {
		this.reqConfigToBody = reqConfigToBody;
	}
	public boolean setReqConfigToBody(String reqConfigToBody) {
		this.reqConfigToBody = new HashMap<>();
		if (!StringUtils.isBlank(reqConfigToBody)) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				this.reqConfigToBody = mapper.readValue(reqConfigToBody, Map.class);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}
	public Map<String, Object> getReqHeaderToBody() { return reqHeaderToBody; }
	public void setReqHeaderToBody(Map<String, Object> reqHeaderToBody) {
		this.reqHeaderToBody = reqHeaderToBody;
	}
	public boolean setReqHeaderToBody(String reqHeaderToBody) {
		this.reqHeaderToBody = new HashMap<>();
		if (!StringUtils.isBlank(reqHeaderToBody)) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				this.reqHeaderToBody = mapper.readValue(reqHeaderToBody, Map.class);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}
	public Map<String, Object> getReqMappingToBody() { return reqMappingToBody; }
	public void setReqMappingToBody(Map<String, Object> reqMappingToBody) {
		this.reqMappingToBody = reqMappingToBody;
	}
	public boolean setReqMappingToBody(String reqMappingToBody) {
		this.reqMappingToBody = new HashMap<>();
		if (!StringUtils.isBlank(reqMappingToBody)) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				this.reqMappingToBody = mapper.readValue(reqMappingToBody, Map.class);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}
	public String getReqUrlDecodeCharset() { return reqUrlDecodeCharset; }
	public void setReqUrlDecodeCharset(String reqUrlDecodeCharset) { this.reqUrlDecodeCharset = reqUrlDecodeCharset; }
	public String getReqUrlEncodeCharset() { return reqUrlEncodeCharset; }
	public void setReqUrlEncodeCharset(String reqUrlEncodeCharset) { this.reqUrlEncodeCharset = reqUrlEncodeCharset; }
	public Map<String, Object> getResMappingToBody() { return resMappingToBody; }
	public void setResMappingToBody(Map<String, Object> resMappingToBody) {
		this.resMappingToBody = resMappingToBody;
	}
	public boolean setResMappingToBody(String resMappingToBody) {
		this.resMappingToBody = new HashMap<>();
		if (!StringUtils.isBlank(resMappingToBody)) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				this.resMappingToBody = mapper.readValue(resMappingToBody, Map.class);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}
	public List<String> getResProvideParam() { return resProvideParam; }
	public void setResProvideParam(List<String> resProvideParam) {
		this.resProvideParam = resProvideParam;
	}
	public boolean setResProvideParam(String resProvideParam) {
		this.resProvideParam = new ArrayList<>();
		if (!StringUtils.isBlank(resProvideParam)) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				this.resProvideParam = mapper.readValue(resProvideParam, List.class);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}
	public String getResUrlEncodeCharset() { return resUrlEncodeCharset; }
	public void setResUrlEncodeCharset(String resUrlEncodeCharset) { this.resUrlEncodeCharset = resUrlEncodeCharset; }
	//-- [tag:SR-20210222][add] }

    //-- [tag:PRJ-20220901] {
    public String getBstgwDplyType() { return bstgwDplyType; }
    public void setBstgwDplyType(String bstgwDplyType) { this.bstgwDplyType = bstgwDplyType; }
    public String getBstgwSysId() { return bstgwSysId; }
    public void setBstgwSysId(String bstgwSysId) { this.bstgwSysId = bstgwSysId; }
    //-- [tag:PRJ-20220901] }

	//-- [tag:SR-20230113]
    public String getHdpHndlroptnConfig() { return hdpHndlroptnConfig; }
    public void setHdpHndlroptnConfig(String hdpHndlroptnConfig) { this.hdpHndlroptnConfig = hdpHndlroptnConfig; }
}
