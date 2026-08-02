package com.kt.openapi.web.apigw.entity.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class GwDeploymentEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = 9209321376164322650L;

    @JsonProperty("to-list")
    private List<String> toList = Arrays.asList("*");

    @JsonProperty("target-object")
    private String targetObject;

    @JsonProperty("id_list")
    private List<String> ids;
        
    public GwDeploymentEntity() {
    }

    public GwDeploymentEntity(String target, String id) {
        this.targetObject = target;
        this.ids = Arrays.asList(id);
    }


    public List<String> getToList() {
        return toList;
    }

    public void setToList(List<String> toList) {
        this.toList = toList;
    }

    public String getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(String targetObject) {
        this.targetObject = targetObject;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
