package com.kt.openapi.web.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.api.vo
 * 2. 타입명   : ApiQuickTmpltVO.java
 * 5. 설명     : "빠른 API 등록" 화면의 템플릿(KOA_TB_API_QUICK_TMPLT) VO.
 *              dfltParamJson은 프론트에서 그대로 JSON.parse해서 파라미터 초기값으로 사용한다.
 * </pre>
 */
@Getter
@Setter
@ToString
public class ApiQuickTmpltVO {
    private String tmpltNo;
    private String tmpltNm;
    private String tmpltDesc;
    private String apiClass;
    private String mthTypeCd;
    private String cntTypeCd;
    private String pathPattern;
    private String dfltParamJson;
    private String dfltFieldJson;
    private String sortOdrg;
    private String tmpltYaml;
    private String useYn;
    private String regr;
    private String amdr;
}
