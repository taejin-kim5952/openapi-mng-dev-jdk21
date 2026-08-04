/**
 *  OPEN API version 1.0
 *
 *  Copyright ⓒ 2017 kt corp. All rights reserved.
 *
 *  This is a proprietary software of kt corp, and you may not use this file except in
 *  compliance with license agreement with kt corp. Any redistribution or use of this
 *  software, with or without modification shall be strictly prohibited without prior written
 *  approval of kt corp, and the copyright notice above does not evidence any actual or
 *  intended publication of such software.
 * 
 */
package com.kt.openapi.web.api.controller;

import com.kt.openapi.fwk.online.page.Pagination;
import com.kt.openapi.web.api.service.ApiMainService;
import com.kt.openapi.web.api.service.ApiRegService;
import com.kt.openapi.web.api.vo.ApiMainVo;
import com.kt.openapi.web.api.vo.ApiRegVO;
import com.kt.openapi.web.auth.vo.AuthVO;
import com.kt.openapi.web.cmm.service.CmnService;
import com.kt.openapi.web.cmm.upload.UploadFileUtils;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import com.kt.openapi.web.util.CommonFunc;
import com.kt.openapi.web.util.MessageUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;


/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.api.controller
 * 2. 타입명   : ApiMainController.java
 * 3. 작성일   : 2017. 11. 10. 오후 5:05:06
 * 4. 작성자   : JeonGeun Kang  
 * 5. 설명     : API Main Controller
 * </pre>
 */
@Controller
@RequestMapping(value="/api/main")
public class ApiMainController {
	
	private static final Logger LOG = LoggerFactory.getLogger(ApiMainController.class);
	  
	@Autowired
	private ApiMainService apiMainService;

	@Value("${pageUnit:10}")
	private int pageUnit;

	@Value("${pageSize:10}")
	private int pageSize;
	
	@Autowired
	private UploadFileUtils uploadFileUtiles;
	
	@Autowired
	protected CmnService cmnService;

	//-- [CYD][202005--][add]
	@Value("${gitlab.arsenal.host}")
	private String gitlabArsenalHost;

	//-- [CYD][202005--][add]
	@Value("${gitlab.arsenal.base.path}")
	private String gitlabArsenalBasePath;
	
	//-- [CYD][202009--][add]
	@Value("${apisystem.apidel.authid}")
	private String apiDelAuthId;
	
	@Autowired
	private ApiRegService apiRegService;
	

	/**
	 * <pre>
	 * 1. 메소드명 : mainList
	 * 2. 작성일   : 2017. 11. 10. 오후 2:21:33
	 * 3. 작성자   : JeonGeun Kang
	 * 4. 설명     : API 메인 리스트 페이지로 이동
	 * </pre>
	 * @param request
	 * @param response
	 * @param model
	 * @return ModelAndView
	 * @throws Exception
	 */
	@RequestMapping(value="/mvMainList.do")
	public ModelAndView mvMainList(HttpSession session, HttpServletRequest request,HttpServletResponse response,  ModelMap model) throws Exception {
		
		LOG.debug("#######################  ApiMainController mainList START ############################");
		ModelAndView mv = new ModelAndView();

		setSession(session);
		
		String isAuthYn = "N";
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		
		LOG.debug("Observer is {}", userJVo.getObserverYn());
		
		ArrayList<String> sysList	= new ArrayList<String>();
		ArrayList<String> authList 	= new ArrayList<String>();
		
		if(userJVo.getAuthList()!=null && userJVo.getAuthList().size()>0) {
			for(AuthVO authVo : userJVo.getAuthList()) {
				sysList.add(authVo.getSysId());
				authList.add(authVo.getAutId());
				LOG.debug("= authVo.getSysId() = {} " , authVo.getSysId());
				LOG.debug("= authVo.getAutId() = {} " , authVo.getAutId());
			}
		}else {
			String message =MessageUtil.getMsg("top.aut.msg");
			LOG.debug("top.aut.req message = {} " , message);
			request.setAttribute("msg", message);
			mv.setViewName("forward:/mypage/mypageInfo.do");
			return mv;
		}
		ApiMainVo apiMainVo = new ApiMainVo();
		apiMainVo.setRegr(userJVo.getEnCmbrId());
		apiMainVo.setAmdr(userJVo.getEnCmbrId());
		apiMainVo.setUserSysIdList(sysList);
		apiMainVo.setUserAutIdList(authList);
		
		apiMainVo.setReviewRqtTypeCd("APIRQT1010"); // 검토요청
		model.addAttribute("replyCnt", apiMainService.selMainRequestCnt(apiMainVo));
		
		apiMainVo.setReviewRqtTypeCd("APIRQT1020");	// 개발요청
		model.addAttribute("reqCnt", apiMainService.selMainRequestCnt(apiMainVo));
		
		model.addAttribute("cmnService", cmnService.selComnList("MBRSTS1000"));
		//-- [CYD][202005--][add]
		model.addAttribute("gitlabArsenalHost"	  , gitlabArsenalHost);
		model.addAttribute("gitlabArsenalBasePath", gitlabArsenalBasePath);
		// 삭제권한추가 - CYD 20200917
		String szAuthId = "";
		String[] arrAuthId = apiDelAuthId.split(",");
		
		for (int i = 0; i < arrAuthId.length; i++) {
			szAuthId = arrAuthId[i].trim();
			if(CommonFunc.safeDbEncrypt(szAuthId).equalsIgnoreCase(userJVo.getEnCmbrId())) {
				isAuthYn = "Y";
				break;
			}
		}
		
		LOG.debug("API삭제권한 체크 {} " , isAuthYn);
		
		// 권한 세션 셋업
		session.setAttribute("sIsAuthYn", isAuthYn);
		model.addAttribute("sIsAuthYn", isAuthYn);

		String devMasterId = (String) session.getAttribute("dev.master.id");
		model.addAttribute("b_is_master", devMasterId != null && devMasterId.equalsIgnoreCase("master"));

		mv.setViewName("api/main");
		
		return mv;
	}   
	
	
	/**
	 * <pre>
	 * 1. 메소드명 : selMainListAjax
	 * 2. 작성일   : 2017. 11. 10. 오후 5:43:47
	 * 3. 작성자   : JeonGeun Kang
	 * 4. 설명     : API 메인 리스트 Ajax 조회
	 * </pre>
	 * @param model
	 * @param vo
	 * @return ModelAndView
	 * @throws Exception
	 * TODO 나중에 코드 명칭 정해지면 mainList대신 코드 명칭으로 수정
	 */
	@ResponseBody
	@RequestMapping(value="/selMainListAjax.do", method=RequestMethod.POST)
	public ModelAndView selMainListAjax(HttpSession session , ModelMap model, ApiMainVo vo) throws Exception {
		
		LOG.debug("#######################  ApiMainController selMainListAjax START ############################");
		ModelAndView mv = new ModelAndView();
		LOG.info("vo.getSchText()" + vo.getSchText());
		LOG.info("vo.getTabCode()" + vo.getTabCode());
		
		setSession(session);
		
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		
		ArrayList<String> sysList = new ArrayList<String>();
		ArrayList<String> authList 	= new ArrayList<String>();
		
		if(userJVo.getAuthList()!=null && userJVo.getAuthList().size()>0) {
			for(AuthVO authVo : userJVo.getAuthList()) {
				sysList.add(authVo.getSysId());
				authList.add(authVo.getAutId());
			}
		}
		
		ApiMainVo apiMainVo = new ApiMainVo();
		vo.setRegr(userJVo.getEnCmbrId());
		vo.setAmdr(userJVo.getEnCmbrId());
		vo.setUserSysIdList(sysList);
		vo.setUserAutIdList(authList);
		
		/*
	     * API Link(Studio) Gateway Observer 권한 설정
	     *   Y: Being Observer(=Read)
	     *   N: Not Observer
	     * CYD - 2020.07.08
	     */
		vo.setObserverYn(userJVo.getObserverYn());
		
		vo.setPageUnit(pageUnit); 	// 페이지당 건수
		vo.setPageSize(pageSize);	//페이지 리스트에 게시되는 건수
		
		/** pageing setting */
		Pagination paginationInfo = new Pagination();
		paginationInfo.setCurrentPageNo(vo.getPageIndex()); // 현재 페이지 인덱스
		paginationInfo.setRecordCountPerPage(vo.getPageUnit());
		paginationInfo.setPageSize(vo.getPageSize());
		
		LOG.info("vo.paginationInfo()" + paginationInfo);
		vo.setFirstIndex(paginationInfo.getFirstRecordIndex());
		vo.setLastIndex(paginationInfo.getLastRecordIndex());
		vo.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());
		
		model.addAttribute("info", apiMainService.selMainList(vo));
		
		int totCnt  = apiMainService.selMainListTotalCnt(vo);
		paginationInfo.setTotalRecordCount(totCnt);
		paginationInfo.calculate();
		model.addAttribute("paginationInfo", paginationInfo);//페이징 정보
		
		return new ModelAndView( "jsonView", model );
	}
	
	
	/**
	* <pre>
	* 1. 메소드명 : savReplyRegAjax
	* 2. 작성일 : 2017. 11. 21. 오전 10:59:47
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 요청글의 답글 달기
	* </pre>
	* @param request
	* @param response
	* @param model
	* @return
	* @throws Exception
	*/
	@RequestMapping(value="/savReplyRegAjax.do")
	public ModelAndView savReplyRegAjax(ModelMap model, ApiMainVo vo) throws Exception {
		
		LOG.debug("#######################  ApiMainController savReplyRegAjax START ############################");
		ModelAndView mv = new ModelAndView();
		
		mv.setViewName("api/main/list");
		
		return mv;
	}
	
	/**
	 * <pre>
	 * 1. 메소드명 : mvDevReqReg
	 * 2. 작성일   : 2017. 11. 10. 오후 2:55:25
	 * 3. 작성자   : JeonGeun Kang
	 * 4. 설명     : 개발요청 페이지로 이동
	 * </pre>
	 * @param request
	 * @param response
	 * @param model
	 * @return ModelAndView
	 * @throws Exception
	 */
	/*
	@RequestMapping(value="/mvDevReqReg.do")
	public ModelAndView mvDevReqReg(HttpSession session, HttpServletRequest request,HttpServletResponse response,  ModelMap model , ApiMainVo vo) throws Exception {
		
		LOG.debug("#######################  ApiMainController mvDevReqReg START ############################");
		ModelAndView mv = new ModelAndView();
		
		model.addAttribute("selSysList", apiMainService.selSysList(vo));//
		
		model.addAttribute("info", apiMainService.selReqInfo(vo));//
		
		setSession(session);
		
		mv.setViewName("api/devReqRegWrite");
		
		return mv;
	}
	*/
	/**
	* <pre>
	* 1. 메소드명 : savDevReqRegAjax
	* 2. 작성일 : 2017. 11. 21. 오전 11:01:53
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 개발요청/검토요청을 등록
	* </pre>
	* @param request
	* @param response
	* @param model
	* @return
	* @throws Exception
	*/
	@ResponseBody
	@RequestMapping(value="/savReqRegAjax.do")
	public ModelAndView savReqRegAjax(HttpSession session , ModelMap model, ApiMainVo vo) throws Exception {
		
		LOG.debug("#######################  ApiMainController savReqRegAjax START ############################");
		ModelAndView mv = new ModelAndView();

		setSession(session);		
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		
		vo.setRegr(userJVo.getEnCmbrId());
		vo.setAmdr(userJVo.getEnCmbrId());
		
		model.addAttribute("apiReviewRqtNo", apiMainService.savDevReqReg(vo));
		
		return new ModelAndView( "jsonView", model );
	}
	
	/**
	 * <pre>
	 * 1. 메소드명 : mvDevReqView
	 * 2. 작성일   : 2017. 11. 10. 오후 2:56:46
	 * 3. 작성자   : JeonGeun Kang
	 * 4. 설명     : 개발 요청/검토요청 상세 페이지로 이동
	 * </pre>
	 * @param request
	 * @param response
	 * @param model
	 * @return ModelAndView
	 * @throws Exception
	 */
	/*
	@RequestMapping(value="/mvDevReqView.do")
	public ModelAndView mvDevReqView(HttpSession session, HttpServletRequest request,HttpServletResponse response,  ModelMap model , ApiMainVo vo) throws Exception {
		
		LOG.debug("#######################  ApiMainController mvDevReqView START ############################");
		ModelAndView mv = new ModelAndView();
		
		model.addAttribute("info", apiMainService.selRegistView(vo));
		
		model.addAttribute("reply", apiMainService.selReqReplyView(vo));
		
		setSession(session);
		
		model.addAttribute("fList", apiMainService.selFileList(vo));
		
		
		mv.setViewName("api/devReqRegView");
		
		return mv;
	}
	*/
	/**
	* <pre>
	* 1. 메소드명 : savDevReplyRegAjax
	* 2. 작성일 : 2017. 11. 21. 오후 10:03:21
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 개발요청/검토요청의 답글을 등록
	* </pre>
	* @param model
	* @param vo
	* @return
	* @throws Exception
	*/
	@ResponseBody
	@RequestMapping(value="/savDevReplyRegAjax.do")
	public ModelAndView savDevReplyRegAjax(HttpSession session, ModelMap model, ApiMainVo vo) throws Exception {
		
		LOG.debug("#######################  ApiMainController savDevReplyRegAjax START ############################");
		ModelAndView mv = new ModelAndView();

		setSession(session);
		
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		
		vo.setRegr(userJVo.getEnCmbrId());
		vo.setAmdr(userJVo.getEnCmbrId());
		
		model.addAttribute("info", apiMainService.savDevReplyReg(vo));

		return new ModelAndView( "jsonView", model );
		
	}
	
	
	/**
	 * <pre>
	 * 1. 메소드명 : mvDevAnsReg
	 * 2. 작성일   : 2017. 11. 10. 오후 2:58:09
	 * 3. 작성자   : JeonGeun Kang
	 * 4. 설명     : 개발 요청 답변 페이지로 이동
	 * </pre>
	 * @param request
	 * @param response
	 * @param model
	 * @return ModelAndView
	 * @throws Exception
	 */
	@RequestMapping(value="/mvDevAnsReg.do")
	public ModelAndView mvDevAnsReg(HttpServletRequest request,HttpServletResponse response,  ModelMap model) throws Exception {
		
		LOG.debug("#######################  ApiMainController mvDevAnsReg START ############################");
		ModelAndView mv = new ModelAndView();
		mv.setViewName("api/main/list");
		
		return mv;
	}
	
	
	/**
	* <pre>
	* 1. 메소드명 : delDevApiAjax
	* 2. 작성일 : 2017. 11. 22. 오후 3:20:35
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : API 작업중인 문서 삭제
	* </pre>
	* @param session
	* @param model
	* @param vo
	* @return
	* @throws Exception
	*/
	@ResponseBody
	@RequestMapping(value="/delDevApiAjax.do")
	public ModelAndView delDevApi(HttpSession session, ModelMap model, ApiMainVo vo) throws Exception {
		
		LOG.debug("#######################  ApiMainController delDevApi START ############################");

		int delCnt = 0;
		boolean apiDelAuth = false;
		setSession(session);
		
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		
		LOG.debug("ApiMainVo 확인 : {}", vo);
		ApiRegVO apiRegVo =  new ApiRegVO();
		apiRegVo.setApiSpcNo(vo.getApiSpcNo());
		apiRegVo.setRegr( userJVo.getEnCmbrId());
		
		LOG.debug("apiRegVo 확인 : {}", apiRegVo);
		
		String[] delAuthIdArr = apiDelAuthId.split(",");
		
		for(String adminId : delAuthIdArr) {
			if(CommonFunc.safeDbEncrypt(adminId).equalsIgnoreCase(userJVo.getEnCmbrId())) {				
				apiDelAuth = true;
				break;
			}
		}
		
		LOG.debug("API 삭제 권한 보유 관리자 체크 : {}", apiDelAuth);
		
		if(apiDelAuth) {		
			delCnt = apiMainService.delDevApi(apiRegVo);
		}
		
		model.addAttribute("cnt", delCnt);
		
		return new ModelAndView( "jsonView", model );
		
	}
	
	/**
	* <pre>
	* 1. 메소드명 : savApiVerAjax
	* 2. 작성일 : 2017. 11. 22. 오후 3:20:35
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : API 문서 버전업
	* </pre>
	* @param session
	* @param model
	* @param vo
	* @return
	* @throws Exception
	*/
	@ResponseBody
	@RequestMapping(value="/savApiVerAjax.do")
	public ModelAndView savApiVerAjax(HttpSession session, ModelMap model, ApiMainVo vo) throws Exception {
		
		LOG.debug("#######################  ApiMainController savApiVerAjax START ############################");

		setSession(session);
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");

		vo.setRegr(userJVo.getEnCmbrId());
		vo.setAmdr(userJVo.getEnCmbrId());

		LOG.debug("==== vo.getApiSpcNo() = {} " , vo.getApiSpcNo());
		LOG.debug("==== vo.getVer() = {} " , vo.getVer());
		LOG.debug("==== vo.getVerDesc() = {} " , vo.getVerDesc());
		LOG.debug("==== vo.getAmdr() = {} " , vo.getAmdr());
		LOG.debug("==== vo.getRegr() = {} " , vo.getRegr());
		
		model.addAttribute("info", apiMainService.savApiVer(vo));

		return new ModelAndView( "jsonView", model );
		
	}
	
	/**
	* <pre>
	* 1. 메소드명 : mutiUploadFile
	* 2. 작성일 : 2017. 11. 23. 오후 8:11:27
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 파일 업로드
	* </pre>
	* @param session
	* @param request
	* @param response
	* @param uploadFiles
	* @param apiReviewRqtNo
	* @return
	* @throws Exception
	*/
	/*
	@RequestMapping(value = "/mutiUploadFile.do", method = RequestMethod.POST, produces="application/json;charset=utf-8")
	@ResponseBody
	public ModelAndView mutiUploadFile(HttpSession session, HttpServletRequest request, HttpServletResponse response, @RequestParam("uploadFile") MultipartFile uploadFiles , @RequestParam("apiReviewRqtNo") String apiReviewRqtNo) throws Exception{
		LOG.debug("uploadFiles : {}" , uploadFiles);
		LOG.debug("apiReviewRqtNo : {}" , apiReviewRqtNo);
		
		ModelAndView mv = new ModelAndView();
		
		File tempFile = WebFileHelper.transferToTempFile(uploadFiles);
		FileUploadInfo file = new FileUploadInfo(uploadFiles.getOriginalFilename(),tempFile);
		  
		CmnFileVo fileVo = uploadFileUtiles.uploadFileUpload(file);
		
		LOG.debug("fileVo : {}" , fileVo);
		
		//int fileSeq = playInfoService.playInfoMutiFileReg(fileVo);
		
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		
		// 파일 등록
		ApiMainVo vo = new ApiMainVo();
		vo.setApiReviewRqtNo(apiReviewRqtNo);
		vo.setOriginFileNm(fileVo.getOrgFileName());
		vo.setSaveFileNm(fileVo.getSaveFileName());
		vo.setFilePath(fileVo.getFilePath());
		vo.setFileSize(fileVo.getFileSize()+"");
		vo.setFileTypeCd("FILTYP1040");
		vo.setRegr(userJVo.getEnCmbrId());
		vo.setAmdr(userJVo.getEnCmbrId());
		
		mv.addObject("success", apiMainService.savReqFile(vo));
		mv.addObject("fileVo", vo);
		
		return mv;
	}
	
	*/
	
	/**
	* <pre>
	* 1. 메소드명 : salApiVerDupCheckAjax
	* 2. 작성일 : 2017. 12. 21. 오후 2:34:43
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : API 버전업시, 동일한 버전 있는 지 체크
	* </pre>
	* @param session
	* @param model
	* @param vo
	* @return
	* @throws Exception
	*/
	@ResponseBody
	@RequestMapping(value="/salApiVerDupCheckAjax.do")
	public ModelAndView salApiVerDupCheckAjax(HttpSession session, ModelMap model, ApiMainVo vo) throws Exception {
		
		LOG.debug("#######################  ApiMainController salApiVerDupCheckAjax START ############################");
		ModelAndView mv = new ModelAndView();

		setSession(session);
		
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		
		vo.setRegr(userJVo.getEnCmbrId());
		vo.setAmdr(userJVo.getEnCmbrId());
		
		LOG.debug("==== vo.getApiSpcNo() = {} " , vo.getApiSpcNo());
		LOG.debug("==== vo.getVer() = {} " , vo.getVer());
		LOG.debug("==== vo.getVerDesc() = {} " , vo.getVerDesc());
		
		model.addAttribute("dupCnt", apiMainService.salApiVerDupCheck(vo));

		return new ModelAndView( "jsonView", model );
		
	}
	
	
	
	public void setSession(HttpSession session) {
		
//		UserJoinVO userJVo = new UserJoinVO();
//		
//		userJVo.setMbrId("0001M7HGVS7AwV401M4R/0xqmg==");
//		userJVo.setMbrNm("홍길동");
//		userJVo.setEnCmbrId("0001M7HGVS7AwV401M4R/0xqmg==");
//		userJVo.setCmpnNm("회사명");
//		
//		ArrayList<AuthVO> authList = new ArrayList<AuthVO>();
//		AuthVO authInfo = new AuthVO();
//		authInfo.setSysId("SHUB");
//		authInfo.setAutNm("개발자");
//		authInfo.setAutId("8");
//		
//		authList.add(authInfo);
////		
//		userJVo.setAuthList(authList);
//		
//		session.setAttribute("ssUserVo",userJVo);
		
	}
	
	
	/**
	* [tag:adpt][drm][add]
	* API Private전환
	*/
	@ResponseBody
	@RequestMapping(value="/savApiPrivateAjax.do")
	public ModelAndView savApiPrivateAjax(HttpSession session, ModelMap model, ApiMainVo vo) throws Exception {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		setSession(session);
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		vo.setRegr(userJVo.getEnCmbrId());
		vo.setAmdr(userJVo.getEnCmbrId());

		LOG.debug("==== vo.getApiSpcNo() = {} " , vo.getApiSpcNo());
		LOG.debug("==== vo.getAmdr() = {} " , vo.getAmdr());
		LOG.debug("==== vo.getRegr() = {} " , vo.getRegr());

		model.addAttribute("info", apiMainService.savApiPrivate(vo));

		return new ModelAndView( "jsonView", model );
		
	}
	
	/**
	* [CYD][202005--][add]
	* YAML 데이터 가져오기
	*/
	@ResponseBody
	@RequestMapping(value="/getYmalAjax.do")
	public ModelAndView getYmalAjax(HttpSession session, ModelMap model, ApiMainVo vo) throws Exception {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		setSession(session);
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		vo.setRegr(userJVo.getEnCmbrId());
		vo.setAmdr(userJVo.getEnCmbrId());

		LOG.debug("==== vo.getApiSpcNo() = {} " , vo.getApiSpcNo());
		LOG.debug("==== vo.getAmdr() = {} " , vo.getAmdr());
		LOG.debug("==== vo.getRegr() = {} " , vo.getRegr());

		model.addAttribute("info", apiMainService.selYamlInfo(vo));

		return new ModelAndView( "jsonView", model );
		
	}
	
}
