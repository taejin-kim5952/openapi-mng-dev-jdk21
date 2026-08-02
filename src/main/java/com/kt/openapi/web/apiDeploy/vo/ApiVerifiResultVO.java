package com.kt.openapi.web.apiDeploy.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 검증 결과 정보를 담는 VO
 */
@Getter
@Setter
@ToString
public class ApiVerifiResultVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long seq;
    private Long deployProcSeq;
    private String veriGb;
    private String veriStDt;
    private String veriEnDt;
    private String veriStatusCd;
    private String veriStatusNm;
    private String veriResult;
    private String regDt;
    private String regr;
    private String apiNm;
}
