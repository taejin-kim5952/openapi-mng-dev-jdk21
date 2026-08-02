package com.kt.openapi.web.apigw.type;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

public enum JobStatus implements EnumModel {
    STANDBY("Stand by", "STANDBY"),
    DOING("Doing", "DOING"),
    DONE("Done", "DONE"),
    FAILURE("Failure", "FAILURE"),
    ;

    private String value;
    private String code;
    
    public static final Map<String, JobStatus> findMap = Maps.newConcurrentMap();

    JobStatus(String value, String code) {
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
        Arrays.stream(JobStatus.values())
                .forEach(type-> findMap.put(type.getCode(), type));
    }

    public static JobStatus findByCode(String code) {
        return findMap.get(code);
    }
}
