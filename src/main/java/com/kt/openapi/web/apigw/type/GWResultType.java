package com.kt.openapi.web.apigw.type;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

public enum GWResultType implements EnumModel {
    OK("Success", "OK"),
    FAILURE("Failure", "FAILURE"),
    ;

    private String value;
    private String code;

    public static final Map<String, GWResultType> findMap = Maps.newConcurrentMap();
    
    GWResultType(String value, String code) {
        this.value = value;
        this.code = code;
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    static {
        Arrays.stream(GWResultType.values())
                .forEach(type-> findMap.put(type.getCode(), type));
    }

    public static GWResultType findByCode(String code) {
        return findMap.get(code);
    }
}
