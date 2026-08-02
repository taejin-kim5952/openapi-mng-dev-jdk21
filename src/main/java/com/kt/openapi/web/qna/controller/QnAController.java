package com.kt.openapi.web.qna.controller;

import com.kt.openapi.web.qna.service.QnAService;
import com.kt.openapi.web.util.SendMailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <pre>
* 1. 패키지명 : com.kt.openapi.web.qna.controller
* 2. 타입명 : QnAController.java
* 3. 작성일 : 2017. 11. 30. 오후 1:35:09
* 4. 작성자 : user
* 5. 설명 :  api QNA
 * </pre>
 */
@Controller
@RequestMapping(value = "/qna")
public class QnAController {

	private static final Logger LOG = LoggerFactory.getLogger(QnAController.class);

	@Autowired
	@Qualifier("qnaService")
	private QnAService service;

	@Value("${pageUnit:10}")
	private int pageUnit;

	@Value("${pageSize:10}")
	private int pageSize;

	// 메일
	@Autowired
	private SendMailUtil sendMailUtil;

	@Value("${mail.receiver.apilink}")
	private String apiLinkMail;
	/**
	 * <pre>
	* 1. 메소드명 : mvQnAList
	* 2. 작성일 : 2017. 11. 30. 오후 1:36:06
	* 3. 작성자 : user
	* 4. 설명 :  QNA 목록페이지
	 * </pre>
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	/*
	 * @RequestMapping(value="/mvQnAList.do") public ModelAndView
	 * mvQnAList(HttpServletRequest request,HttpServletResponse response, ModelMap
	 * model) throws Exception { LOG.
	 * debug("#######################  QnAController mvQnAList START ############################"
	 * ); ModelAndView mv = new ModelAndView(); mv.setViewName("/qna/list");
	 * 
	 * return mv; }
	 */

	/**
	 * <pre>
	* 1. 메소드명 : selQnaListAjax
	* 2. 작성일 : 2017. 11. 30. 오후 1:58:03
	* 3. 작성자 : user
	* 4. 설명 : qna 목록 조회
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
	 * @RequestMapping(value="/selQnaListAjax.do") public ModelAndView
	 * selQnaListAjax(HttpServletRequest request,HttpServletResponse response,
	 * ModelMap model , QnASearchVO param) throws Exception { LOG.
	 * debug("#######################  QnAController selQnaListAjax START ############################"
	 * ); param.setPageUnit(pageUnit); // 페이지당 건수
	 * param.setPageSize(pageSize);//페이지 리스트에 게시되는 건수
	 *//** pageing setting *//*
								 * PaginationInfo paginationInfo = new PaginationInfo();
								 * paginationInfo.setCurrentPageNo(param.getPageIndex()); // 현재 페이지 인덱스
								 * paginationInfo.setRecordCountPerPage(param.getPageUnit());
								 * paginationInfo.setPageSize(param.getPageSize());
								 * 
								 * param.setFirstIndex(paginationInfo.getFirstRecordIndex());
								 * param.setLastIndex(paginationInfo.getLastRecordIndex());
								 * param.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());
								 * 
								 * List<QnAVO> list = service.selQnaList(param); for(QnAVO qnaVO : list) {
								 * String amdrNm = CommonFunc.safeDbDecrypt((qnaVO.getAmdrNm()).toString());
								 * if(amdrNm.length() > 1) { amdrNm = amdrNm.substring(0, amdrNm.length()-1);
								 * amdrNm = amdrNm+"*"; } qnaVO.setAmdrNm( amdrNm ); }
								 * model.addAttribute("nlist", list);//목록 정보
								 * 
								 * int totCnt = service.selQnaListCnt(param);
								 * paginationInfo.setTotalRecordCount(totCnt);
								 * model.addAttribute("paginationInfo", paginationInfo);//페이징 정보
								 * 
								 * return new ModelAndView( "jsonView", model ); }
								 */

	/**
	 * <pre>
	* 1. 메소드명 : mvQnaView
	* 2. 작성일 : 2017. 11. 30. 오후 3:32:55
	* 3. 작성자 : user
	* 4. 설명 :qna 상세보기 페이지
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
	 * @RequestMapping(value = "/mvQnaView.do") public ModelAndView
	 * mvQnaView(HttpServletRequest request, HttpServletResponse response, ModelMap
	 * model, QnASearchVO param) throws Exception { LOG.
	 * debug("#######################  QnAController mvQnaView START ############################"
	 * ); ModelAndView mv = new ModelAndView();
	 * 
	 * QnAVO vmap = service.selQnaView(param); LOG.info("regr :::: {} ",
	 * StringUtil.isNullToString(vmap.getRegr())); vmap.setRegr(
	 * CommonFunc.safeDbDecrypt(StringUtil.isNullToString(vmap.getRegr())));
	 * 
	 * String amdrNm =
	 * CommonFunc.safeDbDecrypt(StringUtil.isNullToString(vmap.getAmdrNm()));
	 * if (amdrNm.length() > 1) { amdrNm = amdrNm.substring(0, amdrNm.length() - 1);
	 * amdrNm = amdrNm + "*"; } vmap.setAmdrNm( amdrNm );
	 * 
	 * vmap.setAns( StringUtil.isNullToString( XSSFilter.fromXssText((String)
	 * vmap.getAns() ))); vmap.setQstn( StringUtil.isNullToString(
	 * XSSFilter.fromXssText((String) vmap.getQstn() ))); vmap.setTitle(
	 * StringUtil.isNullToString( XSSFilter.fromXssText((String)
	 * vmap.getTitle() )));
	 * 
	 * 
	 * // -- [tag:SR-20220126][for qnaId 변조] vmap.setEncQnaId(
	 * CommonFunc.safeDbEncrypt(StringUtil.isNullToString(vmap.getQnaId())));
	 * 
	 * // 상세보기 조회 model.addAttribute("vmap", vmap); // 첨부파일 조회
	 * model.addAttribute("fList", service.selQnaFileList(param));
	 * 
	 * mv.setViewName("/qna/view");
	 * 
	 * return mv; }
	 */

	/**
	 * <pre>
	* 1. 메소드명 : delQnaAjax
	* 2. 작성일 : 2017. 11. 30. 오후 5:23:20
	* 3. 작성자 : user
	* 4. 설명 : qna 글 삭제
	 * </pre>
	 * 
	 * @param param
	 * @param model
	 * @return
	 * @throws Exception
	 */
	/*
	 * @RequestMapping(value = "/delQnaAjax.do") public ModelAndView
	 * delQnaAjax(HttpServletRequest request, QnASearchVO param, ModelMap model)
	 * throws Exception {
	 *//** EgovPropertyService.sample *//*
										 * LOG.
										 * info("QnAController delQnaAjax start ################################### : {}"
										 * , param.toString());
										 * 
										 * 
										 * LoginVO loginVO = (LoginVO) request.getSession().getAttribute("ssUserVo");
										 * 
										 * UserJoinVO userVO = (UserJoinVO)
										 * request.getSession().getAttribute("ssUserVo"); param.setAmdr((null != userVO)
										 * ? userVO.getMbrId() : null);
										 * 
										 * param.setAmdr(CommonFunc.safeDbEncrypt(StringUtil.isNullToString(param.
										 * getAmdr())));
										 * 
										 * try { Integer.parseInt(param.getQnaId()); } catch (NumberFormatException ex)
										 * { model.addAttribute("msg", "잘못된 요청입니다."); return new
										 * ModelAndView("jsonView", model); }
										 * 
										 * // 보안을 위해 해당 qnaId로 한번 더 조회해서 등록자와 일치해야 삭제 가능. int myCnt =
										 * service.checkOwnQna(param);
										 * 
										 * // -- [tag:SR-20220126][for qnaId 변조] if (false ==
										 * CommonFunc.safeDbEncrypt(param.getQnaId())
										 * .equals(StringUtil.isNullToString(request.getParameter("encQnaId")))) {
										 * throw new Exception("qnaId parameter is manipulated"); } if (myCnt > 0) { //
										 * 삭제 쿼리 int cnt = service.delForumAjax(param); if (cnt <= 0) { String message =
										 * MessageUtil.getMsg("del.fail.msg"); model.addAttribute("msg", message); }
										 * else { String message = MessageUtil.getMsg("del.success.msg");
										 * model.addAttribute("msg", message); } } else { model.addAttribute("msg",
										 * "본인이 등록한 게시글만 삭제 가능합니다."); } return new ModelAndView("jsonView", model); }
										 */

	/**
	 * <pre>
	* 1. 메소드명 : mvQnaReg
	* 2. 작성일 : 2017. 11. 30. 오후 5:39:36
	* 3. 작성자 : user
	* 4. 설명 : qna 등록 및 수정 폼 이동
	 * </pre>
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @param qnASaveVO
	 * @return
	 * @throws Exception
	 */
	/*
	 * @RequestMapping(value = "/mvQnaReg.do") public ModelAndView
	 * mvQnaReg(HttpServletRequest request, HttpServletResponse response, ModelMap
	 * model, QnASaveVO qnASaveVO) throws Exception { LOG.
	 * debug("#######################  QnAController mvQnaReg START ############################"
	 * ); ModelAndView mv = new ModelAndView(); QnASearchVO qnASearchVO = new
	 * QnASearchVO();
	 * 
	 * if (qnASaveVO != null &&
	 * !"".equals(StringUtil.isNullToString(qnASaveVO.getQnaId()))) {
	 * 
	 * LOG.info("qnASaveVO : {}", qnASaveVO.toString()); String s1 =
	 * CommonFunc.safeDbEncrypt(qnASaveVO.getQnaId()); String s2 =
	 * request.getParameter("encQnaId");
	 * 
	 * // -- [tag:SR-20220126][for qnaId 변조] if (false ==
	 * CommonFunc.safeDbEncrypt(qnASaveVO.getQnaId())
	 * .equals(StringUtil.isNullToString(request.getParameter("encQnaId")))) {
	 * throw new Exception("qnaId parameter is manipulated"); }
	 * qnASearchVO.setQnaId(qnASaveVO.getQnaId());
	 * 
	 * QnAVO vmap = service.selQnaView(qnASearchVO);// 상세 보기 조회 String regr =
	 * CommonFunc.safeDbDecrypt(StringUtil.isNullToString(vmap.getRegr()));
	 * String ansr =
	 * CommonFunc.safeDbDecrypt(StringUtil.isNullToString(vmap.getAnsr()));
	 * vmap.setRegr( regr ); vmap.setAnsr( ansr ); if (regr.length() > 3) { regr
	 * = regr.substring(0, regr.length() - 3); regr = regr + "***"; } if
	 * (ansr.length() > 3) { ansr = ansr.substring(0, ansr.length() - 3); ansr =
	 * ansr + "***"; } vmap.setRegrMasking( regr ); vmap.setAnsrMasking( ansr );
	 * vmap.setTitle(
	 * XSSFilter.fromXssText(StringUtil.isNullToString(vmap.getTitle())));
	 * vmap.setQstn(
	 * XSSFilter.fromXssText(StringUtil.isNullToString(vmap.getQstn())));
	 * 
	 * model.addAttribute("vmap", vmap);
	 * qnASaveVO.setTitle(XSSFilter.fromXssText(StringUtil.isNullToString(vmap.
	 * getTitle())));
	 * qnASaveVO.setQstn(XSSFilter.fromXssText(StringUtil.isNullToString(vmap.
	 * getQstn()))); LOG.debug("vmap.TITLE    : {}", vmap.getTitle());
	 * 
	 * // 첨부파일 정보 조회 List<QnAFileVO> fList = service.selQnaFileList(qnASearchVO);// 상세
	 * 보기 조회 model.addAttribute("fList", fList); } model.addAttribute("qnASaveVO",
	 * qnASaveVO); mv.setViewName("/qna/write");
	 * 
	 * return mv; }
	 */

	/**
	 * <pre>
	* 1. 메소드명 : saveQna
	* 2. 작성일 : 2017. 12. 1. 오후 1:34:39
	* 3. 작성자 : user
	* 4. 설명 :qna 글 등록 및 수정
	 * </pre>
	 * 
	 * @param qnASaveVO
	 * @param bindingResult
	 * @param mv
	 * @param uploadFile
	 * @return
	 * @throws Exception
	 */
	/*
	 * @RequestMapping(value = "/saveQna.do") public ModelAndView
	 * saveQna(@ModelAttribute("qnASaveVO") QnASaveVO qnASaveVO, BindingResult
	 * bindingResult, ModelAndView mv, @ModelAttribute("uploadFile") MultipartFile
	 * uploadFile, HttpServletRequest request) throws Exception {
	 *//** EgovPropertyService.sample *//*
										 * LOG.debug("saveQna start ################################### : {}",
										 * qnASaveVO);
										 * LOG.debug("uploadFile start ################################### : {}",
										 * uploadFile);
										 * LOG.debug("bindingResult start ################################### : {}",
										 * bindingResult); beanValidator.validate(qnASaveVO, bindingResult);
										 * LOG.debug("bindingResult : {}", bindingResult.hasErrors());
										 * mv.setViewName("/qna/write"); if (bindingResult.hasErrors()) {
										 * List<ObjectError> list = bindingResult.getAllErrors(); for (ObjectError e :
										 * list) { LOG.debug("ObjectError getDefaultMessage : {}",
										 * e.getDefaultMessage()); LOG.debug("ObjectError  getArguments: {}",
										 * e.getArguments()); LOG.debug("ObjectError getCode : {}", e.getCode());
										 * LOG.debug("ObjectError  getObjectName: {}", e.getObjectName());
										 * LOG.debug("ObjectError  getCodes: {}", e.getCodes()); String message =
										 * MessageUtil.getMsg(e.getDefaultMessage(), new String[] {
										 * e.getArguments().toString() }); LOG.debug("result Message  : {}", message);
										 * // -- [2023:codeeyes][반복문에서 break, continue에 대한 불명확한 사용 금지 issue] if (null !=
										 * message) { mv.addObject("msg", message); return mv; } } } // if validate
										 * UserJoinVO userVO = (UserJoinVO)
										 * request.getSession().getAttribute("ssUserVo");
										 * qnASaveVO.setRegr(userVO.getEnCmbrId());
										 * 
										 * qnASaveVO.setTitle(XSSFilter.toText(StringUtil.isNullToString(qnASaveVO.
										 * getTitle())));
										 * qnASaveVO.setQstn(XSSFilter.toText(StringUtil.isNullToString(qnASaveVO.
										 * getQstn())));
										 * 
										 * String filename = ""; String formatName = ""; if (uploadFile != null) {
										 * filename = uploadFile.getOriginalFilename(); formatName =
										 * uploadFile.getOriginalFilename()
										 * .substring(uploadFile.getOriginalFilename().lastIndexOf(".") + 1); } //
										 * filname = "test.zip<img src=x onerror=\"alert('test')>.zip\""; // formatName
										 * = "jpg"; XSSFilter.toText(StringUtil.isNullToString(filename));
										 * 
										 * LOG.info("uploadFile name: {}, fileformat : {}", filename, formatName); if
										 * (formatName != "" && !formatName.equals("zip")) { mv.addObject("msg",
										 * "확장자가 zip 파일만 등록 가능 합니다."); return mv; } else { if
										 * (!"".equals(StringUtil.isNullToString(qnASaveVO.getQnaId()))) {// 수정일 경우
										 * LOG.debug("qna 글 수정 시작 @@@@@@@@@@@@@@@@@@@@@@@@@@@@"); int cnt =
										 * service.updQna(qnASaveVO, uploadFile); if (cnt == 0) { String message =
										 * MessageUtil.getMsg("fail.common.msg"); mv.addObject("msg", message); }
										 * else { String message = MessageUtil.getMsg("update.success.msg");
										 * mv.addObject("msg", message); } mv.addObject("qnaId", qnASaveVO.getQnaId());
										 * } else { String message = "";
										 * LOG.debug("qna 글 등록 시작 @@@@@@@@@@@@@@@@@@@@@@@@@@@@"); String qnaId =
										 * service.saveQna(qnASaveVO, uploadFile); if
										 * ("".equals(StringUtil.isNullToString(qnaId))) { message =
										 * MessageUtil.getMsg("fail.common.msg"); mv.addObject("msg", message);
										 * LOG.debug("message 1 :: {}", message); } else { // 메일 발송 Map<String, String>
										 * map = new HashMap<>(); map.put("tempId", "100000007"); map.put("title",
										 * "APIManager Q&A 등록 알림메일"); map.put("content",
										 * CommonFunc.safeDbDecrypt(qnASaveVO.getRegr()) + " 님의 " + qnASaveVO.getTitle()
										 * + "  신청이 되었습니다."); map.put("toMail", apiLinkMail);
										 * sendMailUtil.sendMailcall(map); message =
										 * MessageUtil.getMsg("insert.success.msg"); LOG.debug("message 2 :: {}",
										 * message); mv.addObject("msg", message); } mv.addObject("qnaId", qnaId); } }
										 * return mv; }
										 */
}
