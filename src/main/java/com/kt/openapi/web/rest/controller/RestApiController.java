package com.kt.openapi.web.rest.controller;

import com.kt.openapi.web.cmmn.ApiException;
import com.kt.openapi.web.util.ShubRestApiCallFunction;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RestController
@RequestMapping(value="/rest")
public class RestApiController {
	private static final Logger LOG = LoggerFactory.getLogger(RestApiController.class);

	@RequestMapping(value = "/authTest.do")
	public ModelAndView authTest(HttpServletRequest request, ModelMap model, @RequestParam(name = "target") String target, @RequestParam(name = "apiId") String apiId) throws ApiException {
		
		LOG.debug("authTest 실행");
		
		Map<String, Object> resultMap = ShubRestApiCallFunction.authTest(target, apiId);
		LOG.debug("resultMap 확인 : {}", resultMap);
		
		model.addAllAttributes(resultMap);
		return new ModelAndView( "jsonView", model );
	}	
}
