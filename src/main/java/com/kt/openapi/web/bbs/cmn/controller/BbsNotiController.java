package com.kt.openapi.web.bbs.cmn.controller;

import com.kt.openapi.web.bbs.cmn.service.BbsCmnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <pre>
* 1. 패키지명 : com.kt.openapi.web.bbs.notice.controller
* 2. 타입명 : BbsNotiController.java
* 3. 작성일 : 2017. 11. 13. 오후 4:54:36
* 4. 작성자 : user
* 5. 설명 : 공지사항 게시판
 * </pre>
 */
@Controller
@RequestMapping(value = "/bbs/notice")
public class BbsNotiController {
	private static final Logger LOG = LoggerFactory.getLogger(BbsNotiController.class);

	@Autowired
	private BbsCmnService bbsCmnService;

	@Value("${pageUnit:10}")
	private int pageUnit;

	@Value("${pageSize:10}")
	private int pageSize;

	@Value("${noticePageUnit:5}")
	private int noticePageUnit;

	@Value("${noticePageSize:5}")
	private int noticePageSize;

	/**
	 * <pre>
	* 1. 메소드명 : mvNoticeList
	* 2. 작성일 : 2017. 11. 9. 오후 2:39:17
	* 3. 작성자 : user
	* 4. 설명 : 공지사항 목록 페이지
	 * </pre>
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	/*
	 * @RequestMapping(value="/mvNoticeList.do") public ModelAndView
	 * mvNoticeList(HttpServletRequest request,HttpServletResponse response,
	 * ModelMap model) throws Exception { LOG.
	 * debug("#######################  BbsNotiController mvNoticeList START ############################"
	 * ); ModelAndView mv = new ModelAndView(); mv.setViewName("bbs/notice/list");
	 * 
	 * return mv; }
	 */

	/**
	 * <pre>
	* 1. 메소드명 : selNoticeListAjax
	* 2. 작성일 : 2017. 11. 10. 오후 2:52:30
	* 3. 작성자 : user
	* 4. 설명 : 공지사항 목록 조회
	 * </pre>
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @param svo
	 * @return
	 * @throws Exception
	 */
	/*
	 * @RequestMapping(value="/selNoticeListAjax.do") public ModelAndView
	 * selNoticeListAjax(HttpServletRequest request,HttpServletResponse response,
	 * ModelMap model , BbsSearchVo param) throws Exception { LOG.
	 * debug("#######################  BbsNotiController selNoticeListAjax START ############################"
	 * ); param.setPageUnit(propertiesService.getInt("pageUnit")); // 페이지당 건수
	 * param.setPageSize(propertiesService.getInt("pageSize"));//페이지 리스트에 게시되는 건수
	 *//** pageing setting *//*
								 * PaginationInfo paginationInfo = new PaginationInfo();
								 * paginationInfo.setCurrentPageNo(param.getPageIndex()); // 현재 페이지 인덱스
								 * paginationInfo.setRecordCountPerPage(param.getPageUnit());
								 * paginationInfo.setPageSize(param.getPageSize());
								 * 
								 * param.setFirstIndex(paginationInfo.getFirstRecordIndex());
								 * param.setLastIndex(paginationInfo.getLastRecordIndex());
								 * param.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());
								 * model.addAttribute("nlist", bbsCmnService.selNoticeList(param));//목록 정보
								 * 
								 * int totCnt = bbsCmnService.selNoticeListCnt(param);
								 * paginationInfo.setTotalRecordCount(totCnt);
								 * model.addAttribute("paginationInfo", paginationInfo);//페이징 정보
								 * 
								 * return new ModelAndView( "jsonView", model ); }
								 */

	/**
	 * <pre>
	* 1. 메소드명 : mvNoticeView
	* 2. 작성일 : 2017. 11. 13. 오후 1:06:01
	* 3. 작성자 : user
	* 4. 설명 : 공지사항 상세보기
	 * </pre>
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @param param
	 * @return
	 * @throws Exception
	 */
	/*
	 * @RequestMapping(value = "/mvNoticeView.do") public ModelAndView
	 * mvNoticeView(HttpServletRequest request, HttpServletResponse response,
	 * ModelMap model, BbsSearchVo param) throws Exception { LOG.
	 * debug("#######################  BbsNotiController mvNoticeView START ############################"
	 * ); ModelAndView mv = new ModelAndView(); // 조회수 업데이트
	 * bbsCmnService.updReadCnt(param); // 상세보기 조회 model.addAttribute("vmap",
	 * bbsCmnService.selNoticeView(param)); // 첨부파일 조회 model.addAttribute("fList",
	 * bbsCmnService.selNoticeFileList(param)); mv.setViewName("bbs/notice/view");
	 * 
	 * return mv; }
	 */

}
