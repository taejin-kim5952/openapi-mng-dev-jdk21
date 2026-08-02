package com.kt.openapi.web.apigw.type;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

public enum DeployJobStatus implements EnumModel {
    STANDBY("StandBy", "STANDBY"),
    INIT("Init", "INIT"),
    DEPLOYING("Deploying", "DEPLOYING"),
    ROLLING_BACK("Rolling back", "ROLLING_BACK"),
    DONE("Done", "DONE"),
    FAIL("Fail", "FAIL"),
    ;

    private String value;
    private String code;

    public static final Map<String, DeployJobStatus> findMap = Maps.newConcurrentMap();
    
    DeployJobStatus(String value, String code) {
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
        Arrays.stream(DeployJobStatus.values())
                .forEach(type-> findMap.put(type.getCode(), type));
    }

    public static DeployJobStatus findByCode(String code) {
        return findMap.get(code);
    }
}
