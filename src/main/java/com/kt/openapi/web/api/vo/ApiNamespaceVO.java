package com.kt.openapi.web.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * API 네임스페이스 및 통계 정보를 담는 VO
 */
@Getter
@Setter
@ToString
public class ApiNamespaceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String namespace;
    private Integer apiCount;
    private String updateTime;
    private Integer rank;
}
