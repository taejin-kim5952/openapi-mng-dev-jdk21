package com.kt.openapi.web.adptran.api.controller;

import com.kt.openapi.web.adptran.api.AdptranApiConst;
import com.kt.openapi.web.adptran.api.common.message.GenericMessage;
import com.kt.openapi.web.adptran.api.common.message.RestMessage;
import com.kt.openapi.web.adptran.api.service.ApistatusApiService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping(value = AdptranApiConst.APISTATUS_API_PATH + AdptranApiConst.APISTATUS_API_VERSION)
@RestController
public class ApistatusApiController {

	private static final Logger logger = LoggerFactory.getLogger(ApistatusApiController.class);

	//--##@Autowired
	@Autowired
	private ApistatusApiService apistatusApiService;

	@RequestMapping(value = "/{group}/{ifname}", method = { RequestMethod.POST }, consumes = "application/json", produces = "application/json; charset=utf8")
	public RestMessage apistatus_api(@PathVariable String group, @PathVariable String ifname, @RequestBody Map<String, Object> param, HttpServletRequest request) throws Exception {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
		
		GenericMessage message = apistatusApiService.get_apistatus_api_data(group, ifname, param, request); 
		return message;
	}
}
