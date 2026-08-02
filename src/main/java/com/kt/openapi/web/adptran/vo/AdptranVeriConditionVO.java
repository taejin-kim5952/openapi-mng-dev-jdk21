package com.kt.openapi.web.adptran.vo;

import com.kt.openapi.web.cmm.vo.ComBaseVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Adptran API 검증 결과 정보를 담는 VO
 * KOA_TB_API_VERI_CONDITION 정보를 수용
 */
@Getter
@Setter
@ToString(callSuper = true)
public class AdptranVeriConditionVO extends ComBaseVO {

    private static final long serialVersionUID = 1L;

    private Long seq;
    private Long deployProcSeq;
    private Long testCaseSeq;
    private String verificationDt;
    private String resultCd;
    private String resultMsg;
    private String verifiUsr;
    private String successYn;
    private String stTime;
    private String endTime;
    private String procResultCd;
    private String procResultMsg;
    private String reqGwProfile;
    private String reqApiVeriBaseurl;
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
    private String apiNo;
    private String apiNm;
    private String testcaseNm;
    private String paramGub;
    private String paramHeader;
    private String paramBody;
    private String paramQuery;
    private String paramHeaderJson;
    private String paramBodyJson;
    private String assertCase;
    private String assertField;
    private String assertOperator;
    private String assertValue;
    private String assertResult;
}
