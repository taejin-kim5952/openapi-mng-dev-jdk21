package com.kt.openapi.web.apiDeploy.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 배포 이력 정보를 담는 VO
 */
@Getter
@Setter
@ToString
public class ApiDeployHstVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long rownum;
    private Long seq;
    private Long deployProcSeq;
    private Long depSeq;
    private String deployGb;
    private String deployStDt;
    private String deployEnDt;
    private String deployStatusCd;
    private String deployStatusNm;
    private String deployResult;
    private String deployDate;
    private String regDt;
    private String regr;
    private String apiNm;
    private String deployCdnm;
    private String sysNm;
    private String serviceNm;
    private String successYn;
    private Long deployApplySeq;
    private String resultMsg;
    private String apiNo;
    private Long logSeq;
}
