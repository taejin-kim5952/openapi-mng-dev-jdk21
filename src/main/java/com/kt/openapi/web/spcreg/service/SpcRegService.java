package com.kt.openapi.web.spcreg.service;

import com.kt.openapi.web.spcreg.vo.SpcRegVO;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.spcreg.service
 * 2. 타입명   : SpcRegService.java
 * 5. 설명     : "SPC 등록" 화면 전용 서비스.
 * </pre>
 */
public interface SpcRegService {

    List<Map<String, Object>> selSysSpcTree(String sysId);

    /** SPC 1건을 등록하고, 생성된 apiSpcNo를 반환 */
    String savSpcReg(SpcRegVO vo);

    /** 그룹 정보 수정 화면에 불러올 SPC 1건의 전체 상세 */
    Map<String, Object> selSpcDetail(String apiSpcNo);

    /** SPC 1건을 수정하고, apiSpcNo를 반환 */
    String updSpcReg(SpcRegVO vo);
}
