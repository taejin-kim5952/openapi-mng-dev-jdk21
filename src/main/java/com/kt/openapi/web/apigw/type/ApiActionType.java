package com.kt.openapi.web.apigw.type;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

public enum ApiActionType implements EnumModel {
    CREATE("Create", "CREATE"),
    UPDATE("Update", "UPDATE"),
    DELETE("Delete", "DELETE"),
    ;

    private String value;
    private String code;
    
    public static final Map<String, ApiActionType> findMap = Maps.newConcurrentMap();

    ApiActionType(String value, String code) {
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
        Arrays.stream(ApiActionType.values())
                .forEach(type-> findMap.put(type.getCode(), type));
    }

    public static ApiActionType findByCode(String code) {
        return findMap.get(code);
    }
}
