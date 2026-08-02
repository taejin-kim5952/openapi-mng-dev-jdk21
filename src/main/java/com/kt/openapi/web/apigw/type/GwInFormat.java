package com.kt.openapi.web.apigw.type;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

public enum GwInFormat implements EnumModel {
    ADP_IN_JSON("ADP Json", "ADP_IN_JSON"),
    ANY_IN_JSON("Any Common Json", "ANY_IN_JSON"),
    ;

    private String value;
    private String code;

    public static final Map<String, GwInFormat> findMap = Maps.newConcurrentMap();

    GwInFormat(String value, String code) {
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
        Arrays.stream(GwInFormat.values())
                .forEach(type-> findMap.put(type.getCode(), type));
    }

    public static GwInFormat findByCode(String code) {
        return findMap.get(code);
    }
}
