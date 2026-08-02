package com.kt.openapi.web.apigw.entity.api.manager;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.kt.openapi.web.apigw.type.ApiDataType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class ApiParameter implements Serializable {
	@Serial
	private static final long serialVersionUID = -7569083255781908084L;

    private ApiDataType type;
    private String name;
    private String description;
    private boolean required = false;
    private boolean personalData = false;

    // CP 미노출 여부
    private boolean hidden = false;

    // 전송안함(CP -> Enabler or Enabler -> CP)
    private boolean doNotSend = false;
    private String fixedValue;

    // name -> mappingKey에 설정된 키로 변경하여 전달
    private String mappingKey;
    
	//-- [tag:SR-20210222][add] {
	//-- url decode여부
	private boolean urlDecode = false;
	//-- url encode여부
	private boolean urlEncode = false;
	//-- upload target여부
	private boolean uploadTarget = false;
	//-- [tag:SR-20210222][add] }

    @JsonIgnore
    private ApiParameter parent;

    // depth : zero index
    private int depth = 0;
    private List<ApiParameter> children;

    public ApiDataType getType() {
        return type;
    }

    public void setType(ApiDataType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isPersonalData() {
        return personalData;
    }

    public void setPersonalData(boolean personalData) {
        this.personalData = personalData;
    }

    public String getFixedValue() {
        return fixedValue;
    }

    public void setFixedValue(String fixedValue) {
        this.fixedValue = fixedValue;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getMappingKey() {
        return mappingKey;
    }

    public void setMappingKey(String mappingKey) {
        this.mappingKey = mappingKey;
    }

	//-- [tag:SR-20210222][add] {
	public boolean isUrlDecode() { return urlDecode; }
	public void setUrlDecode(boolean urlDecode) { this.urlDecode = urlDecode; }
	public boolean isUrlEncode() { return urlEncode; }
	public void setUrlEncode(boolean urlEncode) { this.urlEncode = urlEncode; }
	public boolean isUploadTarget() { return uploadTarget; }
	public void setUploadTarget(boolean uploadTarget) { this.uploadTarget = uploadTarget; }
	//-- [tag:SR-20210222][add] }

	public List<ApiParameter> getChildren() {
        return children;
    }

    public void setChildren(List<ApiParameter> children) {
        this.children = children;
    }

    public ApiParameter getParent() {
        return parent;
    }

    public void setParent(ApiParameter parent) {
        this.parent = parent;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isDoNotSend() {
        return doNotSend;
    }

    public void setDoNotSend(boolean doNotSend) {
        this.doNotSend = doNotSend;
    }
}
