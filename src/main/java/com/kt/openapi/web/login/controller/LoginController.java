package com.kt.openapi.web.login.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.initech.safedb.crypto.util.UUID;
import com.kt.openapi.web.adptran.util.KsmUtil;
import com.kt.openapi.web.auth.vo.AuthVO;
import com.kt.openapi.web.cmmn.ApiException;
import com.kt.openapi.web.cmmn.logutil.ApilinkLogUtil;
import com.kt.openapi.web.login.service.LoginService;
import com.kt.openapi.web.login.vo.LoginVO;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import com.kt.openapi.web.util.*;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 *
* <pre>
* 1. 패키지명 : com.kt.openapi.web.login.controller
* 2. 타입명 : LoginController.java
* 3. 작성일 : 2017. 11. 30. 오후 1:51:06
* 4. 작성자 : ANEUNTAEK
* 5. 설명 : LOGIN
* </pre>
 */
@Controller
@RequestMapping(value="/login")
public class LoginController implements HttpSessionBindingListener{

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	//psso 로그아웃 url
	@Value("${psso.logout.url}")
	private String pssoLogoutHost;

	//-- [tag:login][drm][add][181027][from apilink] {
	// psso 로그인 체크
	@Value("${psso.api.login.checkurl}")
	private String pssoCheckUrl;

	@Value("${psso.api.login.clientkey}")
	private String pssoclientkey;

	@Value("${psso.api.login.status.success}")
	private String pssoSuccess;

	@Value("${config.runmode}")
	private String adptranConfigRunmode;

	@Value("${config.use.login.ide}")
	private String adptranConfigUseLoginIde;

	//-- [tag:login] }
	//-- [tag:login][drm][add][181027] {
	@Value("${psso.api.login.method}")
	private String pssoApiLoginMethod;
	//-- [tag:login] }

	@Autowired
	JsonInfoUtil jsonInfoUtil;

	//신규 PSSO 로그인 API 연동 URL
	@Value("${new.psso.api.member.logincheck}")
	private String memberLoginCheck;

	//PSSO용 aes256 암복호화 키
	@Value("${psso.aes.key}")
	private String aesKey;

	//신규 PSSO API 사용 모드
//	@Value("${new.psso.login.mode}")
//	private String newPssoLoginMode;

	//2FA API 연동을 위한 인증키
	@Value("${shub.authorization.key}")
	private String shubAuthorizationKey;

	//verify_check API 연동 URL
	@Value("${shub.2fa.verifycheck.url}")
	private String verifyCheckUrl;

	//verify_send API 연동 URL
	@Value("${shub.2fa.verifysend.url}")
	private String verifySendUrl;

	//신규 로그인 모드
	@Value("${new.login.mode}")
	private String newLoginMode;

	@Value("${logcenter.record.mode}")
	private String logcenterRecordMode;

	@Value("${apilink.user.terms.form.link}")
	private String userTermsFormLink;
	
	@Value("${apilink.find.user.id.link}")
	private String findUserIdLink;
	
	@Value("${apilink.find.user.pw.link}")
	private String findUserPwLink;

	@Value("${apisystem.observer.autid}")
	private String apisystemObserverAutid;
	
	@Value("${apisystem.observer.autnm}")
	private String apisystemObserverAutnm;
	
	
	//로그인한 접속자를 담기위한 해시테이블
    private static Hashtable<Object, Object> loginUsers = new Hashtable<Object, Object>();

    //private static LoginController loginManager = null;

	@Autowired
	@Qualifier("loginService")
	private LoginService service;

	@Value("${pageUnit:10}")
	private int pageUnit;

	@Value("${pageSize:10}")
	private int pageSize;

	@Autowired
	private RsaManager rsaCall; // 화면단 암호화 된 패스워드 값 복호화

    /*
     * 이 메소드는 세션이 연결되을때 호출된다.(session.setAttribute("login", this))
     * Hashtable에 세션과 접속자 아이디를 저장한다.
     */
    public void valueBound(HttpSessionBindingEvent event) {
    	 logger.debug(  "valueBound   event  : {}"  , event);
    	 logger.debug(  "valueBound   event getValue : {}"  ,  event.getValue());
    	 logger.debug(  "valueBound   event getSource : {}"  ,  event.getSource());
    	 logger.debug(  "valueBound   event getSession : {}"  ,  event.getSession());
    	 logger.debug(  "valueBound   event getName : {}"  , event.getName() );
        //session값을 put한다.
        loginUsers.put(event.getSession(), event.getName());
        logger.debug(  "valueBound   loginUsers : {}"  ,loginUsers);
        logger.debug(  "valueBound   loginUsers GET : {}"  ,loginUsers.get( event.getName()));
        logger.debug(  "valueBound  현재 접속자 수 : {}"  , getUserCount() );
     }


     /*
      * 이 메소드는 세션이 끊겼을때 호출된다.(invalidate)
      * Hashtable에 저장된 로그인한 정보를 제거해 준다.
      */
     public void valueUnbound(HttpSessionBindingEvent event) {
    	 logger.debug(" valueUnbound START @@@@@@@@@@@@@@@@@@@@@@@@@");
    	 logger.debug(  "valueUnbound   event  : {}"  , event);
    	 logger.debug(  "valueUnbound   event  : {}"  , event.getSession());
    	 logger.debug(  "valueUnbound   event getName : {}"  , event.getName() );
         //session값을 찾아서 없애준다.
         loginUsers.remove(event.getSession());
         logger.debug(  "valueUnbound  현재 접속자 수 : {}"  , getUserCount() );
     }

     /*
      * 입력받은 아이디를 해시테이블에서 삭제.
      * @param userID 사용자 아이디
      * @return void
      */
     @SuppressWarnings("rawtypes")
	public void removeSession(String userId){
          Enumeration e = loginUsers.keys();
          HttpSession session = null;
          while(e.hasMoreElements()){
               session = (HttpSession)e.nextElement();
               if(loginUsers.get(session).equals(userId)){
            	   //세션이 invalidate될때 HttpSessionBindingListener를
                   //구현하는 클레스의 valueUnbound()함수가 호출된다.
                   session.invalidate();
               }

          }
     }

     /*
      * 로그인을 완료한 사용자의 아이디를 세션에 저장하는 메소드
      * @param session 세션 객체
      * @param userID 사용자 아이디
      */
     public void setSession(HttpSession session, String userId){
         //이순간에 Session Binding이벤트가 일어나는 시점
         //name값으로 userId, value값으로 자기자신(HttpSessionBindingListener를 구현하는 Object)
         session.setAttribute(userId, this);//login에 자기자신을 집어넣는다.
     }
    /*
     * 해당 아이디의 동시 사용을 막기위해서
     * 이미 사용중인 아이디인지를 확인한다.
     * @param userID 사용자 아이디
     * @return boolean 이미 사용 중인 경우 true, 사용중이 아니면 false
     */
    public boolean isUsing(String userID){
        return loginUsers.containsValue(userID);
    }

    /*
      * 입력받은 세션Object로 아이디를 리턴한다.
      * @param session : 접속한 사용자의 session Object
      * @return String : 접속자 아이디
     */
    public String getUserID(HttpSession session){
        return (String)loginUsers.get(session);
    }

    /*
     * 현재 접속한 총 사용자 수
     * @return int  현재 접속자 수
     */
    public int getUserCount(){
        return loginUsers.size();
    }


    /*
     * 현재 접속중인 모든 사용자 아이디를 출력
     * @return void
     */
    @SuppressWarnings("rawtypes")
	public void printloginUsers(){
        Enumeration e = loginUsers.keys();
        HttpSession session = null;
        logger.debug("======= printloginUsers  Start ==========");
        int i = 0;
        while(e.hasMoreElements()){
            session = (HttpSession)e.nextElement();
            logger.debug( (++i) + ". 접속자 :  {}"  ,  loginUsers.get(session));
        }
        logger.debug(  "========printloginUsers  End ===========");
     }

    /*
     * 현재 접속중인 모든 사용자리스트를 리턴
     * @return list
     */
    public Collection getUsers(){
    	logger.debug("======= getUsers  Start ==========");
        Collection collection = loginUsers.values();
        logger.debug("======= getUsers  End ==========");
        return collection;
    }

	/**
	 *
	* <pre>
	* 1. 메소드명 : loginView
	* 2. 작성일 : 2017. 11. 30. 오후 1:52:25
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : LOGIN FORM
	* </pre>
	* @param request
	* @param response
	* @param locale
	* @param model
	* @param session
	* @return
	* @throws Exception
	 */
	@RequestMapping(value = "/loginForm.do")
	public ModelAndView loginView(HttpServletRequest request, HttpServletResponse response, Locale locale, ModelMap model, HttpSession session) throws Exception {
		ModelAndView mv = new ModelAndView();

		logger.info("Go Login Page");
		
		HashMap<String, String> returnMap = new HashMap<String, String>();
		returnMap = rsaCall.getWebModuluExp();
		String publicKeyModulus  = returnMap.get("RSAModulus");
		String publicKeyExponent = returnMap.get("RSAExponent");

		mv.addObject("userTermsFormLink" , this.userTermsFormLink);
		mv.addObject("findUserIdLink" , this.findUserIdLink);
		mv.addObject("findUserPwLink" , this.findUserPwLink);
		
		mv.addObject("publickeymodulus" , publicKeyModulus);
		mv.addObject("publickeyexponent", publicKeyExponent);

		mv.setViewName("login/newLoginForm");

		return mv;
	}

	//-- [tag:login][drm][add][181027][from apilink]
	/**
	* <pre>

	* 1. 메소드명 : loginCheck
	* 2. 작성일 : 2018. 7. 30. 오후 5:30:09
	* 3. 작성자 : jj
	* 4. 설명 : 로그인 체크

	* </pre>
	* @param mv
	* @param request
	* @param session
	* @param redirectAttributes
	* @param param
	* @return
	* @throws Exception
	* @throws KeyManagementException
	* @throws NoSuchAlgorithmException
	* @throws IOException BDC_BackWorkaround_springFormCaptcha
	*/
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/loginCheck.do")
	public ModelAndView loginCheck(ModelAndView mv, HttpServletRequest request, HttpSession session, RedirectAttributes redirectAttributes,
			@RequestParam(value = "userId", defaultValue = "") String reqUserId,
			@RequestParam(value = "pssoPw", defaultValue = "") String reqPssoPw,
			@RequestParam(value = "captchaCode", defaultValue = "") String captchaCode

			) throws Exception, KeyManagementException, NoSuchAlgorithmException, IOException {

		/* RSA 복호화
		 * Program By CYD - 2022.07.20
		 *///////////////////////////////////////////////////
		String decUserId = "";
		String decPw 	 = "";

		logger.debug("RSA 암호화된 아이디 $$$ : {}", reqUserId);
		logger.debug("RSA 암호화된 비번 $$$ : {}"  , reqPssoPw);
		try {
			decUserId = rsaCall.webDecrypt(reqUserId);
			logger.debug("RSA 복호화된 아이디 $$$ : {}", decUserId);
			// RSA로 암호화된 비밀번호를 복호화
			decPw = rsaCall.webDecrypt(reqPssoPw);
			logger.debug("RSA 복호화된 비번 $$$ : {}", decPw);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			logger.debug("RSA 복호화 오류 $$$ : {}", e.getMessage());
			e.printStackTrace();
		}
		/////////////////////////////////////////////////////

		//--##if ("dev".equals(adptranConfigRunmode) == true) {
		//--[tag:adpt][drm][191125][chg][ide login] {
		/*-- [ref][/loginCheck.do not_used]
		if ("y".equals(adptranConfigUseLoginIde) == true) {
			//--[tag:adpt][drm][190402][add][ide login][ing] {
			boolean b_is_ide_login = ((true == "master".equalsIgnoreCase(captchaCode)) && (true == decUserId.equals(decPw)));
			String s_ide_userid = "";
			String s_ide_usernm = "";
			if (true == b_is_ide_login) {
				if (true == captchaCode.equalsIgnoreCase(decUserId)) {
					s_ide_userid = "netesc2";
					s_ide_usernm = "장철영*";
				}
				else {
					s_ide_userid = decUserId;
					s_ide_usernm = decPw;
				}
				logger.debug("[o-o][drm][b_is_ide_login: {}][reqUserId: {}][reqPssoPw: {}][s_ide_userid: {}][s_ide_usernm: {}]", b_is_ide_login, decUserId, decPw, s_ide_userid, s_ide_usernm);
			}
			if ((s_ide_userid.length() > 0) && (s_ide_usernm.length() > 0)) {
				session.setAttribute("mbrId", s_ide_userid);
				session.setAttribute("mbrNm", s_ide_usernm);
				mv.setViewName("forward:/login/login_success.do");
				return mv;
			}
			//--[tag:adpt][drm][190402][add][ide login][ing] }
		}
		--*/
		
		//-- [i][LAMP로그기록] {
		String group = "loginout";
		String ifname = "login";
		String userId = decUserId;
		String transactionId = UUID.randomUUID().toString();
		String s_log = "[login-req]";
		//-- [log:prepare]
		ApiLinkLogUtil logUtil = new ApiLinkLogUtil(request, group, ifname, userId, transactionId);
		//-- [log:request]
		logUtil.procLogstandard(ApiLinkLogUtil.DEF_LOG_REQ, s_log);

		//-- [i][set for LOG_RES]
		session.setAttribute("lamplog_transactionId", transactionId);
		//-- [i][LAMP로그기록] }

		/*
		SimpleCaptcha captcha = SimpleCaptcha.load(request, "springFormCaptcha");
        boolean isHuman = captcha.validate(captchaCode.toUpperCase());

        if (isHuman) {
            if (session == null) {
                session = request.getSession(true);
            }
            session.setAttribute("authCheck", true);
        } else {
            session.setAttribute("authCheck", false);
			redirectAttributes.addFlashAttribute("returnCode", 10);
		}
		*/
		boolean authCheck = (boolean) request.getSession().getAttribute("authCheck");

		if(!authCheck) {
			// 인증 미처리시
			redirectAttributes.addFlashAttribute("authCheck"	 , !authCheck);
			redirectAttributes.addFlashAttribute("loginVo_userId", decUserId);
			mv.setViewName("redirect:/login/loginForm.do");
		}else {
			// PSSO민식
	//		EgovMap resultMap = jsonInfoUtil.readJsonFromUrl(pssoCheckUrl+"?ClientKey="+pssoclientkey+"&EncPSSOID="+CommonFunc.safeDbEncrypt(param.getUserId())+"&EncPSSOPW="+CommonFunc.safeDbPassEncrypt(param.getPssoPw()));
	//--##	EgovMap resultMap = jsonInfoUtil.readJsonFromUrl(pssoCheckUrl+"?ClientKey="+pssoclientkey+"&EncPSSOID="+URLEncoder.encode(CommonFunc.safeDbEncrypt(param.getUserId()),"EUC-KR")+"&EncPSSOPW="+URLEncoder.encode(CommonFunc.safeDbEncrypt(param.getPssoPw()),"EUC-KR"));
			Map<String, Object> resultMap = jsonInfoUtil.readJsonFromUrl(pssoCheckUrl+"?ClientKey="+pssoclientkey+"&EncPSSOID="+URLEncoder.encode(CommonFunc.safeDbEncrypt(decUserId),"EUC-KR")+"&EncPSSOPW="+URLEncoder.encode(CommonFunc.safeDbEncrypt(decPw),"EUC-KR"));
			String jsonText = resultMap.containsKey("jsonText") ? (String)resultMap.get("jsonText") : "";
			logger.debug("jsonText AI_URL : {}", jsonText);

			ObjectMapper mapper = new ObjectMapper();
			HashMap<String, String> jsonMap = mapper.readValue(jsonText, new TypeReference<HashMap<String, String>>() {});

			String returnCode = jsonMap.containsKey("ReturnCode") ? jsonMap.get("ReturnCode") : "";
			String reqUserNm = jsonMap.containsKey("ReturnName") ? jsonMap.get("ReturnName") : "";
			logger.debug("PSSO 연동확인 $$$ : {}", returnCode);

			// 성공코드가 존재하고 그 값이 11일 경우
			boolean pssoCheck = !"".equals(returnCode) && pssoSuccess.equals(returnCode);
			if (pssoCheck && !"".equals(decUserId)) {
				if (isUsing(CommonFunc.safeDbEncrypt(decUserId))) {
					logger.debug("중복 로그인 : {}");
					mv.setViewName("forward:/login/dupChkYn.do");
				} else {
					logger.debug("중복 로그인 아님 : {}");
					//--###mv.setViewName("forward:/login/loginSuccess.do");
					session.setAttribute("mbrId", decUserId);
					session.setAttribute("mbrNm", CommonFunc.safeDbDecrypt(reqUserNm));
					mv.setViewName("forward:/login/login_success.do");
				}
			} else {
				logger.debug("psso 로그인 실패 : {}", returnCode);

				// psso 실패시 처리
				redirectAttributes.addFlashAttribute("returnCode", returnCode);
				redirectAttributes.addFlashAttribute("loginVo_userId", decUserId);
				mv.setViewName("redirect:/login/loginForm.do");
			}
		}
		return mv;
	}

	/**
	 *
	* <pre>
	* 1. 메소드명 : login_success
	* 2. 작성일 : 2017. 11. 30. 오후 1:52:48
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : LOGIN 성공
	* </pre>
	* @param request
	* @param session
	* @param redirectAttributes
	* @param response
	* @param sso
	* @param userJoinVo
	* @param authVo
	* @return
	* @throws Exception
	 */
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/login_success.do")
	public ModelAndView login_success(HttpServletRequest request, HttpSession session, RedirectAttributes redirectAttributes,  HttpServletResponse response,
			@RequestParam(defaultValue = "", value = "SSO") String sso, UserJoinVO userJoinVo, AuthVO authVo,
			@RequestParam(value = "userId", defaultValue = "") String reqUserId,
			@RequestParam(value = "mbrNm", defaultValue = "") String reqUserNm) throws Exception {
		logger.debug("request : {}", request);

		/* RSA 복호화
		 * Program By CYD - 2022.07.20
		 *///////////////////////////////////////////////////
		String captchaCode = request.getParameter("captchaCode");
		String reqPssoPw   = request.getParameter("pssoPw");
		String decUserId   = "";
		String decPw 	   = "";

		logger.debug("RSA 암호화된 아이디 $$$ : {}", reqUserId);
		logger.debug("RSA 암호화된 비번 $$$ : {}"  , reqPssoPw);
		try {
			decUserId = rsaCall.webDecrypt(reqUserId);
			logger.debug("RSA 복호화된 아이디 $$$ : {}", decUserId);
			// RSA로 암호화된 비밀번호를 복호화
			decPw = rsaCall.webDecrypt(reqPssoPw);
			logger.debug("RSA 복호화된 비번 $$$ : {}", decPw);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			logger.debug("RSA 복호화 오류 $$$ : {}", e.getMessage());
			e.printStackTrace();
		}
		/////////////////////////////////////////////////////

		String cookieIdEnc = "";
		String cookieNmEnc = "";
		logger.debug("equalsIgnoreCase test: {}", pssoApiLoginMethod);
		if (true == "iframe".equalsIgnoreCase(pssoApiLoginMethod)) {
			logger.debug("request test: {}", request.getParameter("beforeUrl"));
			//psso 연동 확인
			logger.debug("sso : {}", sso);
			if(sso.equals("f")) {
				logger.debug("sso login fail: {}", sso);
				return (ModelAndView) new ModelAndView("redirect:/login/loginForm.do?flag=f");
			}

			// 쿠키 정보 가져오기
			Cookie[] cookies = request.getCookies();

			String sessionKey = "";
			for (int i = 0; i < cookies.length; i++) {
				Cookie c = cookies[i];
				// 저장된 쿠키 이름을 가져온다.
				String cName = URLDecoder.decode(c.getName(), "UTF-8");

				// 쿠키 값을 가져온다.
				String cValue = URLDecoder.decode(c.getValue(), "UTF-8");
				logger.debug("PSSO NAME AND CVALE AND DECODE : {}", cName, cValue);

				if(cName.equals("PSSO_enckey")){
					sessionKey = cValue;
				}

				if(cName.equals("PSSO_NEW_UserName")){
					cookieNmEnc = cValue;
				}

				if(cName.equals("PSSO_NEW_UserID")){
					cookieIdEnc = cValue;
				}
			}
			logger.debug("cookieNmEnc : {}", cookieNmEnc);
			logger.debug("cookieIdEnc : {}", cookieIdEnc);
		}
		else {
			//-- login userid
			cookieIdEnc = CommonFunc.safeDbEncrypt(decUserId);
			cookieNmEnc = "";
		}

		UserJoinVO userVO = new UserJoinVO();
		userVO.setMbrNm(cookieNmEnc);
		userVO.setMbrId(cookieIdEnc);

		logger.debug("mbrNm1 : {}", session.getAttribute("mbrNm"));
		logger.debug("mbrid2 : {}", session.getAttribute("mbrId"));

		/**
		 ** psso id와 일치된 openApi 회원 가입 여부 확인
		 **/
		List<UserJoinVO> checkList = service.getUserIdChk(userVO);
		logger.debug("checkList : {}", checkList);

		/**
		 ** 회원 가입 페이지로 이동
		 **/
		if(checkList != null  && checkList.isEmpty()) {
			String userJoinUrl = "/apidev/userJoin/userJoinForm.do";
			RedirectView rv = new RedirectView(userJoinUrl);
			ModelAndView mav = new ModelAndView(rv);
			mav.addObject("mbrNm", session.getAttribute("mbrNm"));
			mav.addObject("mbrId", session.getAttribute("mbrId"));

			return mav;
		}
			/**
			 ** 최종 로그인 일시 수정
			 **/
			service.updateLDate(userVO);

		/**
		 *
		 **/
	    userJoinVo.setMbrId(cookieIdEnc);
	    userJoinVo.setUseYn(Constant.USE_YN_YES);
	    authVo.setMbrId(cookieIdEnc);
	    authVo.setUseYn(Constant.USE_YN_YES);
	    authVo.setUserUseYn(Constant.USER_USE_YN_YES);

		UserJoinVO userJVo = service.selUserInfo(userJoinVo);
		List<AuthVO> authJlist = service.selAuthList(authVo);
		userJVo.setAuthList(authJlist);

		userJVo.setEnCmbrId(cookieIdEnc);

		/*
		 * API Link(Studio) Gateway Observer 권한 설정
		 * CYD - 2020.07.08
		 *//////////////////////////////////////////////////////////////////////////////////////
		//-- [tag:PRJ-20220901][i]apisystem.sysid.observer -> apisystem.observer.autid 로 변경 // [i]시스템간 ID번호변경으로 autnm추가
		String szApisystemObserverAutid = KsmUtil.fnSafeStr(apisystemObserverAutid); 
		String szApisystemObserverAutnm = KsmUtil.fnSafeStr(apisystemObserverAutnm);
		//--## [마이그레이션] EgovProperties 제거 완료
		//--##logger.debug("AdminLevel:" + szApisystemObserverAutid);

		for(AuthVO subAuthVo : userJVo.getAuthList()) {
			logger.debug("= subAuthVo [sysId: {}][autId: {}][autNm: {}]", subAuthVo.getSysId(), subAuthVo.getAutId(), subAuthVo.getAutNm());
			userJVo.setObserverYn("N");
			if ((szApisystemObserverAutid.equalsIgnoreCase(subAuthVo.getAutId()) == true) || (szApisystemObserverAutnm.equalsIgnoreCase(subAuthVo.getAutNm()) == true)) {
				userJVo.setObserverYn("Y");
				break;
			}
		}
		////////////////////////////////////////////////////////////////////////////////////////

		/**
		 * 암호화 된 데이터 복호화 처리
		 * */
		userJVo.setMbrId(CommonFunc.safeDbDecrypt(userJVo.getMbrId()));
		userJVo.setMbrNm(CommonFunc.safeDbDecrypt(userJVo.getMbrNm()));
		userJVo.setCmpnNm(CommonFunc.safeDbDecrypt(userJVo.getCmpnNm()));
		userJVo.setTelNo(CommonFunc.safeDbDecrypt(userJVo.getTelNo()));
		userJVo.setEmail(CommonFunc.safeDbDecrypt(userJVo.getEmail()));

		if (!"".equals(userJVo.getMbrId()) && !"".equals(userJVo.getMbrNm())) {
			//session.setMaxInactiveInterval(60*120);
			session.setMaxInactiveInterval(7200);
			session.setAttribute("ssUserVo", userJVo);
			setSession(session, userJVo.getMbrId());

			logger.debug("MbrId : {}", userJVo.getMbrId(),"MbfNm: {}", userJVo.getMbrNm());
		}

		/**
		 ** 페이지로 이동
		 **/
		ModelAndView mav = new ModelAndView();
		String url = KsmUtil.fnSafeStr(request.getParameter("returnUrl"));
		url = (url.startsWith("/") ? url : "");
		url = (url.startsWith("/apidev") ? url.substring(7, url.length()) : url);

		/*-- [dep]
		String beforeUrl = StringUtil.isNullToString(request.getParameter("beforeUrl"));
		String nowUrl = request.getRequestURL().toString();
		String hosturl = nowUrl.replace(request.getRequestURI(),"");
		String url = beforeUrl;
		url = url.replace(hosturl,"");
		url = url.replaceAll(request.getServletContext().toString(), "");
		logger.debug("returnUrl : {}, hosturl  : {}, final url  : {}", returnUrl, hosturl, url);
		--*/

		String joinUrl = "/userJoin/sJoinInfo.do";
		if ("".equals(url) || joinUrl.equals(url) ) {
			mav.setViewName("redirect:/mypage/mypageInfo.do");	//-- 회원 가입 완료나 이전 url이 없을 경우
		}
		else {
			mav.setViewName("redirect:" + url);
		}
		return mav;
	}

	/**
	* <pre>
	* 1. 메소드명 : pssoLogout
	* 2. 작성일 : 2018. 1. 11. 오후 4:09:21
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : PSSO LOGOUT
	* </pre>
	* @param request
	* @param paramMap
	* @param model
	* @param response
	* @return
	* @throws Throwable
	*/
	/*@RequestMapping("/pssoLogout.do")
	public ModelAndView pssoLogout(HttpServletRequest request, @RequestParam Map<String, Object> paramMap, ModelMap model,  HttpServletResponse response)
			throws Throwable {

		    logger.debug("====================psso logout Controller: {}====================");
		    //mav.setViewName("forward:"+pssoLogoutHost);
		    RedirectView rv = new RedirectView(pssoLogoutHost);
			ModelAndView mav = new ModelAndView(rv);

		return mav;
	}*/

	/**
	* <pre>
	* 1. 메소드명 : logout
	* 2. 작성일 : 2018. 1. 11. 오후 4:08:58
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : LOGOUT
	* </pre>
	* @param request
	* @param paramMap
	* @param model
	* @param response
	* @return
	 * @throws Exception
	* @throws Throwable
	*/
	@RequestMapping("/pssoLogout.do")
	//public ModelAndView logout(HttpServletRequest request, @RequestParam Map<String, Object> paramMap, ModelMap model,  HttpServletResponse response)throws Throwable {
	public ModelAndView logout(ModelAndView mv, HttpServletRequest request) throws Exception {

		/*logger.debug("====================logout Controller: {}====================");
		UserJoinVO userVO = (UserJoinVO)request.getSession().getAttribute("ssUserVo");
		if(userVO != null) {
			removeSession(userVO.getMbrId());
		}
		request.getSession().invalidate();
        String logoutSUrl = "/apidev/main/index.do";
		RedirectView rv = new RedirectView(logoutSUrl);
		ModelAndView mav = new ModelAndView(rv);

		return mav;*/
		logger.debug("==================== LoginController logout : {}====================");

		logout(request);
		mv.setViewName("redirect:/login/loginForm.do");
		return mv;
	}
	/**
	* <pre>

	* 1. 메소드명 : logout
	* 2. 작성일 : 2018. 8. 27. 오후 2:00:45
	* 3. 작성자 : jj
	* 4. 설명 : 로그아웃

	* </pre>
	* @param request
	 * @throws Exception
	* @throws ApiException
	*/
	public void logout(HttpServletRequest request) throws Exception {
		UserJoinVO userVO = (UserJoinVO)request.getSession().getAttribute("ssUserVo");

		logger.debug("로그아웃 실행");

		String userId = (userVO != null) ? userVO.getMbrId() : null;
		String transactionId = UUID.randomUUID().toString();
		ApilinkLogUtil logUtil;

		if("Y".equals(logcenterRecordMode)) {
			logUtil = new ApilinkLogUtil(request, "loginout", "logout", userId, userId, transactionId,"LOGOUT","ACCESS", "id:"+userId);
			logUtil.procLogstandard(ApilinkLogUtil.DEF_LOG_REQ, "[logout-req]");
		}
		String remoteAddr = request.getHeader("X-FORWARDED-FOR");

		if (remoteAddr == null || "".equals(remoteAddr)) {
			remoteAddr = request.getRemoteAddr();
		}

		if(userVO != null) {
			logger.debug("로그아웃 : ID {} / IP {}",userVO.getMbrId(),CommonFunc.safeDbEncrypt(remoteAddr));
			removeSession(userVO.getMbrId());
		}

		//[LAMP로그기록 - RES]
		if("Y".equals(logcenterRecordMode)) {
			logUtil = new ApilinkLogUtil(request, "loginout", "logout", userId, userId, transactionId,"LOGOUT","ACCESS", "id:"+userId);
			logUtil.procLogstandard(ApilinkLogUtil.DEF_LOG_RES, "[logout-res]");
		}
		request.getSession().invalidate();
	}
	//-- [tag:login][drm][add][181027][from apilink]
	/**
	* <pre>

	* 1. 메소드명 : dupChkYnView
	* 2. 작성일 : 2018. 7. 25. 오후 2:28:15
	* 3. 작성자 : jj
	* 4. 설명 : 중복로그인시 이동

	* </pre>
	* @param request
	* @param response
	* @param locale
	* @param model
	* @param session
	* @return
	* @throws Exception
	*/
	@RequestMapping(value = "/dupChkYn.do")
	public ModelAndView dupChkYnView() throws Exception {
		ModelAndView mav = new ModelAndView();
		logger.debug("Start Login Page");
		mav.setViewName("popup/login/sdup/sessionDup");
		return mav;
	}


	//신규 로그인 체크기능
	@ResponseBody
	@RequestMapping(value = "/newLoginCheck.do")
	public ModelAndView newLoginCheck(ModelAndView mv, HttpServletRequest request, HttpSession session,
		RedirectAttributes redirectAttributes, LoginVO loginVo) throws Exception {

		logger.debug("newLoginCheck 실행");

		String reqUserId = loginVo.getUserId();
		String reqPssoPw = loginVo.getPssoPw();
		String captchaCode = loginVo.getCaptchaCode();
		String certifyCode = loginVo.getCertifyCode();
		String decUserId = "";
		String decUserNm = "";
		String decPw 	 = "";
		String pssoReturnCode = "";
		String errorMsg = "";

		mv.setViewName("jsonView");

		mv.addObject("returnCode","0"); //기본적으로 실패로 셋팅 (로그인 성공 시 변경)

		logger.debug("RSA 암호화된 아이디 $$$ : {}", reqUserId);
		logger.debug("RSA 암호화된 비번 $$$ : {}"  , reqPssoPw);
		try {
			decUserId = rsaCall.webDecrypt(reqUserId);
			logger.debug("RSA 복호화된 아이디 $$$ : {}", decUserId);
			// RSA로 암호화된 비밀번호를 복호화
			decPw = rsaCall.webDecrypt(reqPssoPw);
			logger.debug("RSA 복호화된 비번 $$$ : {}", decPw);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			logger.debug("RSA 복호화 오류 $$$ : {}", e.getMessage());
			e.printStackTrace();
		}
		
		//-- [tag:SR-20231018]
		boolean b_is_proc_captcha = true;
		boolean b_is_proc_otp = true;
		boolean b_is_proc_psso = true;

		//-- [tag:SR-20231018][ide login] {
		if ("y".equals(adptranConfigUseLoginIde) == true) {
			boolean b_is_ide_login = ((true == "master".equalsIgnoreCase(certifyCode)) && (true == decUserId.equals(decPw)));
			if (true == b_is_ide_login) {
				decUserNm = "user-%s".formatted(decUserId);
				session.setAttribute("mbrId", decUserId);
				session.setAttribute("mbrNm", decUserNm);

    		b_is_proc_captcha = false;
    		b_is_proc_otp = false;
    		b_is_proc_psso = false;

				logger.debug("[o-o][b_is_ide_login: {}][reqUserId: {}][decUserId: {}][decUserNm: {}]", b_is_ide_login, decUserId, decUserNm);
			}
		}
		//-- [tag:SR-20231018][ide login] }

		//[LAMP로그기록 - REQ]
		String loginTransactionId = UUID.randomUUID().toString();
		ApilinkLogUtil logUtil;

		if("Y".equals(logcenterRecordMode)) {
			logUtil = new ApilinkLogUtil(request, "loginout", "login", decUserId, decUserId, loginTransactionId,"LOGIN","ACCESS", "id:"+decUserId);
			logUtil.procLogstandard(ApilinkLogUtil.DEF_LOG_REQ, "[login-req]");
		}

		if (b_is_proc_captcha) {
//      SimpleCaptcha captcha = SimpleCaptcha.load(request, "springFormCaptcha");
//      boolean isHuman = captcha.validate(captchaCode.toUpperCase());
      
      logger.debug("기존 로그인 해제 및 중복로그인 여부 : "+loginVo.getDupChkYn());
    
      //동일 ID로그인 시도 (기존 사용자 로그아웃 여부 확인) 시 캡챠 미인증(이미 인증 했으므로)
//      if("Y".equals(loginVo.getDupChkYn())){
//      	isHuman = true;
//      }
//    
//      if(!isHuman){
//      	logger.debug("캡챠 입력 오류");
//    		errorMsg = "캡챠 입력 오류입니다.";
//    		mv.addObject("errorDescription",errorMsg);
//    		//[LAMP로그기록 - RES]
//			if("Y".equals(logcenterRecordMode)) {
//				logUtil = new ApilinkLogUtil(request, "loginout", "login", decUserId, decUserId, loginTransactionId,"LOGIN","ACCESS", "id:"+decUserId);
//				//logUtil.procLogstandard(ApilinkLogUtil.DEF_LOG_RES, "[login-res]");
//				logUtil.setResponseCd("E100", "캡챠 입력 오류", "E");
//				logUtil.procLogstandard(ApilinkLogUtil.DEF_LOG_RES, "[login-err]");
//				
//				
//			}
//    		return mv;
//      }
		}	//-- if (b_is_proc_captcha) {
     
    if (b_is_proc_otp) { 
      //휴대폰 인증번호 확인 (동일 ID로그인 시도 (기존 사용자 로그아웃 여부 확인) 시 미실행)
      if (!"Y".equals(loginVo.getDupChkYn())) {
        logger.debug("휴대폰 인증번호 확인 실행");
      
        String sendTarget = (String)session.getAttribute("sendTarget"); //인증확인 전화번호 또는 이메일 정보
        String otpSendType = (String)session.getAttribute("otpSendType"); //인증타입
        String certCheckTransactionId = UUID.randomUUID().toString(); //로그센터 기록용
        String recipientInfo = otpSendType + " : " + sendTarget; //로그센터 기록용 - 인증확인 대상자의 인증타입 + 전화번호 또는 이메일 정보
        String target = sendTarget; //로그센터 기록용 - 인증확인 대상자의 전화번호 또는 이메일 정보
        JsonObject requestBody = new JsonObject();
      
        if("phone".equals(otpSendType)) { //전화번호 인증
         requestBody.addProperty("phone_number", sendTarget);
         requestBody.addProperty("verify", "1");
      
         //로그센터 기록용 - 폰 인증의 경우 target에 전화번호 정보를 치환해서 넣어줘야함
         if(sendTarget.length() ==10) {
           target = sendTarget.substring(0, 3)+"-"+sendTarget.substring(3, 6)+"-"+sendTarget.substring(6);
          }
        	if(sendTarget.length() ==11) {
        		target = sendTarget.substring(0, 3)+"-"+sendTarget.substring(3, 7)+"-"+sendTarget.substring(7);
        	}
    		}else{ //이메일 인증
          requestBody.addProperty("email", sendTarget);
          requestBody.addProperty("verify", "2");
    		}
        logger.info("logcenterRecordMode : {}", logcenterRecordMode);
        if("Y".equals(logcenterRecordMode)) {
          logUtil = new ApilinkLogUtil(request, "certSendCheck", "certCheck", decUserId, target, certCheckTransactionId, otpSendType.toUpperCase(), "AUTH", recipientInfo);
          logUtil.procLogstandard(ApilinkLogUtil.DEF_LOG_REQ, "[certCheck-req]");
        }
      
        requestBody.addProperty("auth_no", certifyCode);
        
        HashMap<String, Object> verifySendResult = new HashMap<String, Object>();
      
        verifySendResult = ShubRestApiCallFunction.shubAnyCommonApiCall(null, requestBody.toString(), shubAuthorizationKey, verifyCheckUrl);
        
        if("Y".equals(logcenterRecordMode)) {
      	  logUtil = new ApilinkLogUtil(request, "certSendCheck", "certCheck", decUserId, target, certCheckTransactionId, otpSendType.toUpperCase(), "AUTH", recipientInfo);
      	  logUtil.procLogstandard(ApilinkLogUtil.DEF_LOG_RES, "[certCheck-res]");
        }
  
        if(!"1".equals(KsmUtil.fnSafeStr(verifySendResult.get("returncode")))) {
        	logger.debug("인증오류");
        
        	if("".equals(KsmUtil.fnSafeStr(verifySendResult.get("errordescription")))) {
        		errorMsg = "휴대폰 인증 오류입니다.";
        	}else {
        		errorMsg = KsmUtil.fnSafeStr(verifySendResult.get("errordescription"));
        	}
          
        	mv.addObject("errorDescription",errorMsg);
        	return mv;
        }
  	  }
    }	//-- if (b_is_proc_otp) { 

		//동일 ID로그인 시도 (기존 사용자 로그아웃 여부 확인) 시 기존 사용자 세션 제거
		if("Y".equals(loginVo.getDupChkYn())){
			removeSession(decUserId);
		}

		HashMap<String, Object> responseCont = new HashMap<String, Object>();
		if (b_is_proc_psso) {
  		//PSSO API 연동을 위해 ID/PW를 aes256 암호화
  		String aseEncId = CommonFunc.aesEncode(decUserId,aesKey);
  		String aseEncPw = CommonFunc.aesEncode(decPw,aesKey);
  
  		HashMap<String, String> contents = new HashMap<>();
  
  		contents.put("EncPSSOID", aseEncId);
  		contents.put("EncPSSOPW", aseEncPw);

  		//로그인을 위해 PSSO 회원인지 체크하는 API실행
  		responseCont = ShubRestApiCallFunction.funcForPsso(contents, memberLoginCheck);
  		pssoReturnCode=KsmUtil.fnSafeStr(responseCont.get("ReturnCode"));
  
  		logger.debug("PSSO 연동결과확인 $$$ : {}", pssoReturnCode);
  
  		boolean pssoCheck = !"".equals(pssoReturnCode) && "11".equals(pssoReturnCode);
  
  		//PSSO 정상 응답
  		if(pssoCheck && !"".equals(decUserId)){
  			//중복로그인인경우
  			if(isUsing(decUserId)) {
  				logger.debug("중복 로그인");
  				errorMsg = "중복 로그인입니다.";
  				mv.addObject("errorType", "dupLogin");
  				mv.addObject("errorDescription",errorMsg);
  				return mv;
  			}
  		//PSSO 실패응답
  		}else{
  			logger.debug("psso 로그인 실패 : {}", pssoReturnCode);
  
  			if("12".equals(pssoReturnCode)) {errorMsg = "아이디/패스워드를 확인해주세요.";}
  			else if("13".equals(pssoReturnCode)) {errorMsg = "아이디/패스워드를 확인해주세요.";}
  			else if("14".equals(pssoReturnCode)) {errorMsg = "아이디/패스워드를 확인해주세요.";}
  			else if("15".equals(pssoReturnCode)) {errorMsg = "아이디/패스워드를 확인해주세요.";}
  			else if("16".equals(pssoReturnCode)) {errorMsg = "아이디/패스워드를 확인해주세요.";}
  			else {errorMsg = "PSSO 연동 오류입니다. \n관리자에게 문의해주세요.";}
  
  			mv.addObject("errorDescription",errorMsg);
  			return mv;
  		}

  		String pssoRtnName = KsmUtil.fnSafeStr(responseCont.get("ReturnName"));
  		decUserNm = "".equals(pssoRtnName) ? "" : CommonFunc.aesDecode(pssoRtnName,aesKey);
		}	//-- if (b_is_proc_psso) {
  
		logger.debug("복호화 한 유저 이름 : "+decUserNm);

		UserJoinVO userVO = new UserJoinVO();
		userVO.setMbrNm(CommonFunc.safeDbEncrypt(decUserNm));
		userVO.setMbrId(CommonFunc.safeDbEncrypt(decUserId));


		//apimanager DB에 회원ID가 존재하는지 체크
		List<UserJoinVO> checkList = service.getUserIdChk(userVO);

		logger.debug("checkList 확인: {}", checkList);

		//PSSO 회원체크는 정상읍답인데 apimanager DB에 회원정보가 없는경우
		if (checkList != null  && checkList.isEmpty()) {
			logger.debug("회원 가입 페이지로 이동");

			String pssoRtnPhone = KsmUtil.fnSafeStr(responseCont.get("ReturnMobile"));
			String pssoRtnEmail = KsmUtil.fnSafeStr(responseCont.get("ReturnOtherm"));

			String decRtnPhone = "".equals(pssoRtnPhone) ? "" : CommonFunc.aesDecode(pssoRtnPhone,aesKey);
			String decRtnEmail = "".equals(pssoRtnEmail) ? "" : CommonFunc.aesDecode(pssoRtnEmail,aesKey);

			mv.addObject("errorType", "newMember");

			request.getSession().setAttribute("memberName",decUserNm);
			request.getSession().setAttribute("memberId",decUserId);
			request.getSession().setAttribute("memberPhone",decRtnPhone);
			request.getSession().setAttribute("memberEmail",decRtnEmail);

			return mv;
		}

		//psso회원정보 확인 및 apimanager에도 등록된 회원일 경우 최종 로그인 일시 수정
		service.updateLDate(userVO);

		UserJoinVO userJoinVo = new UserJoinVO();
		userJoinVo.setMbrId(CommonFunc.safeDbEncrypt(decUserId));
    userJoinVo.setUseYn(Constant.USE_YN_YES);

    AuthVO authVo = new AuthVO();
    authVo.setMbrId(CommonFunc.safeDbEncrypt(decUserId));
    authVo.setUseYn(Constant.USE_YN_YES);
    authVo.setUserUseYn(Constant.USER_USE_YN_YES);

	    //db에 저장된 회원정보(권한정보 포함)를 vo에 담는다
		UserJoinVO userJVo = service.selUserInfo(userJoinVo);

		List<AuthVO> authJlist = service.selAuthList(authVo);

		for(AuthVO userAuthInfo : authJlist) {
			logger.debug("보유권한 : {}", userAuthInfo.getAutNm());
		}

		userJVo.setAuthList(authJlist);
		userJVo.setEnCmbrId(CommonFunc.safeDbEncrypt(decUserId));

		// APILink G/W 운영자 그룹ID, 그룹명
		String szApisystemObserverAutid = KsmUtil.fnSafeStr(apisystemObserverAutid);
		String szApisystemObserverAutnm = KsmUtil.fnSafeStr(apisystemObserverAutnm);

		for(AuthVO subAuthVo : userJVo.getAuthList()) {
			logger.debug("= subAuthVo [sysId: {}][autId: {}][autNm: {}]", subAuthVo.getSysId(), subAuthVo.getAutId(), subAuthVo.getAutNm());
			userJVo.setObserverYn("N");
			// APILink G/W 운영자 권한이 있으면
			if ((szApisystemObserverAutid.equalsIgnoreCase(subAuthVo.getAutId()) == true) || (szApisystemObserverAutnm.equalsIgnoreCase(subAuthVo.getAutNm()) == true)) {
				userJVo.setObserverYn("Y");
				break;
			}
		}

		userJVo.setMbrId(decUserId);
		userJVo.setMbrNm(CommonFunc.safeDbDecrypt(userJVo.getMbrNm()));
		userJVo.setCmpnNm(CommonFunc.safeDbDecrypt(userJVo.getCmpnNm()));
		userJVo.setTelNo(CommonFunc.safeDbDecrypt(userJVo.getTelNo()));
		userJVo.setEmail(CommonFunc.safeDbDecrypt(userJVo.getEmail()));
		userJVo.setMaskingMbrId(CommonFunc.strMasking(decUserId, "id"));
		

		if (!"".equals(userJVo.getMbrId()) && !"".equals(userJVo.getMbrNm())) {

			session.setMaxInactiveInterval(7200);
			session.setAttribute("ssUserVo", userJVo);

			setSession(session, userJVo.getMbrId()); //userJVo에는 회원의 개인정보 및 권한정보가 들어가있다.

			mv.addObject("returnCode","1"); //로그인 정상 통과 시 returnCode를 1로 변경

			//로그인 체크 통과 시 otp 인증을 위해 session에 저장된 정보 삭제
			session.removeAttribute("sendTarget");
			session.removeAttribute("otpSendType");

			//[LAMP로그기록 - RES]
			if("Y".equals(logcenterRecordMode)) {
				logUtil = new ApilinkLogUtil(request, "loginout", "login", decUserId, decUserId, loginTransactionId,"LOGIN","ACCESS", "id:"+decUserId);
				logUtil.procLogstandard(ApilinkLogUtil.DEF_LOG_RES, "[login-res]");
			}
		}

		return mv;
	}

	@RequestMapping(value = "/otpSend.do")
	@ResponseBody
	public ModelAndView otpSend(ModelAndView mv, HttpServletRequest request, LoginVO param, ModelMap model,HttpSession session) throws Exception {

		logger.debug("otpSend 실행");

		session.removeAttribute("sendTarget");
		session.removeAttribute("otpSendType");

		String returnCode = "0";

		mv.setViewName("jsonView");

		String reqUserId   = param.getUserId();
		String reqPssoPw   = param.getPssoPw();
		String decUserId   = "";
		String decPw 	   = "";

		logger.debug("RSA 암호화된 아이디 $$$ : {}", reqUserId);
		logger.debug("RSA 암호화된 비번 $$$ : {}"  , reqPssoPw);
		try {
			decUserId = rsaCall.webDecrypt(reqUserId);
			logger.debug("RSA 복호화된 아이디 $$$ : {}", decUserId);
			// RSA로 암호화된 비밀번호를 복호화
			decPw = rsaCall.webDecrypt(reqPssoPw);
			logger.debug("RSA 복호화된 비번 $$$ : {}", decPw);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			logger.debug("RSA 복호화 오류 $$$ : {}", e.getMessage());
			e.printStackTrace();
		}

		String otpSendType = "";
		String transactionId = UUID.randomUUID().toString(); //로그센터 기록용
		String recipientInfo = ""; //로그센터 기록용 - 인증방법 + 인증시도자 정보(전화번호 또는 이메일)
		String target = ""; //로그센터 기록용 - 수신자 전화번호 도는 이메일 정보

		ApilinkLogUtil logUtil; //로그센터 기록용

		HashMap<String, String> contents = new HashMap<>();
		HashMap<String, Object> responseCont = new HashMap<String, Object>();


		String aseEncId = CommonFunc.aesEncode(decUserId,aesKey);
		String aseEncPw = CommonFunc.aesEncode(decPw,aesKey);
		contents.put("EncPSSOID", aseEncId);
		contents.put("EncPSSOPW", aseEncPw);
		responseCont = ShubRestApiCallFunction.funcForPsso(contents, memberLoginCheck);

		//psso 회원 체크 성공 시
       	if("11".equals(KsmUtil.fnSafeStr(responseCont.get("ReturnCode")))) {

       	   logger.info("PSSO 이메일 복호화 전 : "+(String)responseCont.get("ReturnOtherm"));

    	   String phoneNo = KsmUtil.fnSafeStr(CommonFunc.aesDecode((String)responseCont.get("ReturnMobile"), aesKey));
    	   String email = KsmUtil.fnSafeStr(CommonFunc.aesDecode((String)responseCont.get("ReturnOtherm"), aesKey));

    	   logger.info("PSSO 전화번호 : "+phoneNo);
    	   logger.info("PSSO email : "+email);

	       JsonObject requestBody = new JsonObject();

	       otpSendType = !"".equals(phoneNo) ? "phone" : "email";

	       if(!"".equals(phoneNo)) {
		       requestBody.addProperty("phone_number", phoneNo);
		       requestBody.addProperty("verify", "1");
		       recipientInfo = "phoneNo : "+ phoneNo;

			   	if(phoneNo.length() ==10) {
					target=phoneNo.substring(0, 3)+"-"+phoneNo.substring(3, 6)+"-"+phoneNo.substring(6);
				}
				if(phoneNo.length() ==11) {
					target=phoneNo.substring(0, 3)+"-"+phoneNo.substring(3, 7)+"-"+phoneNo.substring(7);
				}

	       }else if(!"".equals(email)){
		       requestBody.addProperty("email", email);
		       requestBody.addProperty("verify", "2");
		       recipientInfo = "email : "+ email;
		       target = email;
	       }else {
		       mv.addObject("returnCode", returnCode);
			   mv.addObject("failMsg", "전화번호/이메일 정보가 존재하지 않습니다.");
			   return mv;
	       }

	       if("Y".equals(logcenterRecordMode)) {
		       logUtil = new ApilinkLogUtil(request, "certSendCheck", "certSend", decUserId, target, transactionId, otpSendType.toUpperCase(), "AUTH", recipientInfo);
			   logUtil.procLogstandard(ApilinkLogUtil.DEF_LOG_REQ, "[certSend-req]");
	       }

	       HashMap<String, Object> verifySendResult = new HashMap<String, Object>();

	       verifySendResult =  ShubRestApiCallFunction.shubAnyCommonApiCall(null, requestBody.toString(), shubAuthorizationKey, verifySendUrl);
	       returnCode = KsmUtil.fnSafeStr(verifySendResult.get("returncode"));

	       if("1".equals(returnCode)) {
	    	   request.getSession().setAttribute("otpSendType",otpSendType);

	    	   if("phone".equals(otpSendType)) {
	    		   request.getSession().setAttribute("sendTarget", phoneNo);
	    		   mv.addObject("type", "phone");
	    	   }else {
	    		   request.getSession().setAttribute("sendTarget",email);
	    		   mv.addObject("type", "email");
	    	   }
	       }

	       if("Y".equals(logcenterRecordMode)) {
	    	   logUtil = new ApilinkLogUtil(request, "certSendCheck", "certSend",decUserId ,target, transactionId, otpSendType.toUpperCase(), "AUTH", recipientInfo);
	    	   logUtil.procLogstandard(ApilinkLogUtil.DEF_LOG_RES, "[certSend-res]");
	       }

	       mv.addObject("returnCode", returnCode);
		   mv.addObject("failMsg", verifySendResult.get("errordescription"));

	     }else {
	 		mv.addObject("returnCode", returnCode);
	 		mv.addObject("failMsg", "아이디/비밀번호/계정상태를 확인해주세요.");
       	 }

		return mv;

	}
}
