package com.kt.openapi.web.apiDeploy.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 배포 관련 시스템 정보를 담는 VO
 */
@Getter
@Setter
@ToString
public class ApiDeploySystemVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sysId;
    private String sysNm;
    private String url;
    private String regDt;
    private String regr;
    private String amdDt;
    private String amdr;
    private Integer sortOdrg;
    private Integer subSortOdrg;
}
