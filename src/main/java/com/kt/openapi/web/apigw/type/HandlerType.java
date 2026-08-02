package com.kt.openapi.web.apigw.type;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

public enum HandlerType implements EnumModel {
    ADP_JSON_COMMON("Common Handler", "ADPJSON"),
    ANY_JSON_COMMON("Any Common Handler", "ANYJSON"),
    KOS_JSON_COMMON("KOS MOS Handler", "KOSJSON"),
    KOS_SOAP_COMMON("KOS Soap Handler", "KOSSOAP"),
    //-- [tag:SR-20210222][add] {
    ADP_SCAP_COMMON("SCAP Handler", "ADPSCAP"),
    ADP_CAPRI_COMMON("CAPRI Handler", "ADPCAPRI"),
    ADP_SB_COMMON("SB Handler", "ADPSB"),
    //-- [tag:SR-20210222][add] }
    ;

    private String value;
    private String code;
    
    public static final Map<String, HandlerType> findMap = Maps.newConcurrentMap();

    HandlerType(String value, String code) {
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
        Arrays.stream(HandlerType.values())
                .forEach(type-> findMap.put(type.getCode(), type));
    }

    public static HandlerType findByCode(String code) {
        return findMap.get(code);
    }
}
