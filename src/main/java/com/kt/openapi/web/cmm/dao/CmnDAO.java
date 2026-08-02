package com.kt.openapi.web.cmm.dao;

import com.kt.openapi.web.cmm.vo.CmnCdVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 공통코드 관련 MyBatis Mapper 인터페이스
 * [마이그레이션] EgovAbstractDAO 상속 구조를 제거하고 @Mapper 인터페이스로 전환
 */
@Mapper
public interface CmnDAO {

    /**
     * 공통코드 목록 조회
     */
    List<CmnCdVO> selComnList(String groupCd);
}
