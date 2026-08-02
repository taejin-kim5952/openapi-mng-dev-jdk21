package com.kt.openapi.web.apiDeploy.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * API 트리 구조 정보를 담는 VO
 */
@Getter
@Setter
@ToString
public class ApiTreeVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String text;
    private String parent;
    private String type;
    private String apiNo;
    private String apiSpcNo;
    private String sysNm;
    private String spcNm;
    private String apiNm;
    private Long seq;
}
