package com.kt.openapi.web.localApiServer;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kt.openapi.web.cmmn.ApiException;
import com.kt.openapi.web.util.CommonFunc;

@Controller
public class ShubRestApiController {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ShubRestApiController.class);

	@ResponseBody
	@RequestMapping(value="/PSSO_API/DefaultLogin.asp")
    public  String defaultLogin() {
		
		String returnJson;
		String userName = "";
		try {
			userName = CommonFunc.safeDbEncrypt("홍길동");
		} catch (UnsupportedEncodingException e) {
			//-- [tag:SR-20201119][sparrow][correction]
			LOGGER.debug("\n\n### {}.{}() [UnsupportedEncodingException: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e);
		} catch (ApiException e) {
			//-- [tag:SR-20201119][sparrow][correction]
			LOGGER.debug("\n\n### {}.{}() [ApiException: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		returnJson  = "{\"ReturnCode\" : \"11\", \"ReturnName\" : \"" + userName + "\"}";
		
        return returnJson;
    }
}
