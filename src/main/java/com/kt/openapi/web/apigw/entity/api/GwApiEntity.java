package com.kt.openapi.web.apigw.entity.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.openapi.web.apigw.constants.GwConstants;
import com.kt.openapi.web.apigw.type.GwInFormat;
import com.kt.openapi.web.apigw.type.GwOutFormat;
import com.kt.openapi.web.apigw.type.URLScheme;
import org.springframework.http.HttpMethod;

import java.io.Serial;
import java.io.Serializable;

public class GwApiEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = -5683206568782521817L;

    @JsonProperty("apiid")
    private String id;

    @JsonProperty("api-version")
    private String apiVersion;

    @JsonProperty("api-no")
    private String apiNo;

    private HttpMethod method;

    private int timeout;

    @JsonProperty("threadhold-time")
    private int threadHoldTime;

    private URLScheme protocol = URLScheme.HTTP;

    private String uri;

    @JsonProperty("in-format")
    private GwInFormat inFormat;

    @JsonProperty("out-format")
    //-- [tag:SR-20210222][chg]
    //--##private GwOutFormat outFormat;
    private String outFormat;

    private String division = GwConstants.API_DIVISION_ALONE;

    private String parent;

    @JsonProperty("in-common-param")
    private String inCommonParam = GwConstants.API_COMMON_PARAM_TYPE_B;

    @JsonProperty("out-common-param")
    private String outCommonParam;

    private String masking;

    @JsonProperty("proxy_info")
    private GwApiProxy proxy;

    @JsonProperty("route_info")
    private GwApiRoute route;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getThreadHoldTime() {
        return threadHoldTime;
    }

    public void setThreadHoldTime(int threadHoldTime) {
        this.threadHoldTime = threadHoldTime;
    }

    public URLScheme getProtocol() {
        return protocol;
    }

    public void setProtocol(URLScheme protocol) {
        this.protocol = protocol;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public GwInFormat getInFormat() {
        return inFormat;
    }

    public void setInFormat(GwInFormat inFormat) {
        this.inFormat = inFormat;
    }

	//-- [tag:SR-20210222][chg] {
	//-- [i]outFormat type change // GwOutFormat -> String
    public String getOutFormat() {
        return outFormat;
    }

    public void setOutFormat(String outFormat) {
        this.outFormat = outFormat;
    }

    public void setOutFormat(GwOutFormat outFormat) {
        this.outFormat = outFormat.getKey();
    }
	//-- [tag:SR-20210222][chg] }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getInCommonParam() {
        return inCommonParam;
    }

    public void setInCommonParam(String inCommonParam) {
        this.inCommonParam = inCommonParam;
    }

    public String getOutCommonParam() {
        return outCommonParam;
    }

    public void setOutCommonParam(String outCommonParam) {
        this.outCommonParam = outCommonParam;
    }

    public GwApiProxy getProxy() {
        return proxy;
    }

    public void setProxy(GwApiProxy proxy) {
        this.proxy = proxy;
    }

    public GwApiRoute getRoute() {
        return route;
    }

    public void setRoute(GwApiRoute route) {
        this.route = route;
    }

    public String getApiNo() {
        return apiNo;
    }

    public void setApiNo(String apiNo) {
        this.apiNo = apiNo;
    }

    public String getMasking() {
        return masking;
    }

    public void setMasking(String masking) {
        this.masking = masking;
    }
}
