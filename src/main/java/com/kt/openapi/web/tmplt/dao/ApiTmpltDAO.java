package com.kt.openapi.web.tmplt.dao;

import com.kt.openapi.web.tmplt.vo.ApiTmpltVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.tmplt.dao
 * 2. 타입명   : ApiTmpltDAO.java
 * 5. 설명     : API 등록 템플릿 관리 화면 전용 MyBatis Mapper.
 *              KOA_TB_API_QUICK_TMPLT 한 테이블만 담당한다.
 * </pre>
 */
@Mapper
public interface ApiTmpltDAO {

    /** 템플릿 전체 목록 (사용여부 무관, 템플릿 관리 화면용) */
    List<ApiTmpltVO> selTmpltMngList();

    /** 템플릿 상세(파라미터 기본값 포함) */
    ApiTmpltVO selTmpltDetail(String tmpltNo);

    /** 템플릿 신규 등록 (생성된 PK는 selectKey로 vo.tmpltNo에 채워짐) */
    int savTmplt(ApiTmpltVO vo);

    /** 템플릿 수정 */
    int updTmplt(ApiTmpltVO vo);

    /** 템플릿 소프트 삭제 (USE_YN='N') */
    int delTmplt(String tmpltNo);
}
