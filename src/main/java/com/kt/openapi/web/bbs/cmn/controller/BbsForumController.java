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
* 2. 타입명 : BbsForumController.java
* 3. 작성일 : 2017. 11. 13. 오후 5:00:41
* 4. 작성자 : user
* 5. 설명 : 개발자 포럼
 * </pre>
 */
@Controller
@RequestMapping(value = "/bbs/forum")
public class BbsForumController {
	private static final Logger LOG = LoggerFactory.getLogger(BbsForumController.class);

	@Autowired
	private BbsCmnService bbsCmnService;

	@Value("${pageUnit:10}")
	private int pageUnit;

	@Value("${pageSize:10}")
	private int pageSize;

	/**
	 * <pre>
	* 1. 메소드명 : mvNoticeList
	* 2. 작성일 : 2017. 11. 9. 오후 2:39:17
	* 3. 작성자 : user
	* 4. 설명 : 개발자 포럼 목록 페이지
	 * </pre>
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	/*
	 * @RequestMapping(value="/mvForumList.do") public ModelAndView
	 * mvForumList(HttpServletRequest request,HttpServletResponse response, ModelMap
	 * model) throws Exception { LOG.
	 * debug("#######################  BbsForumController mvForumList START ############################"
	 * ); ModelAndView mv = new ModelAndView(); mv.setViewName("bbs/forum/list");
	 * 
	 * return mv; }
	 */
	/**
	 * <pre>
	* 1. 메소드명 : selForumListAjax
	* 2. 작성일 : 2017. 11. 10. 오후 2:52:30
	* 3. 작성자 : user
	* 4. 설명 : 개발자포럼 목록 조회
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
	 * @RequestMapping(value="/selForumListAjax.do") public ModelAndView
	 * selForumListAjax(HttpServletRequest request,HttpServletResponse response,
	 * ModelMap model , BbsSearchVo param) throws Exception { LOG.
	 * debug("#######################  BbsForumController selForumListAjax START ############################"
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
								 * model.addAttribute("nlist", bbsCmnService.selForumList(param));//목록 정보
								 * 
								 * int totCnt = bbsCmnService.selForumListCnt(param);
								 * paginationInfo.setTotalRecordCount(totCnt);
								 * model.addAttribute("paginationInfo", paginationInfo);//페이징 정보
								 * 
								 * return new ModelAndView( "jsonView", model ); }
								 */

	/**
	 * <pre>
	* 1. 메소드명 : mvForumView
	* 2. 작성일 : 2017. 11. 13. 오후 1:06:01
	* 3. 작성자 : user
	* 4. 설명 : 개발자 포럼 상세보기
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
	 * @RequestMapping(value = "/mvForumView.do") public ModelAndView
	 * mvForumView(HttpServletRequest request, HttpServletResponse response,
	 * ModelMap model, BbsSearchVo param) throws Exception { LOG.
	 * debug("#######################  BbsForumController mvForumView START ############################"
	 * ); ModelAndView mv = new ModelAndView(); // 조회수 업데이트
	 * bbsCmnService.updReadCnt(param); // 상세보기 조회 EgovMap vmap =
	 * bbsCmnService.selNoticeView(param); LOG.debug("regr :::: {} ",
	 * StringUtil.isNullToString(vmap.get("regr"))); vmap.put("regr",
	 * CommonFunc.safeDbDecrypt(StringUtil.isNullToString(vmap.get("regr"))));
	 * vmap.put("amdr",
	 * CommonFunc.safeDbDecrypt(StringUtil.isNullToString(vmap.get("amdr"))));
	 * 
	 * vmap.put("title",
	 * XSSFilter.fromXssText(StringUtil.isNullToString(vmap.get("title")) ) );
	 * vmap.put("sbst",
	 * XSSFilter.fromXssText(StringUtil.isNullToString(vmap.get("sbst")) ) );
	 * 
	 * 
	 * model.addAttribute("vmap", vmap); // 댓글 목록 조회 List<EgovMap> commentList =
	 * bbsCmnService.selCommentList(param); if (commentList != null &&
	 * commentList.size() != 0) { for (EgovMap fmap : commentList) { String regr =
	 * CommonFunc.safeDbDecrypt(StringUtil.isNullToString(fmap.get("regr")));
	 * LOG.debug("regr :: {}", regr); fmap.put("regr", regr); // 마스킹 처리 if
	 * (regr.length() > 3) { regr = regr.substring(0, regr.length() - 3); regr =
	 * regr + "***"; } LOG.debug("regr masking :: {}", regr);
	 * fmap.put("regrMasking", regr); } } model.addAttribute("commentList",
	 * commentList); mv.setViewName("bbs/forum/view");
	 * 
	 * return mv; }
	 */

	/**
	 * <pre>
	* 1. 메소드명 : mvForumReg
	* 2. 작성일 : 2017. 11. 13. 오후 7:46:18
	* 3. 작성자 : user
	* 4. 설명 : 개발자 포럼 글쓰기 페이지 이동
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
	 * @RequestMapping(value = "/mvForumReg.do") public ModelAndView
	 * mvForumReg(HttpServletRequest request, HttpServletResponse response, ModelMap
	 * model, BbsSaveVo bbsSaveVo) throws Exception { LOG.
	 * debug("#######################  BbsForumController mvForumReg START ############################"
	 * ); ModelAndView mv = new ModelAndView(); BbsSearchVo bbsSearchVo = new
	 * BbsSearchVo(); if (bbsSaveVo != null &&
	 * !"".equals(StringUtil.isNullToString(bbsSaveVo.getPstingId()))) {
	 * bbsSearchVo.setPstingId(bbsSaveVo.getPstingId());
	 * bbsSearchVo.setBbsTypeCd(bbsSaveVo.getBbsTypeCd());
	 * 
	 * bbsSearchVo.setImptYn("N"); bbsSearchVo.setPstingId(bbsSaveVo.getPstingId());
	 * EgovMap vmap = bbsCmnService.selNoticeView(bbsSearchVo); String regr =
	 * CommonFunc.safeDbDecrypt(StringUtil.isNullToString(vmap.get("regr")));
	 * vmap.put("regr", regr); LOG.debug("regr :: {}", regr); if (regr.length() > 3)
	 * { regr = regr.substring(0, regr.length() - 3); regr = regr + "***"; }
	 * LOG.debug("regr masking :: {}", regr); vmap.put("regrMasking", regr);
	 * 
	 * vmap.put("title",
	 * XSSFilter.fromXssText(StringUtil.isNullToString(vmap.get("title"))));
	 * vmap.put("sbst",
	 * XSSFilter.fromXssText(StringUtil.isNullToString(vmap.get("sbst"))));
	 * 
	 * model.addAttribute("vmap", vmap);// 상세 보기 조회
	 * bbsSaveVo.setTitle(StringUtil
	 * .isNullToString(XSSFilter.fromXssText(StringUtil.isNullToString(vmap.get(
	 * "title"))))); bbsSaveVo.setSbst(StringUtil
	 * .isNullToString(XSSFilter.fromXssText(StringUtil.isNullToString(vmap.get(
	 * "sbst"))))); LOG.debug("vmap.TITLE    : {}", vmap.get("title")); }
	 * model.addAttribute("bbsSaveVo", bbsSaveVo);
	 * mv.setViewName("bbs/forum/write");
	 * 
	 * return mv; }
	 */

	/**
	 * <pre>
	* 1. 메소드명 : saveForumReplyAjax
	* 2. 작성일 : 2017. 11. 14. 오후 1:33:41
	* 3. 작성자 : user
	* 4. 설명 : 답글 달기
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
	 * @RequestMapping(value = "/saveForumCommentAjax.do") public ModelAndView
	 * saveForumReplyAjax(HttpServletRequest request, HttpServletResponse response,
	 * ModelMap model, BbsCommentVo param) throws Exception { LOG.debug(
	 * "#######################  BbsForumController saveForumCommentAjax START ############################"
	 * ); UserJoinVO userVO = (UserJoinVO)
	 * request.getSession().getAttribute("ssUserVo");
	 * param.setRegr(userVO.getEnCmbrId());
	 * param.setSbst(XSSFilter.toText(StringUtil.isNullToString(param.getSbst())
	 * ));
	 * 
	 * if (!"".equals(StringUtil.isNullToString(param.getReplNo()))) { // 수정 int
	 * cnt = bbsCmnService.updForumComment(param); if (cnt == 0) { String message =
	 * MessageUtil.getMsg("fail.common.msg"); model.addAttribute("msg",
	 * message); } else { String message =
	 * MessageUtil.getMsg("insert.success.msg"); model.addAttribute("msg",
	 * message); } } else { // 등록 String replNo =
	 * bbsCmnService.saveForumComment(param); if
	 * ("".equals(StringUtil.isNullToString(replNo))) { String message =
	 * MessageUtil.getMsg("fail.common.msg"); model.addAttribute("msg",
	 * message); } else { String message =
	 * MessageUtil.getMsg("insert.success.msg"); model.addAttribute("msg",
	 * message); } } return new ModelAndView("jsonView", model); }
	 */

	/**
	 * <pre>
	* 1. 메소드명 : delForumCommentAjax
	* 2. 작성일 : 2017. 11. 14. 오후 2:33:43
	* 3. 작성자 : user
	* 4. 설명 : 답글 삭제
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
	 * @RequestMapping(value = "/delForumCommentAjax.do") public ModelAndView
	 * delForumCommentAjax(HttpServletRequest request, HttpServletResponse response,
	 * ModelMap model, BbsCommentVo param) throws Exception { LOG.
	 * debug("#######################  BbsForumController delForumCommentAjax START ############################"
	 * );
	 * 
	 * UserJoinVO userVO = (UserJoinVO)
	 * request.getSession().getAttribute("ssUserVo"); param.setAmdr((null != userVO)
	 * ? userVO.getMbrId() : null);
	 * 
	 * param.setAmdr(CommonFunc.safeDbEncrypt(StringUtil.isNullToString(param.
	 * getAmdr())));
	 * 
	 * try { Integer.parseInt(param.getPstingId()); } catch (NumberFormatException
	 * ex) { model.addAttribute("msg", "잘못된 요청입니다."); return new
	 * ModelAndView("jsonView", model); }
	 * 
	 * // 보안을 위해 해당 qnaId로 한번 더 조회해서 등록자와 일치해야 삭제 가능. int myCnt =
	 * bbsCmnService.checkBbsComent(param); if (myCnt > 0) { // 삭제 쿼리 int cnt =
	 * bbsCmnService.delForumCommentAjax(param); if (cnt == 0) { String message =
	 * MessageUtil.getMsg("del.fail.msg"); model.addAttribute("msg", message); }
	 * else { String message = MessageUtil.getMsg("del.success.msg");
	 * model.addAttribute("msg", message); } } else { model.addAttribute("msg",
	 * "본인이 등록한 게시글만 삭제 가능합니다."); } return new ModelAndView("jsonView", model); }
	 */

	/**
	 * <pre>
	* 1. 메소드명 : saveForum
	* 2. 작성일 : 2017. 11. 16. 오후 2:55:15
	* 3. 작성자 : user
	* 4. 설명 : 개발자 포럼 글 등록
	 * </pre>
	 * 
	 * @param bbsSaveVo
	 * @param model
	 * @param bindingResult
	 * @return
	 * @throws Exception
	 */
	/*
	 * @RequestMapping(value = "/saveForum.do") public ModelAndView
	 * saveForum(@ModelAttribute("bbsSaveVo") BbsSaveVo bbsSaveVo, BindingResult
	 * bindingResult, ModelAndView mv, HttpServletRequest request) throws Exception
	 * {
	 *//** EgovPropertyService.sample *//*
										 * LOG.debug("saveForum start ################################### : {}",
										 * bbsSaveVo); beanValidator.validate(bbsSaveVo, bindingResult);
										 * LOG.debug("bindingResult : {}", bindingResult.hasErrors()); if
										 * (bindingResult.hasErrors()) { List<ObjectError> list =
										 * bindingResult.getAllErrors(); for (ObjectError e : list) {
										 * LOG.debug("ObjectError getDefaultMessage : {}", e.getDefaultMessage());
										 * LOG.debug("ObjectError  getArguments: {}", e.getArguments());
										 * LOG.debug("ObjectError getCode : {}", e.getCode());
										 * LOG.debug("ObjectError  getObjectName: {}", e.getObjectName());
										 * LOG.debug("ObjectError  getCodes: {}", e.getCodes()); String message =
										 * MessageUtil.getMsg(e.getDefaultMessage(), new String[] {
										 * e.getArguments().toString() }); LOG.debug("result Message  : {}", message);
										 * // -- [2023:codeeyes][반복문에서 break, continue에 대한 불명확한 사용 금지 issue] if (null !=
										 * message) { mv.addObject("msg", message); mv.setViewName("bbs/forum/write");
										 * return mv; } } } // if validate UserJoinVO userVO = (UserJoinVO)
										 * request.getSession().getAttribute("ssUserVo");
										 * bbsSaveVo.setRegr(userVO.getEnCmbrId());
										 * 
										 * bbsSaveVo.setTitle(XSSFilter.toText(StringUtil.isNullToString(bbsSaveVo.
										 * getTitle())));
										 * bbsSaveVo.setSbst(XSSFilter.toText(StringUtil.isNullToString(bbsSaveVo.
										 * getSbst())));
										 * 
										 * if (!"".equals(StringUtil.isNullToString(bbsSaveVo.getPstingId()))) {//
										 * 수정일 경우 int cnt = bbsCmnService.updForum(bbsSaveVo); if (cnt == 0) { String
										 * message = MessageUtil.getMsg("fail.common.msg"); mv.addObject("msg",
										 * message); } else { String message =
										 * MessageUtil.getMsg("update.success.msg"); mv.addObject("msg", message); }
										 * mv.addObject("pstingId", bbsSaveVo.getPstingId()); } else { String pstingId =
										 * bbsCmnService.saveForum(bbsSaveVo); if
										 * ("".equals(StringUtil.isNullToString(pstingId))) { String message =
										 * MessageUtil.getMsg("fail.common.msg"); mv.addObject("msg", message); }
										 * else { String message = MessageUtil.getMsg("insert.success.msg");
										 * mv.addObject("msg", message); } mv.addObject("pstingId", pstingId); }
										 * mv.setViewName("bbs/forum/write"); return mv; }
										 */

	/**
	 * <pre>
	* 1. 메소드명 : delForumAjax
	* 2. 작성일 : 2017. 11. 16. 오후 2:55:41
	* 3. 작성자 : user
	* 4. 설명 : 개발자 포럼 글 삭제
	 * </pre>
	 * 
	 * @param bbsSaveVo
	 * @param model
	 * @param bindingResult
	 * @return
	 * @throws Exception
	 */
	/*
	 * @RequestMapping(value = "/delForumAjax.do") public ModelAndView
	 * delForumAjax(BbsSearchVo bbsSearchVo, ModelMap model, HttpServletRequest
	 * request) throws Exception {
	 * 
	 *//** EgovPropertyService.sample *//*
										 * LOG.debug("delForumAjax start ################################### : {}",
										 * bbsSearchVo);
										 * 
										 * UserJoinVO userVO = (UserJoinVO)
										 * request.getSession().getAttribute("ssUserVo"); bbsSearchVo.setAmdr((null !=
										 * userVO) ? userVO.getMbrId() : null);
										 * bbsSearchVo.setAmdr(CommonFunc.safeDbEncrypt(StringUtil.isNullToString(
										 * bbsSearchVo.getAmdr())));
										 * 
										 * try { Integer.parseInt(bbsSearchVo.getPstingId()); } catch
										 * (NumberFormatException ex) { model.addAttribute("msg", "잘못된 요청입니다."); return
										 * new ModelAndView("jsonView", model); }
										 * 
										 * // 보안을 위해 해당 qnaId로 한번 더 조회해서 등록자와 일치해야 삭제 가능. int myCnt =
										 * bbsCmnService.checkOwnBbs(bbsSearchVo); if (myCnt > 0) { // 삭제 쿼리 int cnt =
										 * bbsCmnService.delForumAjax(bbsSearchVo);
										 * 
										 * if (cnt == 0) { String message = MessageUtil.getMsg("del.fail.msg");
										 * model.addAttribute("msg", message); } else { String message =
										 * MessageUtil.getMsg("del.success.msg"); model.addAttribute("msg",
										 * message); } } else { model.addAttribute("msg", "본인이 등록한 게시글만 삭제 가능합니다."); }
										 * 
										 * return new ModelAndView("jsonView", model); }
										 */
}
