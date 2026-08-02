package com.kt.openapi.web.mypage.dao;

import com.kt.openapi.web.mypage.vo.MypageVO;
import com.kt.openapi.web.userJoin.vo.UserHistVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.mypage.dao
 * 2. 타입명   : MypageDAO.java
 * 3. 작성일   : 2017. 11. 30. 오후 2:24:32
 * 4. 작성자   : ANEUNTAEK
 * 5. 설명     : MYPAGE 관련 MyBatis Mapper 인터페이스
 * </pre>
 */
@Mapper
public interface MypageDAO {

    /** API 검토 요청 건수 및 API 개발 요청 건수 */
    int getApiRevCnt(MypageVO param) throws Exception;

    /** 작성중 API 상세보기 */
    Map<String, Object> selWriteApi(MypageVO param) throws Exception;

    /** 나의 개발자 포럼 최신글 1개 */
    Map<String, Object> selDevFView(MypageVO param) throws Exception;

    /** 나의 개발자 포럼 댓글 개수 */
    int selDevFCnt(MypageVO param) throws Exception;

    /** 나의 Q&A 상세글 */
    Map<String, Object> selQAView(MypageVO param) throws Exception;

    /** 나의 보유 권한 최대 5개 */
    List<Map<String, Object>> selAutList(MypageVO svo) throws Exception;

    /** 권한요청 상태 최대 5개 */
    List<Map<String, Object>> selAutReq(MypageVO svo) throws Exception;

    /** 권한 요청 등록 */
    void newAutReq(MypageVO param) throws Exception;

    /** 시스템 목록 */
    List<Map<String, Object>> selboxSysNm(MypageVO param) throws Exception;

    /** 권한 그룹 목록 */
    List<Map<String, Object>> selboxAGroup(MypageVO param) throws Exception;

    /** 반려 사유 */
    Map<String, Object> selBack(MypageVO param) throws Exception;

    /** 회원 관리 이력 */
    void autHist(UserHistVO param) throws Exception;

    /** 회원 상태 수정 */
    void updateMbr(MypageVO param) throws Exception;

    /** 권한 요청 등록시 개수 체크 */
    int chkInsCnt(MypageVO param) throws Exception;

    /** 권한 중복 체크 */
    int chkDupCnt(MypageVO mypageVo) throws Exception;
}
