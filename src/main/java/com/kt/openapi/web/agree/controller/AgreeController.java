package com.kt.openapi.web.agree.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Locale;

@Controller
@RequestMapping(value="/agree")
public class AgreeController {

private static final Logger logger = LoggerFactory.getLogger(AgreeController.class);
	
	@RequestMapping(value = "/agViewinfo.do")
	public ModelAndView agViewinfo (HttpServletRequest request, HttpServletResponse response, Locale locale, ModelMap model) throws Exception {
		
		logger.debug("Start agViewinfo Page ###########################");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("agree/view");
		
	return mav;	
	}
}
