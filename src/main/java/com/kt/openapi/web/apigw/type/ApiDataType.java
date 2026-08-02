package com.kt.openapi.web.apigw.type;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

public enum ApiDataType implements EnumModel {
    STRING("String", "String"),
    OBJECT("Object", "Object"),
    ARRAY("Array", "Array"),
    NUMBER("Number", "Number"),
    BOOLEAN("Boolean", "Boolean"),
    INTEGER("Integer", "Integer"),
    ;

    /*
    Object
            Array
    String
            Number
    Integer
            Boolean
    Date
            Time
    Datetime
            File
            */


    private String value;
    private String code;

    public static final Map<String, ApiDataType> findMap = Maps.newConcurrentMap();
    
    ApiDataType(String value, String code) {
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
        Arrays.stream(ApiDataType.values())
                .forEach(type-> findMap.put(type.getCode(), type));
    }

    public static ApiDataType findByCode(String code) {
        return findMap.get(code);
    }
}
