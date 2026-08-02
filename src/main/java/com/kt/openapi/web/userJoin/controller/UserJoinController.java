package com.kt.openapi.web.userJoin.controller;

import com.initech.safedb.crypto.util.UUID;
import com.kt.openapi.web.adptran.util.KsmUtil;
import com.kt.openapi.web.cmmn.ApiException;
import com.kt.openapi.web.cmmn.logutil.ApilinkLogUtil;
import com.kt.openapi.web.userJoin.service.UserJoinService;
import com.kt.openapi.web.userJoin.vo.UserHistVO;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import com.kt.openapi.web.util.CommonFunc;
import com.kt.openapi.web.util.RsaManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.regex.Pattern;


/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.userJoin.controller
* 2. 타입명 : UserJoinController.java
* 3. 작성일 : 2017. 11. 30. 오후 2:44:40
* 4. 작성자 : ANEUNTAEK
* 5. 설명 : 회원 가입
* </pre>
*/
@Controller
@RequestMapping(value = "/userJoin")
public class UserJoinController {

	private static final Logger logger = LoggerFactory.getLogger(UserJoinController.class);

	@Autowired
    private UserJoinService userJoinService;
    
	@Autowired
	private RsaManager rsaCall; // 화면단 암호화 된 패스워드 값 복호화
	
	//신규 로그인 모드
	@Value("${new.login.mode}")
	private String newLoginMode;	
	
	@Value("${logcenter.record.mode}")
	private String logcenterRecordMode;

	/**
	* <pre>
	* 1. 메소드명 : userJoinView
	* 2. 작성일 : 2017. 11. 30. 오후 2:44:55
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 회원가입 FORM
	* </pre>
	* @param request
	* @param response
	* @param model
	* @return
	* @throws Exception
	*/
	@RequestMapping(value = "/userJoinForm.do")
	public ModelAndView userJoinView(HttpServletRequest request, HttpServletResponse response, HttpSession session, UserJoinVO userVo) throws Exception {

		logger.info("회원가입 전 약관동의 페이지로 이동");		

		ModelAndView mv = new ModelAndView();

		mv.setViewName("userJoin/userForm");
		
		return mv;
	}

	/**
	 * 
	* <pre>
	* 1. 메소드명 : userInfoView
	* 2. 작성일 : 2017. 11. 30. 오후 2:48:40
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 회원 가입 양식 
	* </pre>
	* @param request
	* @param response
	* @param model
	* @param userJoinVO
	* @return
	* @throws Exception
	 */
	@RequestMapping(value = "/userInfo.do")
	public ModelAndView userInfoView(HttpServletRequest request, HttpServletResponse response, UserJoinVO userJoinVO, HttpSession session) throws Exception {

		ModelAndView mv = new ModelAndView();
		
		logger.info("newLoginMode 확인 : {}", newLoginMode);
				
		String mbrNm = KsmUtil.fnSafeStr(session.getAttribute("memberName"));
		String mbrId = KsmUtil.fnSafeStr(session.getAttribute("memberId"));
		String mbrPhone = KsmUtil.fnSafeStr(session.getAttribute("memberPhone"));
		String mbrEmail = KsmUtil.fnSafeStr(session.getAttribute("memberEmail"));
		
		if("".equals(KsmUtil.fnSafeStr(mbrId))) {
			mv.setViewName("userJoin/userForm");			
			return mv;
		}
		
		HashMap<String, String> returnMap = new HashMap<String, String>();
		returnMap = rsaCall.getWebModuluExp();
		String publicKeyModulus  = returnMap.get("RSAModulus");
		String publicKeyExponent = returnMap.get("RSAExponent");

		mv.addObject("publickeymodulus" , publicKeyModulus);
		mv.addObject("publickeyexponent", publicKeyExponent);
			
		logger.info("mbrNm 확인 : {}", mbrNm);
		logger.info("mbrId 확인 : {}", mbrId);
		logger.info("mbrPhone 확인 : {}", mbrPhone);
		logger.info("mbrEmail 확인 : {}", mbrEmail);		

		//화면에 보여지는 개인정보 마스킹 처리
		userJoinVO.setMbrId(CommonFunc.strMasking(mbrId, "id"));
		userJoinVO.setMbrNm(CommonFunc.strMasking(mbrNm, "exceptForOne"));
		userJoinVO.setTelNo("".equals(mbrPhone) ? null : CommonFunc.strMasking(mbrPhone, "phone"));
		userJoinVO.setEmail("".equals(mbrEmail) ? null : CommonFunc.strMasking(mbrEmail, "email"));
		
		mv.addObject("userInfo", userJoinVO);
		mv.setViewName("userJoin/userInfo");
		
		return mv;
		
	}
	
	/**
	 * 
	* <pre>
	* 1. 메소드명 : insertJoinRes
	* 2. 작성일 : 2017. 11. 30. 오후 2:50:41
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 회원 가입 등록
	* </pre>
	* @param userJoinVO
	* @param model
	* @param mv
	* @return
	* @throws Exception
	 */
	@RequestMapping(value = "/insertJoin.do", method = RequestMethod.POST , produces="application/json;charset=utf-8")
	public ModelAndView insertJoinRes(UserJoinVO userJoinVO, ModelMap model, ModelAndView mv, HttpSession session, HttpServletRequest request) throws Exception{
			logger.debug("insertJoin start ################################### : {}" , userJoinVO);
			
			String transactionId = UUID.randomUUID().toString();
			String mbrNm = KsmUtil.fnSafeStr(session.getAttribute("memberName"));
			String mbrId = KsmUtil.fnSafeStr(session.getAttribute("memberId"));
			String mbrPhone = KsmUtil.fnSafeStr(session.getAttribute("memberPhone"));
			String mbrEmail = KsmUtil.fnSafeStr(session.getAttribute("memberEmail"));
			ApilinkLogUtil logUtil;
			
			//이름 ID는 필수정보라서 없을 수 없음
			if("".equals(mbrNm) || "".equals(mbrId)) {
				model.addAttribute("message" , "fail");
				return new ModelAndView( "jsonView", model );	
			}
						
			String decEmail = "";
			String decTelNo = "";
			
			if("Y".equals(logcenterRecordMode)) {
				logUtil = new ApilinkLogUtil(request, "loginJoin", "join", mbrId, mbrId, transactionId,"CREATE","UPIP", "id:"+mbrId+", name:"+mbrNm+", email:"+mbrEmail+", mobile:"+mbrPhone);
				logUtil.procLogstandard(ApilinkLogUtil.DEF_LOG_REQ, "[join-res]");
			}

			logger.debug("RSA 암호화된 이메일 $$$ : {}", userJoinVO.getEmail());
			logger.debug("RSA 암호화된 전화번호 $$$ : {}"  , userJoinVO.getTelNo());
			try {
				decEmail = "".equals(mbrEmail) ? rsaCall.webDecrypt(userJoinVO.getEmail()) : mbrEmail;
				logger.debug("RSA 복호화된 이메일 $$$ : {}", decEmail);
				// RSA로 암호화된 비밀번호를 복호화
				decTelNo = "".equals(mbrPhone) ? rsaCall.webDecrypt(userJoinVO.getTelNo()) : mbrPhone;
				logger.debug("RSA 복호화된 전화번호 $$$ : {}", decTelNo);
			} catch (ApiException e) {
				// TODO Auto-generated catch block
				logger.debug("RSA 복호화 오류 $$$ : {}", e.getMessage());
				e.printStackTrace();
			}

			//전화 번호 양식 변환 (하이픈 추가)
			String formatPnum = makePhoneNumber(decTelNo);
			logger.debug("formatPnum : {}" , formatPnum);

			UserJoinVO insUserInfo = new UserJoinVO();
			
			//개인 정보 암호화
			insUserInfo.setIndvInfoAgreeYn(userJoinVO.getIndvInfoAgreeYn());
			insUserInfo.setStpltAgreeYn(userJoinVO.getStpltAgreeYn());			
			insUserInfo.setIdDivCd(userJoinVO.getIdDivCd());			
			insUserInfo.setMbrId(CommonFunc.safeDbEncrypt(mbrId));
			insUserInfo.setMbrNm(CommonFunc.safeDbEncrypt(mbrNm));			
			insUserInfo.setCmpnNm(CommonFunc.safeDbEncrypt(userJoinVO.getCmpnNm()));
			insUserInfo.setTelNo(CommonFunc.safeDbEncrypt(formatPnum));
			insUserInfo.setEmail(CommonFunc.safeDbEncrypt(decEmail));
			insUserInfo.setMbrSttusCd("MBRSTS1020");
			
			userJoinService.insertUserJoin(insUserInfo); //apimanager 회원정보 등록
			
			UserHistVO histVo = new UserHistVO();
			histVo.setMbrId(insUserInfo.getMbrId());
			histVo.setMgtSttusCd("MBRSTS1020");
			histVo.setMemo("회원가입");
			histVo.setRegr(insUserInfo.getMbrId());
			userJoinService.insertMgtHist(histVo); //회원정보 등록 히스토리
			model.addAttribute("message" , "success");
			
			session.removeAttribute("memberName");
			session.removeAttribute("memberId");
			session.removeAttribute("memberPhone");
			session.removeAttribute("memberEmail");
			
			//[LAMP로그기록 - RES]
			if("Y".equals(logcenterRecordMode)) {
				logUtil = new ApilinkLogUtil(request, "loginJoin", "join", mbrId, mbrId, transactionId,"CREATE","UPIP", "id:"+mbrId+", name:"+mbrNm+", email:"+mbrEmail+", mobile:"+mbrPhone);
				logUtil.procLogstandard(ApilinkLogUtil.DEF_LOG_RES, "[join-res]");
			}
			return new ModelAndView( "jsonView", model );	
					
	}
	
	/**
	 * 
	* <pre>
	* 1. 메소드명 : userJoinComplete
	* 2. 작성일 : 2017. 11. 30. 오후 2:52:05
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 회원 가입 완료 정보
	* </pre>
	* @param request
	* @param response
	* @param model
	* @param param
	* @return
	* @throws Exception
	 */
	@RequestMapping(value = "/sJoinInfo.do")
	public ModelAndView userJoinComplete(HttpServletRequest request, HttpServletResponse response, ModelMap model, UserJoinVO param)
			throws Exception {

		logger.debug("Start selectJoinInfo Page");
		ModelAndView mav = new ModelAndView();
		UserJoinVO map = userJoinService.selectUserJoin(param);
		HashMap<String, String> hashmap = new HashMap<>();
		String descCode = map.getIdDivCd(); 
		String descId = map.getMbrId(); 
		String descName = map.getMbrNm(); 
		String descCom = map.getCmpnNm(); 
		String descPhone = map.getTelNo();  
		String descEmail = map.getEmail();
		
		hashmap.put("authCode", descCode);
		hashmap.put("userId", CommonFunc.strMasking(CommonFunc.safeDbDecrypt(descId), "id"));
		hashmap.put("userName", CommonFunc.strMasking(CommonFunc.safeDbDecrypt(descName),"name"));
		hashmap.put("company", CommonFunc.safeDbDecrypt(descCom));
		hashmap.put("phone", CommonFunc.strMasking(CommonFunc.safeDbDecrypt(descPhone),"phone"));
		hashmap.put("email", CommonFunc.strMasking(CommonFunc.safeDbDecrypt(descEmail),"email"));
	
		model.addAttribute("jmap", hashmap);
		mav.setViewName("userJoin/userJoin");
		return mav; 
	}
	
	//전화번호 양식 변경
	public static String makePhoneNumber(String phoneNumber) {
		   String regEx = "(\\d{3})(\\d{3,4})(\\d{4})";
		   logger.debug("true or false : {}" , Pattern.matches(regEx, phoneNumber) );
		   if(!Pattern.matches(regEx, phoneNumber)) return null;
		   logger.debug("returndata: {}" , phoneNumber.replaceAll(regEx, "$1-$2-$3") );
		   return phoneNumber.replaceAll(regEx, "$1-$2-$3");		   
	}

}
