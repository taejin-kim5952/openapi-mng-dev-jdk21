package com.kt.openapi.web.apigw.entity.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PLDeploymentEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = 9209321376164322650L;

    @JsonProperty("to-list")
    private List<String> toList = Arrays.asList("*");

    @JsonProperty("target-object")
    private String targetObject;

    @JsonProperty("id_list")
    private List<DeploymentApiEntity> ids = new ArrayList<>();
    
    public PLDeploymentEntity() {
    }

    public PLDeploymentEntity(String target, String id, String version) {
        this.targetObject = target;
        ids.add(new DeploymentApiEntity(id, version));
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

    public List<DeploymentApiEntity> getIds() {
        return ids;
    }

    public void setIds(List<DeploymentApiEntity> ids) {
        this.ids = ids;
    }
}
