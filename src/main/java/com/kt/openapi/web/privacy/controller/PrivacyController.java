package com.kt.openapi.web.privacy.controller;

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
@RequestMapping(value="/priv")
public class PrivacyController {

	private static final Logger logger = LoggerFactory.getLogger(PrivacyController.class);
	
	@RequestMapping(value = "/pViewinfo.do")
	public ModelAndView pViewinfo (HttpServletRequest request, HttpServletResponse response, Locale locale, ModelMap model) throws Exception {
		
		logger.debug("Start pViewinfo Page ###########################");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("priv/view");
		
	return mav;	
	}
	
}
