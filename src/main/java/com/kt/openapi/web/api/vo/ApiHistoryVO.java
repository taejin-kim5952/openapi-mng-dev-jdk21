package com.kt.openapi.web.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * API 관리 이력 정보를 담는 VO
 */
@Getter
@Setter
@ToString
public class ApiHistoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hstNo;
    private String memo;
    private String regDt;
    private String regr;
    private String direction;
    private String cdNm;
}
