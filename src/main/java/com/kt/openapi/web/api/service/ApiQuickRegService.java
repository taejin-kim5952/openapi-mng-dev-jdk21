package com.kt.openapi.web.api.service;

import com.kt.openapi.web.api.vo.ApiQuickRegVO;
import com.kt.openapi.web.api.vo.ApiQuickTmpltVO;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.api.service
 * 2. 타입명   : ApiQuickRegService.java
 * 5. 설명     : "빠른 API 등록" 화면 전용 서비스. 기존 ApiRegService와는 독립적으로 동작한다.
 * </pre>
 */
public interface ApiQuickRegService {

    List<Map<String, Object>> selSysApiTree(String sysId);

    List<ApiQuickTmpltVO> selTmpltList();

    List<ApiQuickTmpltVO> selTmpltMngList();

    ApiQuickTmpltVO selTmpltDetail(String tmpltNo);

    /** 신규면 INSERT, tmpltNo가 있으면 UPDATE. 생성/수정된 tmpltNo를 반환 */
    String savTmplt(ApiQuickTmpltVO vo);

    void delTmplt(String tmpltNo);

    /** 기본정보+카테고리+Path/Method+파라미터까지 한 트랜잭션으로 등록하고, 생성된 apiSpcNo를 반환 */
    String savApiQuickReg(ApiQuickRegVO vo);
}
