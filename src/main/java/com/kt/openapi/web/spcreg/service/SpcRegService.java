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
}
