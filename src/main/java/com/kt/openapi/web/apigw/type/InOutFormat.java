//-- [tag:PRJ-20220901][i][mpybe_not_used]
package com.kt.openapi.web.apigw.type;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

public enum InOutFormat implements EnumModel {
    ADPJSON("ADP Json", "ADPJSON"),
    ANYJSON("Any Common Json", "ANYJSON"),
    KOSJSON("KOS Json", "KOSJSON"),
    KOSSOAP("KOS Soap", "KOSSOAP"),
    ;

    private String value;
    private String code;
    
    public static final Map<String, InOutFormat> findMap = Maps.newConcurrentMap();

    InOutFormat(String value, String code) {
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
        Arrays.stream(InOutFormat.values())
                .forEach(type-> findMap.put(type.getCode(), type));
    }

    public static InOutFormat findByCode(String code) {
        return findMap.get(code);
    }
}
