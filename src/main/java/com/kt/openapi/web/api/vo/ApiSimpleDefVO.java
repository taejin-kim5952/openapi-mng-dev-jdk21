package com.kt.openapi.web.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.api.vo
 * 2. 타입명   : ApiSimpleDefVO.java
 * 5. 설명     : "간단 상세" 화면의 Path/Method(KOA_TB_API_DEF) 목록 행 + 상세 팝업 VO.
 *              목록 조회 시에는 요약 필드만, 상세 조회/저장 시에는 전체 필드가 채워진다.
 * </pre>
 */
@Getter
@Setter
@ToString
public class ApiSimpleDefVO {
    // 요약(목록 행)
    private String apiNo;
    private String apiSpcNo;
    private String apiNm;
    private String apiCtgryNo;
    private String ctgryNm;
    private String apiPath;
    private String methodCd;
    private String apiDesc;
    private String useYn;
    private String showStatusCd;

    // 엔드포인트/게이트웨이
    private String endpntMethodCd;
    private String endpntTbUrl;
    private String endpntPrdUrl;
    private String endpntClientIp;
    private String endpntTimeout;

    // 응답 매핑
    private String resmapResCdField;
    private String resmapSuccVal;
    private String resmapErrCdField;
    private String resmapErrMsgField;

    // HDP 연동
    private String hdpApiOutFormat;
    private String hdpApiOutCommonParam;
    private String hdpApiEndpointId;
    private String hdpReqApiName;
    private String hdpReqConfigToBody;
    private String hdpReqHeaderToBody;
    private String hdpReqMappingToBody;
    private String hdpReqUrlDecode;
    private String hdpReqUrlEncode;
    private String hdpResMappingToBody;
    private String hdpResProvideParam;
    private String hdpResUrlEncode;
    private String hdpExtProp;
    private String hdpHndlroptnConfig;

    // 배포현황 (읽기 전용 표시용)
    private String bstgwTbSysId;
    private String bstgwPrdSysId;
    private String dplyReqFlag;
    private String tbDplyStatus;
    private String dplyVeriStatus;
    private String prdDplyReqFlag;
    private String prdDplyStatus;

    private String regr;
    private String amdr;
}
