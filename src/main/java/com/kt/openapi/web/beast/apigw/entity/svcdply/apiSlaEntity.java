package com.kt.openapi.web.beast.apigw.entity.svcdply;

import java.io.Serial;
import java.io.Serializable;

public class apiSlaEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = -6207301211503248412L;

	//-- [i]api별 SLA
	//-- payload {
	private String apiId;
	private SlaEntity sla;
	//-- payload }
	public String getApiId() { return apiId; }
	public void setApiId(String apiId) { this.apiId = apiId; }
	public SlaEntity getSla() { return sla; }
	public void setSla(SlaEntity sla) { this.sla = sla; }
}
