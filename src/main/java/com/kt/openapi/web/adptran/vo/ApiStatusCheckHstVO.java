package com.kt.openapi.web.adptran.vo;

import com.kt.openapi.web.cmm.vo.ComBaseVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * API 상태 점검 이력 정보를 담는 VO
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ApiStatusCheckHstVO extends ComBaseVO {
    private static final long serialVersionUID = 1L;

    private Long seq;
    private String sysId;
    private String sysNm;
    private String apiSpcNo;
    private String asApiSpcNm;
    private String apiNo;
    private String apiNm;
    private String statusCheckDt;
    private String statusCode;
    private Integer statusResMsec;
    
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
}
