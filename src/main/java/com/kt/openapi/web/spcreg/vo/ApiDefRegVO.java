package com.kt.openapi.web.spcreg.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.spcreg.vo
 * 2. 타입명   : ApiDefRegVO.java
 * 5. 설명     : "API 등록"(기존 SPC에 API 추가) 화면 전용 VO. SPC는 만들지 않고(spcReg가 담당),
 *              사용자가 고른 기존 apiSpcNo 아래 KOA_TB_API_CTGRY(재사용 또는 최초 1회 생성)/
 *              KOA_TB_API_DEF/KOA_TB_API_PARAM에 INSERT할 때 필요한 필드만 담는다.
 * </pre>
 */
@Getter
@Setter
@ToString
public class ApiDefRegVO {

    // 등록 대상 그룹
    private String apiSpcNo;
    private String autId;

    // KOA_TB_API_CTGRY
    private String ctgryNm;
    private String apiCtgryNo;

    // KOA_TB_API_DEF
    private String apiNo;
    private String apiId;         // API 아이디 (YAML의 operationId에 해당, 시스템 전체에서 유일해야 함)
    private String apiNm;
    private String apiDesc;
    private String apiPath;
    private String methodCd;      // MTHTYP1000 comn_cd
    private String apiClass;      // APIGUB1000 comn_cd
    private String apiHandlerCd;  // Private 전용, APIHDR1000 comn_cd
    private String providerSeq;
    private String useYn;         // API 노출여부(Y=노출/N=비노출). 기존 마법사 기본값 Y
    private String guideGubun;    // 가이드 구분(REST/SOAP, 선택값이라 빈 문자열 허용)
    private String sandboxYn;     // sandbox 적용여부(Y=적용/N=미적용, 기존 마법사 기본값 N)
    private String endpntMethodCd; // 내부(TB/운영) 엔드포인트 호출 시 실제 쓰는 HTTP Method. MTHTYP1000 comn_cd, methodCd와 다를 수 있음

    // API 버전업(같은 SPC 안에서 Path의 v1.0 -> v1.1처럼 버전 세그먼트만 바꿔 새 DEF를 만드는 기능).
    // apiVer는 서버가 apiPath에서 자동으로 추출해 채우므로 화면이 직접 입력하지 않는다.
    // apiVerNo는 "버전 패밀리 키": 새 DEF는 기본으로 자기 자신의 apiNo를 갖고(=새 패밀리 시작),
    // 버전업으로 만들어진 DEF만 원본의 apiVerNo를 그대로 물려받아 원본과 같은 패밀리가 된다.
    // 같은 패밀리끼리는 API ID 중복검사에서 서로 예외 처리된다(같은 API의 여러 버전이므로).
    private String apiVer;
    private String apiVerNo;

    // BEAST G/W 연동(선택) - 그룹(SPC)의 BEAST 사용여부가 'Y'일 때만 화면에 노출된다.
    // 시스템명은 KOA_TB_API_DEF에 저장 컬럼이 없어(ID만 저장) 화면에서 선택 시점에만 잠깐 보여준다.
    private String bstgwTbSysId;
    private String bstgwPrdSysId;

    // 고급 설정(선택) - 엔드포인트/게이트웨이, 응답매핑, HDP 연동. simpleView(간단 상세)의
    // updDefDetail이 쓰는 것과 동일한 KOA_TB_API_DEF 컬럼을 등록 시점에 같이 채운다.
    private String endpntTbUrl;
    private String endpntPrdUrl;
    private String endpntClientIp;
    private String endpntTimeout;
    private String resmapResCdField;
    private String resmapSuccVal;
    private String resmapErrCdField;
    private String resmapErrMsgField;
    private String hdpApiEndpointId;
    private String hdpReqApiName;
    private String hdpApiOutFormat;
    private String hdpApiOutCommonParam;
    private String hdpReqMappingToBody;
    private String hdpResMappingToBody;

    // Handler별 추가 파라미터. 어떤 Handler를 골랐느냐에 따라 화면에 노출되는 항목이 달라진다
    // (기존 마법사 regFormPrivateHandlerParam_inc.html의 g_handler_param과 같은 구성).
    // Handler에 해당하지 않는 항목은 저장 시 컨트롤러에서 비워 KOA_TB_API_DEF에 잔값이 남지 않게 한다.
    private String hdpReqConfigToBody;
    private String hdpReqHeaderToBody;
    private String hdpResProvideParam;
    private String hdpReqUrlDecode;
    private String hdpReqUrlEncode;
    private String hdpResUrlEncode;
    private String hdpHndlroptnConfig;
    private String hdpExtProp;

    // 파라미터 목록 (KOA_TB_API_PARAM)
    private List<ApiDefParamVO> paramList;

    // 등록자 (세션)
    private String regr;
    private String amdr;

    @Getter
    @Setter
    @ToString
    public static class ApiDefParamVO {
        private String paramNm;
        private String dataTypeCd;   // DATTYP1000 comn_cd
        private String required;     // Y/N
        private String paramDesc;
        private String paramTypeCd;  // PRMTYP1010(입력/Query)/PRMTYP1020(출력)
        private String paramLoc;     // body/query
        private String exam;
        private String personalData; // PIICLS1000 comn_cd
        // 확장 속성 9종 + 응답 상태코드(출력 스코프 전용)
        private String doNotSend;      // DO_NOT_SEND Y/N
        private String fixedValue;     // FIXED_VALUE
        private String hidden;         // HIDDEN Y/N
        private String mappingKey;     // MAPPING_KEY
        private String bigo;           // BIGO
        private String paramSandboxYn; // PARAM_SANDBOX_YN
        private String hdpUrlDecode;   // HDP_URL_DECODE Y/N
        private String hdpUrlEncode;   // HDP_URL_ENCODE Y/N
        private String hdpUploadTarget;// HDP_UPLOAD_TARGET Y/N
        private String resCd;          // RES_CD
        private String resDesc;        // RES_DESC
        // object/array 중첩 저장을 위한 클라이언트 전용 상관키. 저장 시에만 쓰이고
        // 실제 PRNTS_PARAM_NO로 치환된다.
        private String tempId;
        private String parentTempId;
    }
}
