package com.kt.openapi.web.guide.controller;

import com.kt.openapi.web.guide.service.GuideService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.guide.controller
 * 2. 타입명   : GuideController.java
 * 3. 작성일   : 2017. 12. 12. 오전 11:13:25
 * 4. 작성자   : JeonGeun Kang
 * 5. 설명     : 이용가이드 페이지 
 * </pre>
 */
@Controller
@RequestMapping(value="/guide")
public class GuideController {
	
	@Autowired
	private GuideService guideService;
	
	private static final Logger logger = LoggerFactory.getLogger(GuideController.class);

	/**
	 * <pre>
	 * 1. 메소드명 : mvUseList
	 * 2. 작성일   : 2017. 12. 12. 오후 4:59:41
	 * 3. 작성자   : JeonGeun Kang
	 * 4. 설명     : 이용 가이드 페이지로 이동
	 * </pre>
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/mvUseList.do")
	public ModelAndView mvUseList(HttpSession session, @RequestParam(value="1", required=false) String tabCurrent) throws Exception {
		logger.debug("###################### mvUseList #################################");
		
		ModelAndView mv = new ModelAndView();
		
		mv.setViewName("guide/useList");
		
		return mv;
	}   
	
	/**
	 * <pre>
	 * 1. 메소드명 : mvSuhbList
	 * 2. 작성일   : 2017. 12. 12. 오후 4:59:51
	 * 3. 작성자   : JeonGeun Kang
	 * 4. 설명     : shub 가이드 페이지로 이동
	 * </pre>
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/mvShubList.do")
	public ModelAndView mvSuhbList(HttpSession session, HttpServletRequest request,HttpServletResponse response,  ModelMap model) throws Exception {
		logger.debug("###################### mvSuhbList #################################");
		
		ModelAndView mv = new ModelAndView();

		// SHUB API 목록 조회
		mv.addObject("shubList", guideService.selGuideShubList());
		
		mv.setViewName("guide/shubList");
		
		return mv;
	}   
}
