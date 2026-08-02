package com.kt.openapi.web.adptran.vo;

import com.kt.openapi.web.cmm.vo.ComBaseVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * API 상태, 점검 이력, 시스템 정보를 모두 담는 통합 VO
 * [마이그레이션] EgovMap 대체용
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ApiStatusVO extends ComBaseVO {
    private static final long serialVersionUID = 1L;

    // Basic Status info
    private String sysId;
    private String sysNm;
    private String apiSpcNo;
    private String asApiSpcNm;
    private String apiNo;
    private String apiNm;
    private String statusCheckDt;
    private String statusCode;
    private Integer statusResMsec;
    private Integer count;

    // Daily History info
    private String dailyList;

    // Check History Detail info
    private Long seq;
    private String stTime;
    private String endTime;
    private String procResultCd;
    private String procResultMsg;
    private String reqApiUrl;
    private String reqHeaders;
    private String reqBody;
    private String reqTransactionId;
    private String reqSequenceNo;
    private String resTransactionId;
    private String resSequenceNo;
    private String resReturnCode;
    private String resReturnDescription;
    private String resErrorCode;
    private String resErrorDescription;
    private String resResponse;

    // System-Service info
    private String apiSpcId;
    private String ver;
}
