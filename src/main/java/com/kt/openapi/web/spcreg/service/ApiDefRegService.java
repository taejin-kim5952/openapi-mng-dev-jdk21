package com.kt.openapi.web.spcreg.service;

import com.kt.openapi.web.spcreg.vo.ApiDefRegVO;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.spcreg.service
 * 2. 타입명   : ApiDefRegService.java
 * 5. 설명     : "API 등록"(기존 SPC에 API 추가) 화면 전용 서비스.
 * </pre>
 */
public interface ApiDefRegService {

    List<Map<String, Object>> selSysApiTree(String sysId);

    Map<String, Object> selSpcByNo(String apiSpcNo);

    /** 카테고리(재사용 또는 최초 1회 생성)+DEF+파라미터까지 한 트랜잭션으로 등록하고, apiSpcNo를 반환 */
    String savApiDefReg(ApiDefRegVO vo);
}
