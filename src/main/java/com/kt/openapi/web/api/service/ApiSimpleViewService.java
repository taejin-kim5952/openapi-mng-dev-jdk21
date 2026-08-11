package com.kt.openapi.web.api.service;

import com.kt.openapi.web.api.vo.ApiSimpleDefVO;
import com.kt.openapi.web.api.vo.ApiSimpleSpcVO;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.api.service
 * 2. 타입명   : ApiSimpleViewService.java
 * 5. 설명     : "간단 상세" 화면 전용 서비스. 기존 ApiInfoService/ApiRegService와는 독립적으로 동작한다.
 * </pre>
 */
public interface ApiSimpleViewService {

    ApiSimpleSpcVO selSpcEssential(String apiSpcNo);

    void savSpcEssential(ApiSimpleSpcVO vo);

    List<ApiSimpleDefVO> selDefList(String apiSpcNo);

    ApiSimpleDefVO selDefDetail(String apiNo);

    List<Map<String, Object>> selParamList(String apiNo);

    void savDefDetail(ApiSimpleDefVO vo);

    /** 파라미터 목록 저장 (기존 전체 삭제 후 재등록) */
    void savDefParams(String apiNo, List<Map<String, Object>> paramList, String regr);
}
