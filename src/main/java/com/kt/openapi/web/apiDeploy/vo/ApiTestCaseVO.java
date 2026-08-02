package com.kt.openapi.web.apiDeploy.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 테스트 케이스 정보를 담는 VO
 */
@Getter
@Setter
@ToString
public class ApiTestCaseVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long rownum;
    private Long seq;
    private Long deployProcSeq;
    private String testNm;
    private String testResult;
    private String testCont;
    private String regDt;
    private String regr;
    private String apiNo;
    private String caseNm;
    private String cdNm;
    private Long verifiSuccess;
    private Long verifiFail;
    private String lastSuccessYn;
    private Long testCaseSeq;
}
