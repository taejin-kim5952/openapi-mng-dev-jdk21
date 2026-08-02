package com.kt.openapi.web.apigw.type;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

public enum GwOutFormat implements EnumModel {
    ADP_OUT_JSON("ADP Json", "ADP_OUT_JSON"),
    ANY_OUT_JSON("Any Common Json", "ANY_OUT_JSON"),
    KOS_OUT_JSON("KOS Json", "KOS_OUT_JSON"),
    KOS_OUT_SOAP("KOS SOAP", "KOS_OUT_SOAP"),
    //-- [tag:SR-20210222][add] {
    SCAP("ADP SCAP Out", "SCAP"),
    CAPRI("ADP CAPRI Out", "CAPRI"),
    //-- [tag:SR-20210222][add] }
    ;

    private String value;
    private String code;
    
    public static final Map<String, GwOutFormat> findMap = Maps.newConcurrentMap();

    GwOutFormat(String value, String code) {
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
        Arrays.stream(GwOutFormat.values())
                .forEach(type-> findMap.put(type.getCode(), type));
    }

    public static GwOutFormat findByCode(String code) {
        return findMap.get(code);
    }
}
