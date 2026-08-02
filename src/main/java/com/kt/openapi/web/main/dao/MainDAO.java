package com.kt.openapi.web.main.dao;

import com.kt.openapi.web.main.vo.MainBBSVO;
import com.kt.openapi.web.main.vo.MainVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.main.dao
 * 2. 타입명   : MainDAO.java
 * 3. 작성일   : 2017. 12. 1. 오후 5:54:13
 * 4. 작성자   : ANEUNTAEK
 * 5. 설명     : Main 관련 MyBatis Mapper 인터페이스
 * </pre>
 */
@Mapper
public interface MainDAO {

    /** 공지사항 최신글 3개 */
    List<MainBBSVO> selRecNotice(MainVO param) throws Exception;

    /** 개발자 포럼 최신글 3개 */
    List<MainBBSVO> selRecDevF(MainVO param) throws Exception;

}
