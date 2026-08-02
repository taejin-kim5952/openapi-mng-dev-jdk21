package com.kt.openapi.web.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * API 시스템 정보를 담는 VO
 */
@Getter
@Setter
@ToString
public class ApiSystemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sysId;
    private String sysNm;
    private String sysDesc;
    private String url;
    private String delYn;
    private Date regDt;
    private String regr;
    private Date amdDt;
    private String amdr;
    private String useYn;
    private String minId;
    private String maxId;
}
