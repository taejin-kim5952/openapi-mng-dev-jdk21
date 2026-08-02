package com.kt.openapi.web.beast.apigw.entity.svcdply;

import java.io.Serial;
import java.io.Serializable;

public class AtribEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = -8099970071646129211L;

	//-- [i]속성
	//-- payload {
    private String cpId; //-- CP ID
    private String serviceId; //-- 서비스 ID
	//-- payload }

	public String getCpId() { return cpId; }
	public void setCpId(String cpId) { this.cpId = cpId; }
	public String getServiceId() { return serviceId; }
	public void setServiceId(String serviceId) { this.serviceId = serviceId; }
}
