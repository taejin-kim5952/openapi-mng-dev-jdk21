package com.kt.openapi.web.tmplt.service;

import com.kt.openapi.web.tmplt.vo.ApiTmpltVO;

import java.util.List;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.tmplt.service
 * 2. 타입명   : ApiTmpltService.java
 * 5. 설명     : API 등록 템플릿 관리 화면 전용 서비스.
 *              템플릿을 "사용하는" 쪽(API 등록 화면)은 화면 독립 원칙에 따라 각자 조회하므로,
 *              여기에는 관리(목록/상세/저장/삭제) 기능만 둔다.
 * </pre>
 */
public interface ApiTmpltService {

    List<ApiTmpltVO> selTmpltMngList();

    ApiTmpltVO selTmpltDetail(String tmpltNo);

    /** 신규면 INSERT, tmpltNo가 있으면 UPDATE. 생성/수정된 tmpltNo를 반환 */
    String savTmplt(ApiTmpltVO vo);

    void delTmplt(String tmpltNo);
}
