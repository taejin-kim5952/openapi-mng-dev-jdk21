package com.kt.openapi.web.qna.service;

import com.kt.openapi.web.qna.vo.QnAFileVO;
import com.kt.openapi.web.qna.vo.QnASaveVO;
import com.kt.openapi.web.qna.vo.QnASearchVO;
import com.kt.openapi.web.qna.vo.QnAVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.qna.service
* 2. 타입명 : QnAService.java
* 3. 작성일 : 2017. 11. 30. 오후 2:14:58
* 4. 작성자 : user
* 5. 설명 : QNA SERVICE INTERFACE
* </pre>
*/
public interface QnAService {


	/**
	* <pre>
	* 1. 메소드명 : selQnaList
	* 2. 작성일 : 2017. 11. 30. 오후 2:12:37
	* 3. 작성자 : user
	* 4. 설명 : QNA 목록 조회
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	List<QnAVO>  selQnaList(QnASearchVO param)  throws Exception;

	/**
	* <pre>
	* 1. 메소드명 : selQnaListCnt
	* 2. 작성일 : 2017. 11. 30. 오후 2:52:14
	* 3. 작성자 : user
	* 4. 설명 : QNA 목록 전체 갯수
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	int selQnaListCnt(QnASearchVO param) throws Exception;

	/**
	* <pre>
	* 1. 메소드명 : selQnaView
	* 2. 작성일 : 2017. 11. 30. 오후 3:36:04
	* 3. 작성자 : user
	* 4. 설명 : qna 상세보기
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	QnAVO selQnaView(QnASearchVO param) throws Exception;

	/**
	* <pre>
	* 1. 메소드명 : delForumAjax
	* 2. 작성일 : 2017. 11. 30. 오후 5:23:43
	* 3. 작성자 : user
	* 4. 설명 : qna 글 삭제
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	int delForumAjax(QnASearchVO param) throws Exception;

	/**
	* <pre>
	* 1. 메소드명 : selQnaFileList
	* 2. 작성일 : 2017. 11. 30. 오후 6:35:47
	* 3. 작성자 : user
	* 4. 설명 :  QNA 첨부 파일 
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	List<QnAFileVO>  selQnaFileList(QnASearchVO param)  throws Exception;

	/**
	* <pre>
	* 1. 메소드명 : saveQna
	* 2. 작성일 : 2017. 11. 30. 오후 8:29:02
	* 3. 작성자 : user
	* 4. 설명 : qna 글 등록
	* </pre>
	* @param qnASaveVO
	 * @param uploadFile 
	* @return
	* @throws Exception
	*/
	String saveQna(QnASaveVO qnASaveVO, MultipartFile uploadFile)  throws Exception;

	int updQna(QnASaveVO qnASaveVO, MultipartFile uploadFile)   throws Exception;

	int checkOwnQna(QnASearchVO param) throws Exception;

}
