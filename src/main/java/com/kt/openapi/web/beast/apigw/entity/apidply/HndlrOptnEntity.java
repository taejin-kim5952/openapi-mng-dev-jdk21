package com.kt.openapi.web.beast.apigw.entity.apidply;

import java.io.Serial;
import java.io.Serializable;

public class HndlrOptnEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = -6268357459780011673L;

	//-- [i]핸들러 옵션
	//-- payload {
    private String request; //-- 요청 stageparam // JSON.stringify()
    private String response;    //-- 응답 stageparam // JSON.stringify()
    private String config;	//-- nginX내의 설정정보 등등
    private String custom;	//-- handler 내의 값에 대한 처리가 특이한 케이스 처리
	//-- payload }

    public String getRequest() { return request; }
    public void setRequest(String request) { this.request = request; }
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
	public String getConfig() { return config; }
	public void setConfig(String config) { this.config = config; }
	public String getCustom() { return custom; }
	public void setCustom(String custom) { this.custom = custom; }
}
