package com.kt.openapi.web.apigw.type;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

public enum URLScheme implements EnumModel {
    HTTP("HTTP", "HTTP"),
    HTTPS("HTTPS", "HTTPS"),
    ;

    private String value;
    private String code;
    
    public static final Map<String, URLScheme> findMap = Maps.newConcurrentMap();

    URLScheme(String value, String code) {
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
        Arrays.stream(URLScheme.values())
                .forEach(type-> findMap.put(type.getCode(), type));
    }

    public static URLScheme findByCode(String code) {
        return findMap.get(code);
    }
}
