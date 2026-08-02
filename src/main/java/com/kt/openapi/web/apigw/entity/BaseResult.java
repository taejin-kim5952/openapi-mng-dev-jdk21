package com.kt.openapi.web.apigw.entity;

import java.io.Serial;
import java.io.Serializable;

public class BaseResult implements Serializable {
	@Serial
	private static final long serialVersionUID = 8520921583592985231L;

    private String id;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
