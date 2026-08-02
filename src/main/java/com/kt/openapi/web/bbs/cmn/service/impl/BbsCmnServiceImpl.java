package com.kt.openapi.web.bbs.cmn.service.impl;

import com.kt.openapi.web.bbs.cmn.dao.BbsCmnDAO;
import com.kt.openapi.web.bbs.cmn.service.BbsCmnService;
import com.kt.openapi.web.bbs.cmn.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.bbs.notice.service.impl
* 2. 타입명 : BbsNotiServiceImpl.java
* 3. 작성일 : 2017. 11. 10. 오후 2:54:32
* 4. 작성자 : user
* 5. 설명 : 게시판 - 공지사항
* </pre>
*/
@Service("bbsCmnService")
public class BbsCmnServiceImpl implements BbsCmnService {
	
	private static final Logger LOG = LoggerFactory.getLogger(BbsCmnServiceImpl.class);

	@Autowired
	private BbsCmnDAO bbsCmnDAO;
	
	/* (non-Javadoc)
	 * @see com.kt.openapi.web.bbs.notice.service.BbsNotiService#selNoticeList(com.kt.openapi.web.bbs.notice.vo.BbsSearchVo)
	 */
	@Override
	public List<BbsVo> selNoticeList(BbsSearchVo svo) throws Exception {
		return bbsCmnDAO.selNoticeList(svo);
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.bbs.notice.service.BbsNotiService#selNoticeListCnt(com.kt.openapi.web.bbs.notice.vo.BbsSearchVo)
	 */
	@Override
	public int selNoticeListCnt(BbsSearchVo param) throws Exception {
		return bbsCmnDAO.selNoticeListCnt(param);
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.bbs.notice.service.BbsNotiService#selNoticeView(com.kt.openapi.web.bbs.notice.vo.BbsSearchVo)
	 */
	@Override
	public BbsVo selNoticeView(BbsSearchVo param) throws Exception {
		return bbsCmnDAO.selNoticeView(param);
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.bbs.cmn.service.BbsCmnService#selNoticeFileList(com.kt.openapi.web.bbs.cmn.vo.BbsSearchVo)
	 */
	@Override
	public List<BbsAtcVo> selNoticeFileList(BbsSearchVo param) throws Exception {
		return bbsCmnDAO.selNoticeFileList(param);
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.bbs.cmn.service.BbsCmnService#selForumList(com.kt.openapi.web.bbs.cmn.vo.BbsSearchVo)
	 */
	@Override
	public List<BbsVo> selForumList(BbsSearchVo param) throws Exception {
		return bbsCmnDAO.selForumList(param);
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.bbs.cmn.service.BbsCmnService#selForumListCnt(com.kt.openapi.web.bbs.cmn.vo.BbsSearchVo)
	 */
	@Override
	public int selForumListCnt(BbsSearchVo param) throws Exception {
		return bbsCmnDAO.selForumListCnt(param);
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.bbs.cmn.service.BbsCmnService#selCommentList(com.kt.openapi.web.bbs.cmn.vo.BbsSearchVo)
	 */
	@Override
	public List<BbsCommentVo> selCommentList(BbsSearchVo param) throws Exception {
		return bbsCmnDAO.selCommentList(param);
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.bbs.cmn.service.BbsCmnService#saveForumComment(com.kt.openapi.web.bbs.cmn.vo.BbsCommentVo)
	 */
	@Override
	public String saveForumComment(BbsCommentVo param) throws Exception {
		return bbsCmnDAO.saveForumComment(param);
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.bbs.cmn.service.BbsCmnService#updForumComment(com.kt.openapi.web.bbs.cmn.vo.BbsCommentVo)
	 */
	@Override
	public int updForumComment(BbsCommentVo param) throws Exception {
		return bbsCmnDAO.updForumComment(param);
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.bbs.cmn.service.BbsCmnService#delForumCommentAjax(com.kt.openapi.web.bbs.cmn.vo.BbsCommentVo)
	 */
	@Override
	public int delForumCommentAjax(BbsCommentVo param) throws Exception {
		return bbsCmnDAO.delForumCommentAjax(param);
	}
	
	@Override
	public int checkOwnBbs(BbsSearchVo param) throws Exception {
		return bbsCmnDAO.checkOwnBbs(param);
	}
	
	@Override
	public int checkBbsComent(BbsCommentVo param) throws Exception {
		return bbsCmnDAO.checkBbsComent(param);
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.bbs.cmn.service.BbsCmnService#delForumAjax(com.kt.openapi.web.bbs.cmn.vo.BbsSaveVo)
	 */
	@Override
	public int delForumAjax(BbsSearchVo vo) throws Exception {
		return bbsCmnDAO.delForumAjax(vo);
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.bbs.cmn.service.BbsCmnService#saveForum(com.kt.openapi.web.bbs.cmn.vo.BbsSaveVo)
	 */
	@Override
	public String saveForum(BbsSaveVo vo) throws Exception {
		return bbsCmnDAO.saveForum(vo);
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.bbs.cmn.service.BbsCmnService#updReadCnt(com.kt.openapi.web.bbs.cmn.vo.BbsSearchVo)
	 */
	@Override
	public void updReadCnt(BbsSearchVo param) throws Exception {
		 bbsCmnDAO.updReadCnt(param);
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.bbs.cmn.service.BbsCmnService#updForum(com.kt.openapi.web.bbs.cmn.vo.BbsSaveVo)
	 */
	@Override
	public int updForum(BbsSaveVo bbsSaveVo) throws Exception {
		return bbsCmnDAO.updForum(bbsSaveVo);
	}
	
}
