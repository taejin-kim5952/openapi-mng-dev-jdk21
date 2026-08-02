package com.kt.openapi.web.authConfirm.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.openapi.web.login.vo.LoginVO;
import com.kt.openapi.web.util.CommonFunc;
import com.kt.openapi.web.util.JsonInfoUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
* <pre>

* 1. 패키지명 : com.kt.openapi.web.authConfirm.controller
* 2. 타입명 : AuthConfirmController.java
* 3. 작성일 : 2018. 7. 30. 오후 4:04:11
* 4. 작성자 : jj
* 5. 설명 : 인증

* </pre>
*/
@RestController
@RequestMapping(value="/login/auth")
public class AuthConfirmController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthConfirmController.class);
	
	@Value("${psso.api.sms.checkurl}")
	private String pssoSmsUrl;
	
	@Value("${psso.api.login.clientkey}")
	private String pssoClientKey;
	
//	@Value("${psso.api.login.checkktno}")
//	private String pssoKtempnoUrl;
	
	@Value("${pageUnit:10}")
	private int pageUnit;

	@Value("${pageSize:10}")
	private int pageSize;
	
	@Autowired
	JsonInfoUtil jsonInfoUtil;
	
	/**
	* <pre>
	
	* 1. 메소드명 : smsSend
	* 2. 작성일 : 2018. 7. 30. 오후 4:08:28
	* 3. 작성자 : jj
	* 4. 설명 : PSSO SMS 인증
	
	* </pre>
	* @param mv
	* @param request
	* @param param
	* @param model
	* @return
	* @throws Exception	
	* @throws KeyManagementException
	* @throws NoSuchAlgorithmException
	* @throws UnsupportedEncodingException
	* @throws IOException
	*/
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/smsSend.do")
	@ResponseBody
	public ModelAndView smsSend(ModelAndView mv, HttpServletRequest request, LoginVO param, ModelMap model) throws Exception, KeyManagementException, NoSuchAlgorithmException, UnsupportedEncodingException, IOException {
		
		String returnNumber = "";
		
		setSession(request.getSession(), false);
		
		if(param.getPhoneNo() != null && !"".equals(param.getPhoneNo())) {
			// PSSO
			Map<String, Object> resultMap = jsonInfoUtil.readJsonFromUrl(pssoSmsUrl+"?ClientKey="+pssoClientKey+"&MobileNumber="+URLEncoder.encode(CommonFunc.safeDbEncrypt(param.getPhoneNo().replaceAll("-", "")),"EUC-KR"));
			String jsonText = resultMap.containsKey("jsonText") ? (String)resultMap.get("jsonText") : "";
			LOGGER.debug("jsonText AI_URL : {}", jsonText);
			
			ObjectMapper mapper = new ObjectMapper();
			HashMap<String, String> jsonMap = mapper.readValue(jsonText, new TypeReference<HashMap<String, String>>() {});
			
			returnNumber  = jsonMap.containsKey("ReturnNumber") ? jsonMap.get("ReturnNumber") : "";
			LOGGER.debug("PSSO SMS 인증확인 : {}", returnNumber );
		}
		
		request.getSession().setAttribute("returnNumber", CommonFunc.safeDbPassEncrypt(returnNumber));
		
		model.addAttribute("returnType", !"".equals(returnNumber));
		return new ModelAndView( "jsonView", model );
	}

	/**
	* <pre>
	
	* 1. 메소드명 : phoneConfirm
	* 2. 작성일 : 2018. 7. 31. 오후 2:00:26
	* 3. 작성자 : jj
	* 4. 설명 : 휴대폰 인증번호 확인
	
	* </pre>
	* @param mv
	* @param request
	* @param model
	* @param certifyCode
	* @return
	* @throws Exception
	* @throws UnsupportedEncodingException
	*/
	@RequestMapping(value = "/phoneConfirm.do")
	@ResponseBody
	public ModelAndView phoneConfirm(ModelAndView mv, HttpServletRequest request, ModelMap model,  
									@RequestParam("certifyCode") String certifyCode) throws Exception, UnsupportedEncodingException {
		
		String returnNumber = (String) request.getSession().getAttribute("returnNumber");
		
		boolean checkType = CommonFunc.safeDbPassEncrypt(certifyCode).equals(returnNumber);
		
		LOGGER.debug("phoneConfirm 휴대폰 인증여부 : {} "+ checkType);
		setSession(request.getSession(), checkType);
		
		model.addAttribute("phoneResultType", checkType);
		return new ModelAndView( "jsonView", model );
	}
	
	/**
	* <pre>
	
	* 1. 메소드명 : random
	* 2. 작성일 : 2018. 7. 30. 오후 4:57:48
	* 3. 작성자 : jj
	* 4. 설명 : 
	
	* </pre>
	* @return
	* @throws Exception
	*/
	public String random(int range) throws Exception {
		String result = RandomStringUtils.random(range, true, true);
		LOGGER.debug("result 난수발생 "+ range +"자리 : {}", result);
		return result;
	}

	/**
	* <pre>
	
	* 1. 메소드명 : setSession
	* 2. 작성일 : 2018. 7. 31. 오전 10:59:24
	* 3. 작성자 : jj
	* 4. 설명 : 인증체크 session
	
	* </pre>
	* @param session
	* @param authCheck
	*/
	public void setSession(HttpSession session, boolean authCheck) {
		session.setAttribute("authCheck", authCheck);
	}
	
	/**
	* <pre>
	
	* 1. 메소드명 : authCheck
	* 2. 작성일 : 2018. 7. 31. 오전 11:01:06
	* 3. 작성자 : jj
	* 4. 설명 : 인증권한 체크
	
	* </pre>
	* @param mv
	* @param request
	* @param model
	* @return
	* @throws Exception
	* @throws UnsupportedEncodingException
	*/
	@RequestMapping(value = "/authCheck.do")
	@ResponseBody
	public ModelAndView authCheck(ModelAndView mv, HttpServletRequest request, ModelMap model) throws Exception, UnsupportedEncodingException {
		model.addAttribute("authCheck", request.getSession().getAttribute("authCheck"));
		return new ModelAndView( "jsonView", model );
	}
	
}

