package com.kt.openapi.web.login.dao;

import com.kt.openapi.web.auth.vo.AuthVO;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * LOGIN 관련 MyBatis Mapper 인터페이스
 * [마이그레이션] @Mapper 어노테이션을 직접 사용하여 SqlSession 호출 없이 쿼리 연동
 */
@Mapper
public interface LoginDAO {

    /**
     * PSSO ID와 OPENAPI ID 일치 여부 확인
     */
    List<UserJoinVO> getUserIdChk(UserJoinVO param);
    
    /**
     * 최종 로그인 일시 수정
     */
    void updateLDate(UserJoinVO param);
    
    /**
     * 회원 정보 조회
     */
    UserJoinVO selUserInfo(UserJoinVO userJoinVo);
    
    /**
     * 권한 정보 조회
     */
    List<AuthVO> selAuthList(AuthVO authVo);
}
