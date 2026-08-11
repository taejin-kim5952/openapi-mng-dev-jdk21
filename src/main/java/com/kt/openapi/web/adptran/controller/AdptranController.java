package com.kt.openapi.web.adptran.controller;

import com.kt.openapi.web.adptran.util.AdptranUtil;
import com.kt.openapi.web.adptran.util.KsmUtil;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Enumeration;

@Controller
@RequestMapping(value="/adptran")
public class AdptranController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdptranController.class);

	@Autowired
	private Environment environment;

	// /devHome사용 check key
	@Value("${dev.use.devhome.key}")
	private String devUseDevHomeKey;

	//-- [tag:PRJ-20220901]
	@Value("${config.use.login.ide}")
	private String adptranConfigUseLoginIde;

	//-- for apistatus {
	@RequestMapping(value = {
		"/apistatus/group", "/apistatus/list",
	})
	public ModelAndView adptran_apistatus(HttpServletRequest request) throws Exception {
		LOGGER.debug("\n\n### {}.{}() [request.getServletPath(): {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), request.getServletPath());

		ModelAndView mv = new ModelAndView();
		mv.setViewName("adptran/vue_page_mount_apistatus");
		return mv;
	}
	//-- for apistatus }

	@RequestMapping(value = "/devHome")
	public ModelAndView devHome(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "homekey", defaultValue = "") String reqHomekey,
			@RequestParam(value = "sskey", defaultValue = "") String reqSsKey,
			@RequestParam(value = "ssval", defaultValue = "") String reqSsVal,
			@RequestParam(value = "overwrite", defaultValue = "") String reqOverwrite) throws Exception {
		ModelAndView mv = new ModelAndView();
		//-- [tag:PRJ-20220901]
		//-- [i][개발시에만동작]
		if (false == "y".equalsIgnoreCase(adptranConfigUseLoginIde)) {
			mv.setViewName("redirect:/");
			return mv;
		}

		String homeKey = ((0 == reqHomekey.length()) ? KsmUtil.fnSafeStr(session.getAttribute("dev.use.devhome.key")) : reqHomekey);

		String keycheckmode = "";
		String md5_reqHomekey = KsmUtil.md5(homeKey);
		if (true == this.devUseDevHomeKey.equalsIgnoreCase(md5_reqHomekey)) {
			session.setAttribute("dev.use.devhome.key", homeKey);
		}
		else {
			keycheckmode = "y";
		}
		session.setAttribute("pass.devhome.key", ((keycheckmode.length() == 0) ? "y" : ""));
		LOGGER.debug("\n\n### {}.{}() [homekey: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), homeKey);

		//-- [JSP->Thymeleaf 마이그레이션] devHome.jsp가 <c:set>/<c:choose>로 직접 하던
		//-- dev session 설정처리(sskey/ssval/overwrite 쿼리파라미터 기반)를 컨트롤러로 이관.
		if (reqSsKey.length() > 0) {
			boolean bIsPermitKey = ("dev.use.devhome.key".equals(reqSsKey) || "dev.use.js.console.log".equals(reqSsKey)
					|| "dev.use.webpack.dev.server".equals(reqSsKey) || "config.runmode".equals(reqSsKey)
					|| "dev.master.id".equals(reqSsKey) || "kos.apipath.prefix".equals(reqSsKey));
			if (bIsPermitKey) {
				if (reqSsVal.length() == 0) {
					session.removeAttribute(reqSsKey);
				}
				else {
					session.setAttribute(reqSsKey, reqSsVal);
					if ("config.runmode".equals(reqSsKey)) {
						AdptranUtil.getInstance().setConfigRunmode(reqSsVal);
					}
				}
			}
			else if ("dev.runmode.tag".equals(reqSsKey)) {
				if (reqSsVal.length() == 0) {
					session.removeAttribute(reqSsKey);
				}
				else if ("y".equals(reqOverwrite)) {
					session.setAttribute(reqSsKey, reqSsVal);
				}
				else {
					String sAttr = KsmUtil.fnSafeStr(session.getAttribute(reqSsKey));
					if (!((";" + sAttr + ";").contains(";" + reqSsVal + ";"))) {
						String newVal = ((sAttr.length() > 0) ? (sAttr + ";" + reqSsVal) : reqSsVal);
						session.setAttribute(reqSsKey, newVal);
					}
				}
			}
		}

		//-- [i] 세션 전체 덤프(ssUserVo 제외, dev.use.devhome.key는 마스킹) - 원본과 동일하게 raw HTML로 렌더링
		StringBuilder sbHttpSession = new StringBuilder();
		Enumeration<String> attrNames = session.getAttributeNames();
		while (attrNames.hasMoreElements()) {
			String key = attrNames.nextElement();
			Object value = session.getAttribute(key);
			if ("ssUserVo".equals(key) && value instanceof UserJoinVO) {
				continue;
			}
			sbHttpSession.append("<span class=\"txt_key\">").append(key).append("</span>: ");
			sbHttpSession.append("<span class=\"txt_value\">");
			if ("dev.use.devhome.key".equals(key)) {
				sbHttpSession.append("*".repeat(KsmUtil.fnSafeStr(value).length()));
			}
			else {
				sbHttpSession.append(KsmUtil.fnSafeStr(value));
			}
			sbHttpSession.append("</span><br>");
		}

		String attrConfigRunmode = KsmUtil.fnSafeStr(environment.getProperty("config.runmode"));
		String attrConfigJsVersion = KsmUtil.fnSafeStr(environment.getProperty("config.js.version"));
		String attrConfigAdptranApiUrl = KsmUtil.fnSafeStr(environment.getProperty("config.adptran.api.url"));
		String attrDevAdptranApiUrl = KsmUtil.fnSafeStr(environment.getProperty("dev.adptran.api.url"));
		String attrDevUseJsConsoleLog = KsmUtil.fnSafeStr(environment.getProperty("dev.use.js.console.log"));
		String attrDevUseWebpackDevServer = KsmUtil.fnSafeStr(environment.getProperty("dev.use.webpack.dev.server"));
		String attrDevWebpackDevServer = KsmUtil.fnSafeStr(environment.getProperty("dev.webpack.dev.server"));

		String ssConfigRunmode = KsmUtil.fnSafeStr(session.getAttribute("config.runmode"));
		String dpConfigRunmode = ((ssConfigRunmode.length() > 0) ? ssConfigRunmode : attrConfigRunmode);
		boolean bIsRunmodeDev = "dev".equals(dpConfigRunmode);

		String ssDevUseJsConsoleLog = KsmUtil.fnSafeStr(session.getAttribute("dev.use.js.console.log"));
		String dpDevUseJsConsoleLog = ((ssDevUseJsConsoleLog.length() > 0) ? ssDevUseJsConsoleLog : attrDevUseJsConsoleLog);
		String dpUseConsoleObject = ("y".equals(dpDevUseJsConsoleLog) ? "use console log" : "not use console log");

		String ssDevUseWebpackDevServer = KsmUtil.fnSafeStr(session.getAttribute("dev.use.webpack.dev.server"));
		String dpDevUseWebpackDevServer = ((ssDevUseWebpackDevServer.length() > 0) ? ssDevUseWebpackDevServer : attrDevUseWebpackDevServer);
		String dpUseWebpackDevServer = ("y".equals(dpDevUseWebpackDevServer) ? "use webpack-dev-server" : "not use webpack-dev-server");

		String dpAdptranApiUrl = (bIsRunmodeDev ? attrDevAdptranApiUrl : attrConfigAdptranApiUrl);
		String dpDevRunmodeTag = KsmUtil.fnSafeStr(session.getAttribute("dev.runmode.tag"));
		String dpDevMasterId = KsmUtil.fnSafeStr(session.getAttribute("dev.master.id"));
		String dpUserId = AdptranUtil.getServiceLoginInfo(session, "userid");
		String dpUserName = AdptranUtil.getServiceLoginInfo(session, "username");

		String serverInfo = request.getServletContext().getServerInfo();
		int servletMajorVersion = request.getServletContext().getMajorVersion();
		int servletMinorVersion = request.getServletContext().getMinorVersion();
		// [JSP 지원 제거 완료] JSP 엔진이 더 이상 없어 항상 빈 값
		String jspSpecVersion = "";

		mv.addObject("attr_keycheckmode", keycheckmode);
		mv.addObject("dp_httpSession", sbHttpSession.toString());
		mv.addObject("attr_config_runmode", attrConfigRunmode);
		mv.addObject("attr_config_js_version", attrConfigJsVersion);
		mv.addObject("attr_dev_use_js_console_log", attrDevUseJsConsoleLog);
		mv.addObject("attr_dev_use_webpack_dev_server", attrDevUseWebpackDevServer);
		mv.addObject("attr_dev_webpack_dev_server", attrDevWebpackDevServer);
		mv.addObject("dp_config_runmode", dpConfigRunmode);
		mv.addObject("dp_useConsoleObject", dpUseConsoleObject);
		mv.addObject("dp_useWebpackDevServer", dpUseWebpackDevServer);
		mv.addObject("dp_httpSessionId", session.getId());
		mv.addObject("dp_dev_runmode_tag", dpDevRunmodeTag);
		mv.addObject("dp_adptran_api_url", dpAdptranApiUrl);
		mv.addObject("dp_dev_master_id", dpDevMasterId);
		mv.addObject("dp_userId", dpUserId);
		mv.addObject("dp_userName", dpUserName);
		mv.addObject("server_info", serverInfo);
		mv.addObject("servlet_major_version", servletMajorVersion);
		mv.addObject("servlet_minor_version", servletMinorVersion);
		mv.addObject("jsp_spec_version", jspSpecVersion);

		mv.setViewName("adptran/devHome");

		return mv;
	}

	@RequestMapping(value = "/devQuery")
	public ModelAndView devQuery(HttpServletRequest request, HttpSession session) throws Exception {
		LOGGER.debug("\n\n### {}.{}() [request.getServletPath(): {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), request.getServletPath());

		ModelAndView mv = new ModelAndView();
		//-- [tag:PRJ-20220901]
		//-- [i][개발시에만동작]
		if (false == "y".equalsIgnoreCase(adptranConfigUseLoginIde)) {
			mv.setViewName("redirect:/");
			return mv;
		}
		
		boolean b_is_pass_devhome_key = "y".equals(session.getAttribute("pass.devhome.key"));

		if (true == b_is_pass_devhome_key) {
			// [JSP->Thymeleaf 마이그레이션] 예전엔 ":prop_data=\"...\"" 형태의 raw 속성 문자열을
			// JSP에서 그대로 splice했으나, Thymeleaf에서는 값만 넘기고 템플릿에서 속성명을 붙인다.
			mv.addObject("vuePropData", "{ auto_load_schema_list: 'n' }");
			mv.setViewName("adptran/vue_page_mount");
		}
		else {
			mv.setViewName("redirect:/adptran/devHome");
		}
		return mv;
	}
}
