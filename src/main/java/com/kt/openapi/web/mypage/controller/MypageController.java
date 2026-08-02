package com.kt.openapi.web.mypage.controller;

import com.kt.openapi.web.adptran.util.KsmUtil;
import com.kt.openapi.web.api.service.ApiRegService;
import com.kt.openapi.web.auth.vo.AuthVO;
import com.kt.openapi.web.mypage.service.MypageService;
import com.kt.openapi.web.mypage.vo.MypageVO;
import com.kt.openapi.web.userJoin.vo.UserHistVO;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import com.kt.openapi.web.util.CommonFunc;
import com.kt.openapi.web.util.MessageUtil;
import com.kt.openapi.web.util.StringUtil;
import com.kt.openapi.web.util.SendMailUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
* <pre>
* 1. 패키지명 : com.kt.openapi.web.mypage.controller
* 2. 타입명 : MypageController.java
* 3. 작성일 : 2017. 11. 30. 오후 2:10:57
* 4. 작성자 : ANEUNTAEK
* 5. 설명 : MYPAGE
* </pre>
 */
@Controller
@RequestMapping(value="/mypage")
public class MypageController {
	
	private static final Logger logger = LoggerFactory.getLogger(MypageController.class);
	
	@Autowired
	private SendMailUtil sendMailUtil;
	
	@Value("${mail.receiver.apilink}")
	private String apiLinkMail;
	
	@Autowired
	@Qualifier("mypageService")
	private MypageService service;

	//-- [tag:PRJ-20220901]
	@Autowired
	private ApiRegService apiRegService;

	//-- [tag:PRJ-20220901]
	@RequestMapping(value = "/console.do")
	public ModelAndView console(HttpServletRequest request, HttpSession session, ModelMap model) throws Exception {
		ModelAndView mav = new ModelAndView();

		UserJoinVO userJoinVO = (UserJoinVO)session.getAttribute("ssUserVo");
		
		MypageVO mypageVo = new MypageVO();
		mypageVo.setMbrId(CommonFunc.safeDbEncrypt(userJoinVO.getMbrId()));

		//-- {나의 보유 권한} // autId, autNm, sysId, sysNm, sttusCd, autApvDt, autApvr
		List<Map<String, Object>> autList = service.selAutList(mypageVo);	//-- #mbrId
		//-- {권한 요청 상태} // autId, autNm, sysId, sysNm, sttusCd, autApvDt, autApvr
		List<Map<String, Object>> aReqList = service.selAutReq(mypageVo);	//-- mbrId
		//-- 시스템 목록
		List<Map<String, Object>> sysSelectBox = service.selboxSysNm(mypageVo);	//-- no_param

		model.addAttribute("userJoinVO", userJoinVO);
		model.addAttribute("autList", autList);
		model.addAttribute("aReqList", aReqList);
		model.addAttribute("sysSelBox", sysSelectBox);
		
		//-- [i]상단 API등록현황 // TB배로완료, 검증중, 사용배포대기, 상용배포완료
		Map<String, Object>map_ret = apiRegService.selBeastApiCountGroupByStatus(userJoinVO);
		//-- [i]map_reg {'nlist':, 'tb_deploy_ok_cnt':, 'verify_ing_cnt':, 'prd_deploy_req_cnt':, 'prd_deploy_ok_cnt': } 
		model.addAttribute("beastApiCountInfo", map_ret);

		mav.addObject("msg", KsmUtil.fnSafeStr(request.getAttribute("msg")));
		mav.setViewName("mypage/console");
		return mav;
	}

	/**
	 * 
	* <pre>
	* 1. 메소드명 : loginView
	* 2. 작성일 : 2017. 11. 30. 오후 2:11:19
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : MYPAGE 페이지 이동
	* </pre>
	* @param request
	* @param model
	* @param session
	* @param mypageVo
	* @return
	* @throws Exception
	 */
	@RequestMapping(value = "/mypageInfo.do")
	public ModelAndView loginView(HttpServletRequest request, ModelMap model, HttpSession session, MypageVO mypageVo) throws Exception {
		
		ModelAndView mav = new ModelAndView();
		logger.debug("====================Start mypageInfo Page==================== {}" ,  StringUtil.isNullToString(request.getAttribute("msg")));
		logger.debug("mypageVo {}", mypageVo);
		
		
		mav.addObject("msg", StringUtil.isNullToString(request.getAttribute("msg")));
		mav.setViewName("mypage/mypage");
		
		UserJoinVO userJoinVO = (UserJoinVO)session.getAttribute("ssUserVo");
		logger.debug("Start user session enCmbrId: {}", userJoinVO.getEnCmbrId());
		logger.debug("Start user session mbrId: {}", userJoinVO.getMbrId());
		logger.debug("Start user session maskingMbrId: {}", userJoinVO.getMaskingMbrId());
				
		ArrayList<String> sysList	= new ArrayList<String>();
		ArrayList<String> atList	= new ArrayList<String>();
		
		if(userJoinVO.getAuthList()!=null && userJoinVO.getAuthList().size()>0) {
			for(AuthVO authVo : userJoinVO.getAuthList()) {
						sysList.add(authVo.getSysId());
			}
		}	
		
		if(userJoinVO.getAuthList()!=null && userJoinVO.getAuthList().size()>0) {
			for(AuthVO authVo : userJoinVO.getAuthList()) {
				       atList.add(authVo.getAutId());
			}
		}
				
		mypageVo.setUserSysIdList(sysList);
		mypageVo.setUserAutIdList(atList);
		
        if(userJoinVO.getMbrId() != null) {
        	mypageVo.setMbrId(CommonFunc.safeDbEncrypt(userJoinVO.getMbrId()));
		}
        logger.debug("mypageVo mbrId: {}", mypageVo.getMbrId());
		
		mypageVo.setReviewRqtTypeCd("APIRQT1010");   
		//api 검토요청 건수
		int revcnt = 0;
		revcnt = service.getApiRevCnt(mypageVo);
		
		mypageVo.setReviewRqtTypeCd("APIRQT1020");   
		//api 개발 요청 건수
		int devcnt = 0;
		devcnt = service.getApiRevCnt(mypageVo);
		
		//작성중 API 상세보기
		Map<String, Object> wMap = service.selWriteApi(mypageVo);
		
		//세션에서 담은 값 넣기
		UserJoinVO userVO = (UserJoinVO)request.getSession().getAttribute("ssUserVo");
		mypageVo.setRegr(userVO.getEnCmbrId());
		
		//나의 개발자 포럼 최신글 1개
		int apiDevFCnt = 0;
		Map<String, Object> dMap = service.selDevFView(mypageVo);
		if(dMap != null) {
			mypageVo.setPstingId( StringUtil.isNullToString(dMap.get("pstingId"))  );
			apiDevFCnt = service.selDevFCnt(mypageVo);////나의 개발자 포럼 댓글 개수
		}
		
		//나의 Q&A 상세글
		Map<String, Object> qMap = service.selQAView(mypageVo);
		
		//나의 보유 권한 최대 5개
		List<Map<String, Object>> autList = service.selAutList(mypageVo);
		
		//권한 요청 상태 최대 5개
		List<Map<String, Object>> aReqList = service.selAutReq(mypageVo);
		
		mypageVo.setAutSttusCd("MBRAUT1010");
		mypageVo.setRegr(CommonFunc.safeDbDecrypt(mypageVo.getMbrId()));
		mypageVo.setAmdr(CommonFunc.safeDbDecrypt(mypageVo.getMbrId()));
		
		//시스템 목록 
		List<Map<String, Object>> sysSelectBox = service.selboxSysNm(mypageVo);
		
		model.addAttribute("apiRevCnt", revcnt);
		model.addAttribute("apiDevCnt", devcnt);
		model.addAttribute("wMap", wMap);
		model.addAttribute("dMap", dMap);
		model.addAttribute("apiDevFCnt", apiDevFCnt);
		model.addAttribute("qMap", qMap);
		model.addAttribute("autList", autList);
		model.addAttribute("aReqList", aReqList);
		model.addAttribute("sysSelBox", sysSelectBox);
		
		return mav;
	}
	
	/**
	 * 
	* <pre>
	* 1. 메소드명 : selBackViewAjax
	* 2. 작성일 : 2017. 11. 30. 오후 2:20:35
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 반려 사유
	* </pre>
	* @param request
	* @param model
	* @param param
	* @return
	* @throws Exception
	 */
	@RequestMapping(value = "/selBackViewAjax.do", method = RequestMethod.POST , produces="application/json;charset=utf-8")
	public ModelAndView selBackViewAjax(HttpServletRequest request, ModelMap model , MypageVO param, HttpSession session) throws Exception {
		logger.debug("#######################  MypageController selBackViewAjax START ############################");
		logger.debug("MypageVO param: {}",param);
		UserJoinVO userJoinVO = (UserJoinVO)session.getAttribute("ssUserVo");
		logger.debug("Start user session mbrId: {}", userJoinVO.getMbrId());
		if(userJoinVO.getMbrId() != null) {
			param.setMbrId(CommonFunc.safeDbEncrypt(userJoinVO.getMbrId()));
		}	
			param.setAutSttusCd("MBRAUT1030");
			Map<String, Object> backMap = service.selBack(param);
			
			model.addAttribute("backMap", backMap);
			model.addAttribute("message" , "success");
			return new ModelAndView( "jsonView", model );
		
	}
	
	/**
	 * 
	* <pre>
	* 1. 메소드명 : selboxAjax
	* 2. 작성일 : 2017. 11. 30. 오후 2:21:40
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 권한 그룹 목록
	* </pre>
	* @param request
	* @param model
	* @param param
	* @return
	* @throws Exception
	 */
	@RequestMapping(value = "/selboxAjax.do", method = RequestMethod.POST , produces="application/json;charset=utf-8")
	public ModelAndView selboxAjax(HttpServletRequest request, ModelMap model , MypageVO param) throws Exception {
		logger.debug("#######################  MypageController selboxAjax START ############################");
		logger.debug("select box val:"+param.getSysId());
			List<Map<String, Object>> autGroup = service.selboxAGroup(param);
			model.addAttribute("autGroup", autGroup);
			return new ModelAndView( "jsonView", model );
	}
	
	/**
	 * 
	* <pre>
	* 1. 메소드명 : insertJoinRes
	* 2. 작성일 : 2017. 11. 30. 오후 2:22:09
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 권한 요청
	* </pre>
	* @param mypageVo
	* @param model
	* @param mv
	* @param session
	* @param userHistVo
	* @return
	* @throws Exception
	 */
	@RequestMapping(value = "/autInsertAjax.do", method = RequestMethod.POST , produces="application/json;charset=utf-8")
	public ModelAndView insertJoinRes(MypageVO mypageVo , ModelMap model, ModelAndView mv, HttpSession session, UserHistVO userHistVo, HttpServletRequest request) throws Exception{
	
		    logger.debug("Start autInsertAjax Page");
			logger.debug("userHistVo param ################################### : {}" , userHistVo);
			logger.debug("mypageVo param ################################### : {}" , mypageVo);
			UserJoinVO userJoinVO = (UserJoinVO)session.getAttribute("ssUserVo");
			logger.debug("Start user session mbrId: {}", userJoinVO.getMbrId());
			if(userJoinVO.getMbrId() != null) {
				mypageVo.setMbrId(CommonFunc.safeDbEncrypt(userJoinVO.getMbrId()));
				mypageVo.setAutSttusCd("MBRAUT1010");
				mypageVo.setRegr(CommonFunc.safeDbEncrypt(userJoinVO.getMbrId()));
				mypageVo.setAmdr(CommonFunc.safeDbEncrypt(userJoinVO.getMbrId()));
			}
			logger.debug("mypageVo mbrId: {}", mypageVo.getMbrId());
			
			mypageVo.setAutSttusCd("MBRAUT1010");
			
			//권한 중복여부 체크
			int duCnt = service.chkDupCnt(mypageVo);
			if(duCnt != 0 ) {
				String message =MessageUtil.getMsg("warning.auth.have.msg");
				logger.debug("message : {}", message);
				model.addAttribute("message", message);
				model.addAttribute("msgCd", "fail");
				return new ModelAndView( "jsonView", model );
			}
			
			//현재 보유 권한 갯수 처리
			int chCnt = service.chkInsCnt(mypageVo);
			
			/*
			 * 5개권한제한 해제함
			 * CYD - 2020.07.08
			 */
//			if(chCnt >= 5) {
//				model.addAttribute("chCnt", chCnt);
//				String message =MessageUtil.getMsg("warning.auth.cnt.msg");
//				logger.debug("message : {}", message);
//				model.addAttribute("message", message);
//				model.addAttribute("msgCd", "fail");
//			}else 
				
			//if(chCnt < 5) {
				//새로운 권한 요청
				service.newAutReq(mypageVo);
				userHistVo.setMbrId(mypageVo.getMbrId());
				userHistVo.setMgtSttusCd("MBRSTS4010");
				userHistVo.setRegr(mypageVo.getMbrId());
				//회원 관리 이력 저장
				service.autHist(userHistVo);
				model.addAttribute("chCnt", chCnt);
				String message =MessageUtil.getMsg("insert.success.msg");
				logger.debug("message : {}", message);
				//메일 발송
				Map<String,String> map = new HashMap<>();
				map.put("tempId", "100000014");
				map.put("title", "API Link 권한 신청 알림메일");
				map.put("content", userJoinVO.getMbrId()+" 님의 권한 신청이 요청 되었습니다.");
				map.put("toMail", apiLinkMail);
				sendMailUtil.sendMailcall(map);
				model.addAttribute("message", message);
				model.addAttribute("msgCd", "success");
			//}
			return new ModelAndView( "jsonView", model );
	}
}
