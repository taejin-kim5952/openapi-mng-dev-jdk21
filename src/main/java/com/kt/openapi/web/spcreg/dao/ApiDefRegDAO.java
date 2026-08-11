package com.kt.openapi.web.spcreg.dao;

import com.kt.openapi.web.spcreg.vo.ApiDefRegVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.spcreg.dao
 * 2. 타입명   : ApiDefRegDAO.java
 * 5. 설명     : "API 등록"(기존 SPC에 API 추가) 화면 전용 MyBatis Mapper. SPC는 만들지 않고,
 *              KOA_TB_API_CTGRY(재사용/최초생성)/KOA_TB_API_DEF/KOA_TB_API_PARAM에 대한
 *              자체 INSERT/조회만 담당한다.
 * </pre>
 */
@Mapper
public interface ApiDefRegDAO {

    /** 선택한 서비스(sysId)에 이미 등록된 API 목록 (좌측 참고 트리, 읽기 전용) */
    List<Map<String, Object>> selSysApiTree(String sysId);

    /** apiSpcNo 1건의 그룹 정보(sysId/host/basPath 등) - 화면 진입 시 유효성 검증 + 컨텍스트 조회용 */
    Map<String, Object> selSpcByNo(String apiSpcNo);

    /** 선택한 apiSpcNo에 이미 있는 첫 카테고리 조회 (없으면 null - 그 그룹의 첫 API라는 뜻) */
    String selDefaultCtgryBySpc(String apiSpcNo);

    /** KOA_TB_API_CTGRY 기본 카테고리 등록 (생성된 PK는 selectKey로 vo.apiCtgryNo에 채워짐) */
    int savApiCtgry(ApiDefRegVO vo);

    /** KOA_TB_API_DEF 등록 (생성된 PK는 selectKey로 vo.apiNo에 채워짐) */
    int savApiDef(ApiDefRegVO vo);

    /** KOA_TB_API_PARAM 등록 (파라미터 1건, Map: apiNo/paramNm/dataTypeCd/required/paramDesc/sortOdrg/regr) */
    int savApiParam(Map<String, Object> params);
}
