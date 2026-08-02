package com.kt.openapi.web.bbs.cmn.dao;

import com.kt.openapi.web.bbs.cmn.vo.BbsAtcVo;
import com.kt.openapi.web.bbs.cmn.vo.BbsCommentVo;
import com.kt.openapi.web.bbs.cmn.vo.BbsSaveVo;
import com.kt.openapi.web.bbs.cmn.vo.BbsSearchVo;
import com.kt.openapi.web.bbs.cmn.vo.BbsVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 게시판 공통 관련 MyBatis Mapper 인터페이스
 * [마이그레이션] EgovAbstractDAO 상속 구조를 제거하고 @Mapper 인터페이스로 전환
 */
@Mapper
public interface BbsCmnDAO {

	/**
	 * 공지사항 목록 조회
	 */
	List<BbsVo> selNoticeList(BbsSearchVo svo);

	/**
	 * 공지 전체 갯수
	 */
	int selNoticeListCnt(BbsSearchVo param);

	/**
	 * 공지사항 상세조회
	 */
	BbsVo selNoticeView(BbsSearchVo param);

	/**
	 * 공지사항 첨부 파일 조회
	 */
	List<BbsAtcVo> selNoticeFileList(BbsSearchVo param);

	/**
	 * 개발자 포럼 목록 조회
	 */
	List<BbsVo> selForumList(BbsSearchVo param);

	/**
	 * 개발자 포럼 목록 전체 갯수
	 */
	int selForumListCnt(BbsSearchVo param);

	/**
	 * 답글 목록 조회
	 */
	List<BbsCommentVo> selCommentList(BbsSearchVo param);

	/**
	 * 개발자 포럼 답글 등록
	 */
	String saveForumComment(BbsCommentVo param);

	/**
	 * 개발자 포럼 답글 수정
	 */
	int updForumComment(BbsCommentVo param);

	/**
	 * 개발자 포럼 답글 삭제
	 */
	int delForumCommentAjax(BbsCommentVo param);
	
	/**
	 * 본인 글 여부 체크
	 */
	int checkOwnBbs(BbsSearchVo param);
	
	/**
	 * 본인 답글 여부 체크
	 */
	int checkBbsComent(BbsCommentVo param);

	/**
	 * 개발자 포럼 글 삭제
	 */
	int delForumAjax(BbsSearchVo vo);

	/**
	 * 개발자 포럼 글 등록
	 */
	String saveForum(BbsSaveVo vo);

	/**
	 * 조회수 증가
	 */
	void updReadCnt(BbsSearchVo param);

	/**
	 * 개발자 포럼 글 수정
	 */
	int updForum(BbsSaveVo bbsSaveVo);
}
