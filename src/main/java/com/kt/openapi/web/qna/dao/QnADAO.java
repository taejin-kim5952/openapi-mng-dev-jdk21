package com.kt.openapi.web.qna.dao;

import com.kt.openapi.web.qna.vo.QnAFileVO;
import com.kt.openapi.web.qna.vo.QnASaveVO;
import com.kt.openapi.web.qna.vo.QnASearchVO;
import com.kt.openapi.web.qna.vo.QnAVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.qna.dao
* 2. 타입명   : QnADAO.java
* 3. 작성일   : 2017. 11. 30. 오후 2:14:17
* 4. 작성자   : user
* 5. 설명     : QNA 관련 MyBatis Mapper 인터페이스
* </pre>
*/
@Mapper
public interface QnADAO {

    /** QNA 목록 조회 */
    List<QnAVO> selQnAList(QnASearchVO qnaSeVO) throws Exception;

    /** qna 전체 목록 갯수 조회 */
    int selQnaListCnt(QnASearchVO param) throws Exception;

    /** qna 상세보기 */
    QnAVO selQnaView(QnASearchVO param) throws Exception;

    /** qna 글 삭제 */
    int delForumAjax(QnASearchVO param) throws Exception;

    /** 본인이 등록한 글인지 체크 */
    int checkOwnQna(QnASearchVO param) throws Exception;

    /** qna 첨부파일 목록 */
    List<QnAFileVO> selQnaFileList(QnASearchVO param) throws Exception;

    /** qna 글 등록 */
    int saveQna(QnASaveVO param) throws Exception;

    /** 첨부파일 등록 */
    int qnaFileReg(QnAFileVO fvo) throws Exception;

    /** 첨부파일 삭제 */
    int delQnaFile(QnASaveVO qnASaveVO) throws Exception;

    /** qna 글 수정 */
    int updQna(QnASaveVO qnASaveVO) throws Exception;
}
