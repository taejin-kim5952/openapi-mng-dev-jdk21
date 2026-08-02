package com.kt.openapi.web.faq.controller;

import com.kt.openapi.web.cmm.service.CmnService;
import com.kt.openapi.web.faq.service.FaqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <pre>
* 1. 패키지명 : com.kt.openapi.web.faq.controller
* 2. 타입명 : FaqController.java
* 3. 작성일 : 2017. 11. 30. 오후 2:45:34
* 4. 작성자 : ANEUNTAEK
* 5. 설명 : FAQ
 * </pre>
 */

@Controller
@RequestMapping(value = "/faq")
public class FaqController {

	private static final Logger logger = LoggerFactory.getLogger(FaqController.class);

	@Value("${pageUnit:10}")
	private int pageUnit;

	@Value("${pageSize:10}")
	private int pageSize;

	@Autowired
	private FaqService faqService;

	@Autowired
	protected CmnService cmnService;

	/**
	 * 
	 * <pre>
	* 1. 메소드명 : faqView
	* 2. 작성일 : 2017. 11. 30. 오후 1:39:53
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : FAQ 목록
	 * </pre>
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @param session
	 * @return
	 * @throws Exception
	 */
	/*
	 * @RequestMapping(value = "/mvfaqList.do") public ModelAndView
	 * faqView(HttpServletRequest request, HttpServletResponse response, ModelMap
	 * model,HttpSession session) throws Exception {
	 * 
	 * ModelAndView mav = new ModelAndView(); logger.debug("Start mvfaqList Page");
	 * mav.setViewName("/faq/faqlist");
	 * 
	 * return mav; }
	 */

	/**
	 * 
	 * <pre>
	* 1. 메소드명 : faqListAjax
	* 2. 작성일 : 2017. 11. 30. 오후 1:40:18
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : FAQ 목록 AJAX
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
	 * @RequestMapping(value = "/mvfaqListAjax.do") public ModelAndView
	 * faqListAjax(HttpServletRequest request, HttpServletResponse response,
	 * ModelMap model, FaqVO param) throws Exception {
	 * 
	 * logger.
	 * debug("#######################  FaqController mvfaqListAjax START ############################"
	 * ); logger.debug("param:{} ", param);
	 * 
	 * param.setPageUnit(propertiesService.getInt("pageUnit"));
	 * param.setPageSize(propertiesService.getInt("pageSize")); PaginationInfo
	 * paginationInfo = new PaginationInfo();
	 * paginationInfo.setCurrentPageNo(param.getPageIndex());
	 * paginationInfo.setRecordCountPerPage(param.getPageUnit());
	 * paginationInfo.setPageSize(param.getPageSize());
	 * param.setFirstIndex(paginationInfo.getFirstRecordIndex());
	 * param.setLastIndex(paginationInfo.getLastRecordIndex());
	 * param.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());
	 * 
	 * List<EgovMap> faqlist = faqService.selFaqList(param);
	 * logger.debug("faqlist:{} ", faqlist); List<EgovMap> cmnclist =
	 * cmnService.selComnList("FAQCAT1000"); logger.debug("cmnclist:{} ", cmnclist);
	 * model.addAttribute("cmnCd", cmnclist);
	 * 
	 * model.addAttribute("faqlist", faqlist); int totCnt =
	 * faqService.selfaqCnt(param); logger.debug("totCnt:{} ", totCnt);
	 * 
	 * paginationInfo.setTotalRecordCount(totCnt);
	 * model.addAttribute("paginationInfo", paginationInfo);
	 * 
	 * return new ModelAndView( "jsonView", model ); }
	 */

	/**
	 * <pre>
	* 1. 메소드명 : faqCateAjax
	* 2. 작성일 : 2017. 12. 12. 오후 3:49:15
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 최초 카테고리 조회
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
	 * @RequestMapping(value = "/faqCateAjax.do") public ModelAndView
	 * faqCateAjax(HttpServletRequest request, HttpServletResponse response,
	 * ModelMap model, FaqVO param) throws Exception {
	 * 
	 * logger.
	 * debug("#######################  FaqController faqCateAjax START ############################"
	 * ); logger.debug("param:{} ", param); List<EgovMap> cmnclist =
	 * cmnService.selComnList("FAQCAT1000"); logger.debug("cmnclist:{} ", cmnclist);
	 * model.addAttribute("cmnCd", cmnclist);
	 * 
	 * return new ModelAndView("jsonView", model); }
	 */

	/**
	 * 
	 * <pre>
	* 1. 메소드명 : faqTopListAjax
	* 2. 작성일 : 2017. 11. 30. 오후 1:40:33
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : FAQ TOP 5 목록
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
	 * @RequestMapping(value = "/mvfaqTopListAjax.do") public ModelAndView
	 * faqTopListAjax(HttpServletRequest request, HttpServletResponse response,
	 * ModelMap model, FaqVO param) throws Exception { List<EgovMap> toplist =
	 * faqService.selToplist(param); logger.debug("toplist:{} ", toplist);
	 * model.addAttribute("toplist", toplist); int topTotCnt =
	 * faqService.selTopCnt(param); logger.debug("topTotCnt:{} ", topTotCnt); return
	 * new ModelAndView("jsonView", model); }
	 */

	/**
	 * <pre>
	* 1. 메소드명 : upRCntAjax
	* 2. 작성일 : 2018. 1. 4. 오후 8:57:06
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 조회수 증가
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
	 * @RequestMapping(value = "/upRCntAjax.do") public ModelAndView
	 * upRCntAjax(HttpServletRequest request, HttpServletResponse response, ModelMap
	 * model, FaqVO param) throws Exception { logger.
	 * debug("#######################  FaqController upRCntAjax START ############################"
	 * ); logger.debug("param:{} ", param); faqService.upRCnt(param); String message
	 * = MessageUtil.getMsg("update.success.msg"); model.addAttribute("msg",
	 * message); return new ModelAndView("jsonView", model); }
	 */
}
