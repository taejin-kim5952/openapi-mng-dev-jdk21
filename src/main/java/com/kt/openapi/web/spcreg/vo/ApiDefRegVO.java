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
    private String apiNm;
    private String apiDesc;
    private String apiPath;
    private String methodCd;      // MTHTYP1000 comn_cd
    private String apiClass;      // APIGUB1000 comn_cd
    private String apiHandlerCd;  // Private 전용, APIHDR1000 comn_cd
    private String providerSeq;

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
        // object/array 중첩 저장을 위한 클라이언트 전용 상관키. 저장 시에만 쓰이고
        // 실제 PRNTS_PARAM_NO로 치환된다.
        private String tempId;
        private String parentTempId;
    }
}
