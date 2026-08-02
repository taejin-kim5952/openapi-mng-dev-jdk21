package com.kt.openapi.web.cmmn;

import com.kt.openapi.web.adptran.util.KsmUtil;
import com.kt.openapi.web.login.controller.LoginController;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @deprecated {@link com.kt.openapi.fwk.online.filter.SessionCheckInterceptor} 사용
 */
@Deprecated(forRemoval = true)
public class SessionCheckInterceptor implements HandlerInterceptor {
	
	private static final Logger log = LoggerFactory.getLogger(SessionCheckInterceptor.class);

	private final LoginController loginController;

	//-- [tag:PRJ-20220901]
	@Value("${dev.runmode.tag}")
	private String devRunmodeTag;

	//  인증 체크가 필요 없는 URL 리스트
	List<String> urlList;

	public SessionCheckInterceptor(LoginController loginController) {
		this.loginController = loginController;
	}

	public void setUrlList(List urlList) {
		this.urlList = urlList;
	}
 
	@Override
	@SuppressWarnings("unchecked")
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//System.out.println("======================  Intercepter url is no session check  ======================");
		//log.info("preHandler Start");
		//log.info("Pass urlList>>"  + this.urlList);
		//log.info("Pass urlParam>>" + request.getParameter("pageResult"));
	/*	psso 로그인 안될경우 임시 세션 처리 로직 
		if(request.getSession().getAttribute("ssUserVo") == null) {
			UserJoinVO  apprvVo = new UserJoinVO();
			apprvVo.setMbrId("pssotest12");
			apprvVo.setMbrNm("홍길동");
			apprvVo.setEnCmbrId("0001M7HGVS7AwV401M4R/0xqmg==");
			apprvVo.setCmpnNm("회사명");
			
			//authList
			ArrayList<AuthVO> authList = new ArrayList<AuthVO>();
			AuthVO authInfo = new AuthVO();
			authInfo.setSysId("AUTHENTI");
			authInfo.setSysNm("AUTHENTI");
			authInfo.setAutNm("개발자");
			authInfo.setAutId("66");
			authList.add(authInfo);
			apprvVo.setAuthList(authList);
			
			HttpSession newSession = request.getSession();
			  
			newSession.setMaxInactiveInterval(60*120);
			newSession.setAttribute("ssUserVo", apprvVo);
		}
		return true;*/
		HttpSession session = request.getSession();
		boolean isAjaxCall = Boolean.parseBoolean(request.getHeader("AJAX")); 
		String requestURI = KsmUtil.fnSafeStr(request.getRequestURI());

		//-- [tag:PRJ-20220901]
		//-- [i][Session key dev.runmode.tag 값을 처리
		boolean bProcRunmodeTag = (requestURI.indexOf("/adptran/devHome") == -1);
		if (true == bProcRunmodeTag) { 
			String devRunmodeTagValue = KsmUtil.fnSafeStr(session.getAttribute("dev.runmode.tag"));
			devRunmodeTagValue = ((devRunmodeTagValue.length() > 0) ? devRunmodeTagValue : this.devRunmodeTag);
			session.setAttribute("dev.runmode.tag", devRunmodeTagValue);
			log.info("[dev.runmode.tag=%s][session dev.runmode.tag: %s]".formatted(this.devRunmodeTag, devRunmodeTagValue));
		}
		try {
			//log.info("== URL : {} ",request.getRequestURI());
			// 인증 체크가 필요 없는 URL 체크
			if(urlList != null && urlList.size() != 0){
				for(int i=0; i < urlList.size(); i++){
					String reqURI = request.getRequestURI();
					if (reqURI.matches(urlList.get(i))) {
						//log.info("=== urlList check === urlList.get({}): {}", i, urlList.get(i));
						//log.info("== Pass URL ============================");
						return true;
					}
					else {
						//--##log.info("=== urlList check === urlList.get({}): {}", i, urlList.get(i));
					}
				}

				//--##//--[tag:adpt][drm][add][tmp]
				//--##if (request.getRequestURI().indexOf("/apidev/adptran/") != -1) {
				//--##	log.debug("\n###[o-o][drm][{}.{}()][message: {}][request.getRequestURI(): {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), "login pass", request.getRequestURI());
				//--##	return true;
				//--##}

				// ajax check
				if (true == isAjaxCall) {
					//log.info(request.getRequestURI() + "====================== Ajax  ajaxCall preHandle  ===============================");
					if(request.getSession().getAttribute("ssUserVo") == null) {
						response.sendRedirect("/apidev/main/index.do");
						return false;
					}else {
						UserJoinVO userVO = (UserJoinVO)request.getSession().getAttribute("ssUserVo");
						if (loginController.isUsing(userVO.getMbrId())) {
							//세션에 해당 유저의 정보가 존재 할 경우 만 유효
							return true;
						}else {
							response.sendRedirect("/apidev/main/index.do");
							return false;
						}
					}
				}
			}
			// ssUserVo
			log.info("== ssUserVo : "+ request.getSession().getAttribute("ssUserVo") +" ============================");
			
			if(request.getSession().getAttribute("ssUserVo") == null) {
				response.sendRedirect("/apidev/main/index.do");
				return false;
			}else {
				UserJoinVO userVO = (UserJoinVO)request.getSession().getAttribute("ssUserVo");
				if (loginController.isUsing(userVO.getMbrId())) {
					//세션에 해당 유저의 정보가 존재 할 경우 만 유효
					return true;
				}else {
					response.sendRedirect("/apidev/main/index.do");
					return false;
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
}
