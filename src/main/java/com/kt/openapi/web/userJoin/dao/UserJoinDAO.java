package com.kt.openapi.web.userJoin.dao;

import com.kt.openapi.web.userJoin.vo.UserHistVO;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.userJoin.dao
 * 2. 타입명   : UserJoinDAO.java
 * 3. 작성일   : 2017. 11. 30. 오후 2:52:51
 * 4. 작성자   : ANEUNTAEK
 * 5. 설명     : 회원가입 관련 MyBatis Mapper 인터페이스
 * </pre>
 */
@Mapper
public interface UserJoinDAO {

    /** 회원 가입 등록 */
    void insertUserJoin(UserJoinVO param);

    /** 회원 가입 완료 정보 조회 */
    UserJoinVO selectUserJoin(UserJoinVO param);

    /** 회원 가입 이력 저장 */
    void insertMgtHist(UserHistVO param);
}
