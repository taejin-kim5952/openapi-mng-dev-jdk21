package com.kt.openapi.web.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.api.vo
 * 2. 타입명   : ApiQuickRegVO.java
 * 5. 설명     : "빠른 API 등록" 화면 전용 VO. 기존 ApiRegVO/ApiDefVO와는 독립적으로,
 *              이 화면에서 KOA_TB_API_SPC/KOA_TB_API_CTGRY/KOA_TB_API_DEF/KOA_TB_API_PARAM에
 *              INSERT할 때 필요한 필드만 담는다.
 * </pre>
 */
@Getter
@Setter
@ToString
public class ApiQuickRegVO {

    // 템플릿 선택
    private String tmpltNo;

    // KOA_TB_API_SPC
    private String apiSpcNo;
    private String apiNm;
    private String apiDesc;
    private String ver;
    private String sysId;
    private String autId;
    private String host;
    private String basPath;
    private String apiClass;      // APIGUB1000 comn_cd
    private String bstgwYn;
    private String apiVeriBaseurl;

    // KOA_TB_API_CTGRY
    private String ctgryNm;
    private String apiCtgryNo;

    // KOA_TB_API_DEF
    private String apiNo;
    private String apiPath;
    private String methodCd;      // MTHTYP1000 comn_cd
    private String apiHandlerCd;  // Private 전용, APIHDR1000 comn_cd
    private String providerSeq;

    // 파라미터 목록 (KOA_TB_API_PARAM)
    private List<ApiQuickParamVO> paramList;

    // 등록자 (세션)
    private String regr;
    private String amdr;

    @Getter
    @Setter
    @ToString
    public static class ApiQuickParamVO {
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
