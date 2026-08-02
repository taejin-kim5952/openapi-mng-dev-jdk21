package com.kt.openapi.web.faq.dao;

import com.kt.openapi.web.faq.vo.FaqVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.faq.dao
 * 2. 타입명   : FaqDAO.java
 * 3. 작성일   : 2017. 11. 30. 오후 1:40:58
 * 4. 작성자   : ANEUNTAEK
 * 5. 설명     : FAQ 관련 MyBatis Mapper 인터페이스
 * </pre>
 */
@Mapper
public interface FaqDAO {

    /** FAQ 목록 조회 */
    List<FaqVO> selFaqList(FaqVO param) throws Exception;

    /** FAQ TOP5 목록 조회 */
    List<FaqVO> selToplist(FaqVO param) throws Exception;

    /** FAQ 목록 개수 */
    int selfaqCnt(FaqVO param) throws Exception;

    /** FAQ TOP5 목록 개수 */
    int selTopCnt(FaqVO param) throws Exception;

    /** 조회수 증가 */
    void upRCnt(FaqVO param) throws Exception;
}
