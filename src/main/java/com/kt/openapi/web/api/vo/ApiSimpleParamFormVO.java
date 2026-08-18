package com.kt.openapi.web.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.api.vo
 * 2. 타입명   : ApiSimpleParamFormVO.java
 * 5. 설명     : "간단 상세" 팝업의 파라미터 목록 저장 폼 바인딩 전용 VO.
 *              paramList[n].xxx 형태의 indexed form 파라미터를 Spring이 자동 바인딩할 수 있도록
 *              커맨드 객체로 감싼다(ApiDefRegVO.ApiDefParamVO와 동일한 패턴).
 * </pre>
 */
@Getter
@Setter
@ToString
public class ApiSimpleParamFormVO {
    private String apiNo;
    private List<ApiSimpleParamVO> paramList;

    @Getter
    @Setter
    @ToString
    public static class ApiSimpleParamVO {
        private String paramNm;
        private String dataTypeCd;
        private String required;
        private String paramDesc;
        private String paramTypeCd;
        private String paramLoc;
        private String exam;
        private String personalData;
        // object/array 중첩 저장을 위한 클라이언트 전용 상관키 (실제 DB PK가 아직 없는 트리에서
        // 부모-자식 관계를 표현). 저장 시에만 쓰이고 실제 PRNTS_PARAM_NO로 치환된다.
        private String tempId;
        private String parentTempId;
    }
}
