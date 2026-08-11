package com.kt.openapi.web.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.api.vo
 * 2. 타입명   : ApiSimpleSpcVO.java
 * 5. 설명     : "간단 상세" 화면의 스펙(KOA_TB_API_SPC) 필수정보 VO.
 *              기존 ApiRegVO/ApiDefaultVO와는 독립적으로, 이 화면에 필요한 필드만 담는다.
 * </pre>
 */
@Getter
@Setter
@ToString
public class ApiSimpleSpcVO {
    private String apiSpcNo;
    private String apiNm;
    private String ver;
    private String sysId;
    private String sysNm;
    private String apiDesc;
    private String host;
    private String basPath;
    private String regSttusCd;
    private String apiClass;
    private String regr;
    private String amdr;
}
