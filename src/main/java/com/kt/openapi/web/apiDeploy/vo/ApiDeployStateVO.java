package com.kt.openapi.web.apiDeploy.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 배포 전체 현황 정보를 담는 VO
 */
@Getter
@Setter
@ToString
public class ApiDeployStateVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long seq;
    private String sysId;
    private String sysNm;
    private Integer totalCnt;
    private Integer deployReqCnt;
    private Integer deployIngCnt;
    private Integer deployOkCnt;
    private Integer deployNkCnt;
    private String deployCdnm;
    private String verifiCdnm;
    private String prntsCd;
    private String ctgryNm;
    private String apiNm;
    private String deployCd;
    private String verifiCd;
    private String regr;
    private String regDt;
    private String bigo;
    private String apiVer;
    private String apiId;
    private Integer requiredCnt;
}
