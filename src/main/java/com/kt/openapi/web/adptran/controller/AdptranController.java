package com.kt.openapi.web.adptran.controller;

import com.kt.openapi.web.adptran.util.KsmUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value="/adptran")
public class AdptranController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdptranController.class);

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
	public ModelAndView devHome(HttpSession session, @RequestParam(value = "homekey", defaultValue = "") String reqHomekey) throws Exception {
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

		mv.addObject("attr_keycheckmode", keycheckmode);
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
			mv.addObject("attr_vue_page_properties", ":prop_data=\"{ auto_load_schema_list: 'n' }\"");
			mv.setViewName("adptran/vue_page_mount");
		}
		else {
			mv.setViewName("redirect:/adptran/devHome");
		}
		return mv;
	}
}
