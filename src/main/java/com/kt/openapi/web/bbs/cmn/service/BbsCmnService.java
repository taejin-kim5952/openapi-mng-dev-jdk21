package com.kt.openapi.web.bbs.cmn.service;

import java.util.List;

import com.kt.openapi.web.bbs.cmn.vo.BbsAtcVo;
import com.kt.openapi.web.bbs.cmn.vo.BbsCommentVo;
import com.kt.openapi.web.bbs.cmn.vo.BbsSaveVo;
import com.kt.openapi.web.bbs.cmn.vo.BbsSearchVo;
import com.kt.openapi.web.bbs.cmn.vo.BbsVo;

/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.bbs.notice.service
* 2. 타입명 : BbsNotiService.java
* 3. 작성일 : 2017. 11. 10. 오후 2:52:11
* 4. 작성자 : user
* 5. 설명 : 게시판 - 공지사항, 개발자 포럼
* </pre>
*/
public interface BbsCmnService {

	/**
	* <pre>
	* 1. 메소드명 : selNoticeList
	* 2. 작성일 : 2017. 11. 10. 오후 2:52:03
	* 3. 작성자 : user
	* 4. 설명 : 공지사항 목록 조회
	* </pre>
	* @param svo
	* @return
	* @throws Exception
	*/
	List<BbsVo> selNoticeList(BbsSearchVo svo) throws Exception;

	/**
	* <pre>
	* 1. 메소드명 : selNoticeListCnt
	* 2. 작성일 : 2017. 11. 10. 오후 4:00:27
	* 3. 작성자 : user
	* 4. 설명 : 공지사항 목록 전체 갯수
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	int selNoticeListCnt(BbsSearchVo param)  throws Exception;

	/**
	* <pre>
	* 1. 메소드명 : selNoticeView
	* 2. 작성일 : 2017. 11. 13. 오후 1:09:24
	* 3. 작성자 : user
	* 4. 설명 : 공지사항 상세보기 조회
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	BbsVo selNoticeView(BbsSearchVo param)  throws Exception;

	/**
	* <pre>
	* 1. 메소드명 : selNoticeFileList
	* 2. 작성일 : 2017. 11. 13. 오후 1:17:16
	* 3. 작성자 : user
	* 4. 설명 : 공지사항 첨부파일 목록 조회
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	List<BbsAtcVo> selNoticeFileList(BbsSearchVo param)  throws Exception;

	/**
	* <pre>
	* 1. 메소드명 : selForumList
	* 2. 작성일 : 2017. 11. 13. 오후 7:08:46
	* 3. 작성자 : user
	* 4. 설명 : 개발자 포럼 목록 조회
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	List<BbsVo> selForumList(BbsSearchVo param) throws Exception;

	/**
	* <pre>
	* 1. 메소드명 : selForumListCnt
	* 2. 작성일 : 2017. 11. 13. 오후 7:09:59
	* 3. 작성자 : user
	* 4. 설명 : 개발자 포럼 목록 전체 갯수
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	int selForumListCnt(BbsSearchVo param) throws Exception;

	/**
	* <pre>
	* 1. 메소드명 : selCommentList
	* 2. 작성일 : 2017. 11. 14. 오후 1:37:26
	* 3. 작성자 : user
	* 4. 설명 : 답글 목록 조회
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	List<BbsCommentVo> selCommentList(BbsSearchVo param) throws Exception;

	/**
	* <pre>
	* 1. 메소드명 : saveForumComment
	* 2. 작성일 : 2017. 11. 14. 오후 1:56:31
	* 3. 작성자 : user
	* 4. 설명 : 개발자 포럼 답글 등록
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	String saveForumComment(BbsCommentVo param)  throws Exception;
    
	/**
	* <pre>
	* 1. 메소드명 : updForumComment
	* 2. 작성일 : 2017. 11. 14. 오후 2:25:39
	* 3. 작성자 : user
	* 4. 설명 : 개발자 포럼 답글 수정
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	int updForumComment(BbsCommentVo param)   throws Exception;
    
	/**
	* <pre>
	* 1. 메소드명 : delForumCommentAjax
	* 2. 작성일 : 2017. 11. 14. 오후 2:34:55
	* 3. 작성자 : user
	* 4. 설명 : 답글 삭제
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	int delForumCommentAjax(BbsCommentVo param)  throws Exception;

	/**
	* <pre>
	* 1. 메소드명 : delForumAjax
	* 2. 작성일 : 2017. 11. 16. 오후 2:56:49
	* 3. 작성자 : user
	* 4. 설명 : 개발자 포럼 글 삭제
	* </pre>
	* @param bbsSaveVo
	* @return
	* @throws Exception
	*/
	int delForumAjax(BbsSearchVo vo) throws Exception;

	/**
	* <pre>
	* 1. 메소드명 : saveForum
	* 2. 작성일 : 2017. 11. 16. 오후 4:02:26
	* 3. 작성자 : user
	* 4. 설명 : 개발자 포럼 글 등록
	* </pre>
	* @param bbsSaveVo
	* @return
	* @throws Exception
	*/
	String saveForum(BbsSaveVo bbsSaveVo)  throws Exception;

	/**
	* <pre>
	* 1. 메소드명 : updReadCnt
	* 2. 작성일 : 2017. 11. 16. 오후 5:27:46
	* 3. 작성자 : user
	* 4. 설명 : 조회수 증가 
	* </pre>
	* @param param
	* @throws Exception
	*/
	void updReadCnt(BbsSearchVo param) throws Exception;

	/**
	* <pre>
	* 1. 메소드명 : updForum
	* 2. 작성일 : 2017. 11. 16. 오후 5:51:19
	* 3. 작성자 : user
	* 4. 설명 : 개발자 포럼 글 수정
	* </pre>
	* @param bbsSaveVo
	* @return
	* @throws Exception
	*/
	int updForum(BbsSaveVo bbsSaveVo) throws Exception;

	int checkOwnBbs(BbsSearchVo bbsSearchVo) throws Exception;
	
	int checkBbsComent(BbsCommentVo bbsSearchVo) throws Exception;

}
