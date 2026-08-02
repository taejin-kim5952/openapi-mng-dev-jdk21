package com.kt.openapi.web.apigw.utils;


import com.kt.openapi.web.apigw.type.EnumModel;

public class EnumValue {
    private String key;
    private String value;

    public EnumValue() {
    }

    public EnumValue(EnumModel enumModel) {
        this.key = enumModel.getKey();
        this.value = enumModel.getValue();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
