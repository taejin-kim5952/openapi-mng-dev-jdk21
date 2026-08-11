package com.kt.openapi.web.spcreg.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.spcreg.vo
 * 2. 타입명   : SpcRegVO.java
 * 5. 설명     : "SPC 등록" 화면 전용 VO. KOA_TB_API_SPC 1건을 만드는 데 필요한 필드만 담는다.
 *              API(Method+Path, KOA_TB_API_DEF) 등록은 이 화면 범위 밖(quickApiReg가 담당).
 * </pre>
 */
@Getter
@Setter
@ToString
public class SpcRegVO {

    // KOA_TB_API_SPC
    private String apiSpcNo;      // 생성 후 selectKey로 채워짐
    private String sysId;
    private String autId;
    private String apiNm;         // API 그룹 이름
    private String apiDesc;
    private String host;
    private String basPath;
    private String ver;
    private String apiSchema;     // http/https
    private String apiClass;      // APIGUB1000 comn_cd (Public/Private/Internal)
    private String apiVeriBaseurl;
    private String bstgwYn;

    // 등록자 (세션)
    private String regr;
    private String amdr;
}
