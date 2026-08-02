package com.kt.openapi.web.main.controller;

import com.kt.openapi.web.main.service.MainService;
import com.kt.openapi.web.main.vo.MainBBSVO;
import com.kt.openapi.web.main.vo.MainVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Locale;

/**
 * 
* <pre>
* 1. 패키지명 : com.kt.openapi.web.main.controller
* 2. 타입명 : MainController.java
* 3. 작성일 : 2017. 11. 30. 오후 2:10:21
* 4. 작성자 : ANEUNTAEK
* 5. 설명 : MAIN
* </pre>
 */
@Controller
@RequestMapping(value="/main")
public class MainController {
	
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	@Autowired
	@Qualifier("mainService")
	private MainService service;
	
	/**
	 * 
	* <pre>
	* 1. 메소드명 : mainView
	* 2. 작성일 : 2017. 11. 30. 오후 2:10:37
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : MAIN 페이지 이동
	* </pre>
	* @param request
	* @param response
	* @param locale
	* @param model
	* @param session
	* @return
	* @throws Exception
	 */
	@RequestMapping(value = "/index.do")
	public ModelAndView mainView(HttpServletRequest request, HttpServletResponse response, Locale locale, ModelMap model, HttpSession session, MainVO mainVo) throws Exception {
		
		ModelAndView mav = new ModelAndView();
		logger.debug("Start main Page");
		mav.setViewName("main/main");
		
		//공지사항 최신글 3개
		List<MainBBSVO> resNList = service.selRecNotice(mainVo);

		//개발자 포럼 최신글 3개
		List<MainBBSVO> resDevFList = service.selRecDevF(mainVo);
		
		model.addAttribute("resNList", resNList);
		model.addAttribute("resDevFList", resDevFList);

		return mav;
	}
	
}
