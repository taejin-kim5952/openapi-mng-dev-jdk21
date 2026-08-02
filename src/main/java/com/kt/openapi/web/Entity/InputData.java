package com.kt.openapi.web.Entity;

import java.util.Map;

public class InputData {
	private String templateId;
	private Map<String,String> inData;
	
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public Map<String, String> getInData() {
		return inData;
	}
	public void setInData(Map<String, String> inData) {
		this.inData = inData;
	}
	
}
