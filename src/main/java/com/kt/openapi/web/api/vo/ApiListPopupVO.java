package com.kt.openapi.web.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApiListPopupVO {
    private String apiId;
    private String apiNm;
    private String sysId;
    private String sysNm;
}