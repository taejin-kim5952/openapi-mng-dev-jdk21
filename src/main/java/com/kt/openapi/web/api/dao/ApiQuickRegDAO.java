package com.kt.openapi.web.api.dao;

import com.kt.openapi.web.api.vo.ApiQuickRegVO;
import com.kt.openapi.web.api.vo.ApiQuickTmpltVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.api.dao
 * 2. 타입명   : ApiQuickRegDAO.java
 * 5. 설명     : "빠른 API 등록" 화면 전용 MyBatis Mapper. 기존 ApiRegDAO와는 독립적으로,
 *              KOA_TB_API_SPC/CTGRY/DEF/PARAM에 대한 자체 INSERT만 담당한다.
 * </pre>
 */
@Mapper
public interface ApiQuickRegDAO {

    /** 선택한 서비스(sysId)에 이미 등록된 API 목록 (좌측 참고 트리, 읽기 전용) */
    List<Map<String, Object>> selSysApiTree(String sysId);

    /** 사용 가능한 빠른등록 템플릿 목록 (use_yn='Y'만, 빠른등록 화면용) */
    List<ApiQuickTmpltVO> selTmpltList();

    /** 템플릿 전체 목록 (사용여부 무관, 템플릿 관리 화면용) */
    List<ApiQuickTmpltVO> selTmpltMngList();

    /** 템플릿 상세(파라미터 기본값 포함) */
    ApiQuickTmpltVO selTmpltDetail(String tmpltNo);

    /** 템플릿 신규 등록 (생성된 PK는 selectKey로 vo.tmpltNo에 채워짐) */
    int savTmplt(ApiQuickTmpltVO vo);

    /** 템플릿 수정 */
    int updTmplt(ApiQuickTmpltVO vo);

    /** 템플릿 소프트 삭제 (USE_YN='N') */
    int delTmplt(String tmpltNo);

    /** KOA_TB_API_SPC 등록 (생성된 PK는 selectKey로 vo.apiSpcNo에 채워짐) */
    int savApiSpc(ApiQuickRegVO vo);

    /** KOA_TB_API_CTGRY 기본 카테고리 등록 (생성된 PK는 selectKey로 vo.apiCtgryNo에 채워짐) */
    int savApiCtgry(ApiQuickRegVO vo);

    /** KOA_TB_API_DEF 등록 (생성된 PK는 selectKey로 vo.apiNo에 채워짐) */
    int savApiDef(ApiQuickRegVO vo);

    /** KOA_TB_API_PARAM 등록 (파라미터 1건, Map: apiNo/paramNm/dataTypeCd/required/paramDesc/sortOdrg/regr) */
    int savApiParam(Map<String, Object> params);
}
