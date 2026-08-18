package com.kt.openapi.web.tmplt.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.tmplt.vo
 * 2. 타입명   : ApiTmpltVO.java
 * 5. 설명     : API 등록 템플릿(KOA_TB_API_QUICK_TMPLT) VO.
 *              dfltParamJson은 프론트에서 그대로 JSON.parse해서 파라미터 초기값으로 사용한다.
 *              (구 ApiQuickTmpltVO - "빠른 API 등록" 화면 제거 시 이 패키지로 이관.
 *               테이블명은 운영 데이터가 있어 그대로 둔다.)
 * </pre>
 */
@Getter
@Setter
@ToString
public class ApiTmpltVO {
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
