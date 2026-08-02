package com.kt.openapi.web.rest.auth.controller;

import com.kt.openapi.web.login.controller.LoginController;
import com.kt.openapi.web.rest.auth.service.AuthService;
import com.kt.openapi.web.rest.auth.vo.SearchVO;
import com.kt.openapi.web.rest.auth.vo.StatBodyVO;
import com.kt.openapi.web.rest.util.ErrorValidator;
import com.kt.openapi.web.util.CommonFunc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;


/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.rest.auth.controller
* 2. 타입명 : RestController.java
* 3. 작성일 : 2017. 12. 5. 오전 9:59:44
* 4. 작성자 : user
* 5. 설명 : 권한 체크
* </pre>
*/
@Controller
@RequestMapping(value="/mbr.json")
public class AuthController {
	
	private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

	private final LoginController loginController;

	@Autowired
	@Qualifier("authService")
	private AuthService service;

	@Autowired
	public AuthController(LoginController loginController) {
		this.loginController = loginController;
	}
	
	@Value("${pageUnit:10}")
	private int pageUnit;

	@Value("${pageSize:10}")
	private int pageSize;

	@Autowired
	private ErrorValidator errorValidator;
	 
	
	/**
	* <pre>
	* 1. 메소드명 : chkauth
	* 2. 작성일 : 2017. 12. 5. 오전 10:00:56
	* 3. 작성자 : user
	* 4. 설명 : 권한 존재 여부 체크
	* </pre>
	* @param request
	* @param response
	* @param map
	* @return
	* @throws Exception
	* http://10.214.188.79:8080/apidev/mbr.json?mbrId=0001EDL7paepLUowDTqveogIbg==&apiNo=1171
	*/
//	@RequestMapping(value="/chkauth.json")
	@RequestMapping(method=RequestMethod.GET)
	public HashMap<String,Object>  chkauth(SearchVO svo, BindingResult result) throws Exception {
		HashMap<String,Object> ret = new HashMap<String,Object>();
		
		LOG.debug("#######################   chkauth START ############################");
		LOG.debug("result   : {}", result);
		LOG.debug("SearchVO   : {}", svo);
		LOG.debug("SearchVO   : {}", svo);
		
		errorValidator.validate(svo, result);
		
		StatBodyVO root = new StatBodyVO();
			
			LOG.debug("result 결과 값   : {}", result.hasErrors());
			LOG.debug("svo.getMbrId()  => {}", svo.getMbrId());
			
			if(result.hasErrors()){
				root.setResult("400");
				return ret;
		    }
			
			
			String loginYn 	= "";
			String mbrId 	= CommonFunc.safeDbDecrypt(svo.getMbrId());
			
			LOG.debug("mbrId  => {}", mbrId);
			
			LOG.debug("loginController.isUsing(mbrId)  => {}", loginController.isUsing(mbrId));

			//세션에 해당 유저의 정보가 존재 할 경우 만 유효
			if (loginController.isUsing(mbrId)) {
				root.setLoginYn("Y");
				loginYn = "Y";
			}else {
				loginYn = "N";
			}
			
			LOG.debug("loginYn  => {}", loginYn);
			
			String authYn = "";
			
			int authCnt 	= service.selAuth(svo);	// 권한 존재 여부 조회
			
			if(authCnt>0) {
				authYn = "Y";
			}else {
				authYn = "N";
			}
			
			LOG.debug("authCnt =  {}", authCnt);
			
//			loginYn = "Y";
//			authYn  = "Y";
			
			ret.put("loginYn" , loginYn );
			ret.put("authYn" , authYn );
			
			root.setResult("200");
		
		return ret;
	}

	
	@RequestMapping(method=RequestMethod.POST)
	public HashMap<String,Object>  chkauth2( @RequestBody SearchVO svo, BindingResult result) throws Exception {
		HashMap<String,Object> ret = new HashMap<String,Object>();
		
		LOG.debug("#######################   chkauth2 START ############################");
		LOG.debug("result   : {}", result);
		LOG.debug("SearchVO   : {}", svo);
		
		errorValidator.validate(svo, result);
		
		StatBodyVO root = new StatBodyVO();
			
			LOG.debug("result 결과 값   : {}", result.hasErrors());
			
			if(result.hasErrors()){
				root.setResult("400");
				return ret;
		    }
			
			int authCnt 	= service.selAuth(svo);	// 권한 존재 여부 조회
			String loginYn 	= "";
			
			//세션에 해당 유저의 정보가 존재 할 경우 만 유효
			if (loginController.isUsing(svo.getMbrId())) {
				loginYn = "Y";
			}else {
				loginYn = "N";
			}
			
			String authYn = "";
			
			if(authCnt>0) {
				authYn = "Y";
			}else {
				authYn = "N";
			}
			
//			loginYn = "Y";
//			authYn  = "Y";
			
			ret.put("loginYn" , loginYn );
			ret.put("authYn" , authYn );
			
			root.setResult("200");
		
		return ret;
	}
	
	
}
