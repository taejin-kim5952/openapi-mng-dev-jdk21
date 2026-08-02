package com.kt.openapi.web.apiDeploy.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 배포 신청 정보를 담는 VO
 */
@Getter
@Setter
@ToString
public class ApiDeployApplyVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long seq;
    private Long deployProcSeq;
    private String deployCdnm;
    private String verifiCdnm;
    private String prntsCd;
    private String sysNm;
    private String ctgryNm;
    private String apiNm;
    private String deployCd;
    private String verifiCd;
    private String regr;
    private String regDt;
    private String bigo;
    private Long deployApplySeq;
    private String apiNo;
    private String apiId;
    private String apiVer;
    
    // For selDeployProposeContent
    private String deployStDt;
    private String reqNm;
    private String deployTitle;
    private String deployCont;
    private String apiPath;
    private String rejectCont;
    private String prdDplyStDt;
}
