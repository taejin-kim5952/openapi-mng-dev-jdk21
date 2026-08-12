package com.kt.openapi.web.spcreg.dao;

import com.kt.openapi.web.spcreg.vo.SpcRegVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.spcreg.dao
 * 2. 타입명   : SpcRegDAO.java
 * 5. 설명     : "SPC 등록" 화면 전용 MyBatis Mapper. KOA_TB_API_SPC에 대한 자체 INSERT/조회만 담당한다.
 * </pre>
 */
@Mapper
public interface SpcRegDAO {

    /** 선택한 서비스(sysId)의 기존 SPC + 소속 API 목록 (좌측 참고 트리, 읽기 전용) */
    List<Map<String, Object>> selSysSpcTree(String sysId);

    /** KOA_TB_API_SPC 등록 (생성된 PK는 selectKey로 vo.apiSpcNo에 채워짐) */
    int savApiSpc(SpcRegVO vo);

    /** 그룹 정보 수정 화면에 불러올 SPC 1건의 전체 상세 */
    Map<String, Object> selSpcDetail(String apiSpcNo);

    /** KOA_TB_API_SPC 수정 */
    int updApiSpc(SpcRegVO vo);
}
