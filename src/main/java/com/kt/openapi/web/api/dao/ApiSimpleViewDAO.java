package com.kt.openapi.web.api.dao;

import com.kt.openapi.web.api.vo.ApiSimpleDefVO;
import com.kt.openapi.web.api.vo.ApiSimpleSpcVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.api.dao
 * 2. 타입명   : ApiSimpleViewDAO.java
 * 5. 설명     : "간단 상세" 화면 전용 MyBatis Mapper. 기존 ApiInfoDAO/ApiRegDAO와는 독립적으로,
 *              KOA_TB_API_SPC/DEF/PARAM에 대한 조회·수정을 새로 작성한다(스키마는 기존과 동일).
 * </pre>
 */
@Mapper
public interface ApiSimpleViewDAO {

    /** 스펙 필수정보 조회 */
    ApiSimpleSpcVO selSpcEssential(String apiSpcNo);

    /** 스펙 필수정보 수정 */
    int updSpcEssential(ApiSimpleSpcVO vo);

    /** 스펙에 속한 Path/Method 요약 목록 */
    List<ApiSimpleDefVO> selDefList(String apiSpcNo);

    /** Path/Method 상세(팝업용) */
    ApiSimpleDefVO selDefDetail(String apiNo);

    /** Path/Method 상세 수정 */
    int updDefDetail(ApiSimpleDefVO vo);

    /** 파라미터 목록 조회 */
    List<Map<String, Object>> selParamList(String apiNo);

    /** 파라미터 전체 삭제(저장 시 재구성을 위한 단순화된 방식) */
    int delParamsByApiNo(String apiNo);

    /** 파라미터 1건 등록 */
    int savParam(Map<String, Object> params);
}
