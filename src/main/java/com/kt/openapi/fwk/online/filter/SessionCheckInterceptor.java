package com.kt.openapi.fwk.online.filter;

import com.kt.openapi.web.adptran.util.KsmUtil;
import com.kt.openapi.web.login.controller.LoginController;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 세션 체크 인터셉터
 * [마이그레이션] mngDev의 비즈니스 로직(UserJoinVO 등)을 유지하면서 패키지 구조만 fwk로 현대화
 */
@Component
public class SessionCheckInterceptor implements HandlerInterceptor {
	
	private static final Logger log = LoggerFactory.getLogger(SessionCheckInterceptor.class);

	private final LoginController loginController;

	@Value("${dev.runmode.tag:}")
	private String devRunmodeTag;

	// 인증 체크가 필요 없는 URL 리스트 (WebMvcConfig에서 주입)
	private List<String> urlList;

	public SessionCheckInterceptor(LoginController loginController) {
		this.loginController = loginController;
	}

	public void setUrlList(List<String> urlList) {
		this.urlList = urlList;
	}
 
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession();
		String ajaxHeader = request.getHeader("AJAX");
		boolean isAjaxCall = ajaxHeader != null && Boolean.parseBoolean(ajaxHeader); 
		String requestURI = KsmUtil.fnSafeStr(request.getRequestURI());

		// [tag:PRJ-20220901] 실행 모드 태그 처리
		boolean bProcRunmodeTag = (requestURI.indexOf("/adptran/devHome") == -1);
		if (bProcRunmodeTag) { 
			String devRunmodeTagValue = KsmUtil.fnSafeStr(session.getAttribute("dev.runmode.tag"));
			devRunmodeTagValue = ((devRunmodeTagValue.length() > 0) ? devRunmodeTagValue : this.devRunmodeTag);
			session.setAttribute("dev.runmode.tag", devRunmodeTagValue);
		}

		try {
			// 1. 인증 체크 제외 URL 확인
			if(urlList != null && !urlList.isEmpty()){
				for(String skipUrl : urlList){
					if (requestURI.matches(skipUrl)) {
						return true;
					}
				}
			}

			// 2. 세션 유무 및 사용자 정보 확인 (ssUserVo)
			UserJoinVO userVO = (UserJoinVO) session.getAttribute("ssUserVo");
			
			if(userVO == null) {
				handleUnauthenticated(request, response, isAjaxCall);
				return false;
			} else {
				// 3. 중복 로그인 및 세션 유효성 체크 (LoginController 활용)
				if (loginController.isUsing(userVO.getMbrId())) {
					return true;
				} else {
					handleUnauthenticated(request, response, isAjaxCall);
					return false;
				}
			}
		} catch (Exception e) {
			log.error("SessionCheckInterceptor preHandle Exception: ", e);
			return false;
		}
	}

	/**
	 * 미인증 시 처리 (Ajax 여부에 따른 분기)
	 */
	private void handleUnauthenticated(HttpServletRequest request, HttpServletResponse response, boolean isAjaxCall) throws Exception {
		// mngDev 기존 리다이렉트 경로 유지
		String redirectPath = "/apidev/main/index.do";
		
		if (isAjaxCall) {
			// Ajax 호출 시에는 403 에러를 보내거나 특정 스크립트로 유도 가능 (일단 기존 로직대로 리다이렉트)
			response.sendRedirect(redirectPath);
		} else {
			response.sendRedirect(redirectPath);
		}
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}
}
