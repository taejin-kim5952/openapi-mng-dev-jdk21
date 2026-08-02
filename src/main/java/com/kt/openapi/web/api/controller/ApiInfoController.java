/**
 *  OPEN API version 1.0
 *
 *  Copyright ⓒ 2017 kt corp. All rights reserved.
 *
 *  This is a proprietary software of kt corp, and you may not use this file except in
 *  compliance with license agreement with kt corp. Any redistribution or use of this
 *  software, with or without modification shall be strictly prohibited without prior written
 *  approval of kt corp, and the copyright notice above does not evidence any actual or
 *  intended publication of such software.
 * 
 */
package com.kt.openapi.web.api.controller;

import com.kt.openapi.fwk.online.page.Pagination;
import com.kt.openapi.web.adptran.vo.AdptranTestcaseVO;
import com.kt.openapi.web.api.service.ApiInfoService;
import com.kt.openapi.web.api.service.ApiSearchService;
import com.kt.openapi.web.api.vo.ApiMainSearchVo;
import com.kt.openapi.web.api.vo.ApiMainVo;
import com.kt.openapi.web.cmm.service.CmnService;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;

/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.api.controller
* 2. 타입명 : ApiInfoController.java
* 3. 작성일 : 2017. 12. 19. 오후 4:27:56
* 4. 작성자 : user
* 5. 설명 :Api 규격서
* </pre>
*/
/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.api.controller
* 2. 타입명 : ApiInfoController.java
* 3. 작성일 : 2017. 12. 19. 오후 4:59:15
* 4. 작성자 : user
* 5. 설명 :
* </pre>
*/
@Controller
@RequestMapping(value="/api/info")
public class ApiInfoController {
	
	private static final Logger LOG = LoggerFactory.getLogger(ApiInfoController.class);
	  
	@Autowired
	private ApiInfoService apiInfoService;
	
	@Autowired
	private ApiSearchService apiSearchService;

	@Autowired
	protected CmnService cmnService;
	
	@Value("${pageUnit:10}")
	private int pageUnit;

	@Value("${pageSize:10}")
	private int pageSize;
	

	/**
	* <pre>
	* 1. 메소드명 : mvInfoList
	* 2. 작성일 : 2017. 12. 19. 오후 4:49:56
	* 3. 작성자 : user
	* 4. 설명 : api 규격서 메인 페이지 이동
	* </pre>
	* @param session
	* @param request
	* @param response
	* @param model
	* @return
	* @throws Exception
	*/
	@RequestMapping(value="/mvInfoList.do")
	public ModelAndView mvInfoList(HttpSession session, HttpServletRequest request,HttpServletResponse response,  ModelMap model, ApiMainVo vo) throws Exception {
		
		vo.setTargetCd("TARGET1020");
		
		LOG.debug("#######################  ApiInfoController mvInfoList START ############################");
		ModelAndView mv = new ModelAndView();
		//메뉴 조회
		model.addAttribute("leftMenuList", apiInfoService.selApiMenuList(vo));
		
		mv.setViewName("api/info/list");
		
		return mv;
	}   
	
	/**
	* <pre>
	* 1. 메소드명 : selApiAjax
	* 2. 작성일 : 2017. 12. 19. 오후 4:59:18
	* 3. 작성자 : user
	* 4. 설명 : api 기본정보 조회
	* </pre>
	* @param request
	* @param response
	* @param model
	* @param vo
	* @return
	* @throws Exception
	*/
	@RequestMapping(value="/selApiAjax.do")
	public ModelAndView selApiAjax(HttpServletRequest request,HttpServletResponse response,  ModelMap model , ApiMainVo vo) throws Exception {
		LOG.debug("#######################  ApiInfoController selApiAjax START ############################");
		//api_no 와 api_spc_no 를 인자로 기본정보 조회
		model.addAttribute("defaultApiMap", apiInfoService.selApiDefaultData(vo));

		//--[tag:adpt][add]
		HashMap<String, Object> map_in = new HashMap<>();
		map_in.put("apiNo", vo.getApiNo());
		List<AdptranTestcaseVO> list_out = apiInfoService.select_API_TESTCASE(map_in);
		model.addAttribute("apiTestcaseList", list_out);

		return new ModelAndView( "jsonView", model );
	}
	
	/**
	* <pre>
	* 1. 메소드명 : mvInfoView
	* 2. 작성일 : 2017. 12. 19. 오후 4:49:56
	* 3. 작성자 : user
	* 4. 설명 : api 규격서 상세 페이지 이동
	* </pre>
	* @param session
	* @param request
	* @param response
	* @param model
	* @return
	* @throws Exception
	*/
	/*
	@RequestMapping(value="/mvInfoView.do")
	public ModelAndView mvInfoView(HttpSession session, HttpServletRequest request,HttpServletResponse response,  ModelMap model, ApiMainVo vo) throws Exception {
		
		LOG.debug("#######################  ApiInfoController mvInfoView START ############################");
		ModelAndView mv = new ModelAndView();
		vo.setLeftDept("1");
		vo.setTargetCd("TARGET1020");
		//메뉴 조회
		model.addAttribute("leftMenuList", apiInfoService.selApiMenuList(vo));
		
		mv.setViewName("api/info/view");
		
		return mv;
	}  
	*/
	/**
	 * <pre>
	 * 1. 메소드명 : savApiMenuListAjax
	 * 2. 작성일   : 2017. 12. 20. 오후 3:45:44
	 * 3. 작성자   : JeonGeun Kang
	 * 4. 설명     :
	 * </pre>
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/savApiMenuListAjax.do")
	public ModelAndView savApiMenuListAjax(HttpSession session , HttpServletRequest request,HttpServletResponse response,  ModelMap model , ApiMainVo vo) throws Exception {
		LOG.debug("#######################  ApiRegController savApiMenuListAjax START ############################");
		
		vo.setTargetCd("TARGET1020");
		
		model.addAttribute("menuList", apiInfoService.selApiMenuList(vo));
		
		return new ModelAndView( "jsonView", model );
	}
	
	/**
	* <pre>
	
	* 1. 메소드명 : selApiSearchList
	* 2. 작성일 : 2019. 04. 02. 오전 11:36:49
	* 3. 작성자 : kms
	* 4. 설명 :
	
	* </pre>
	* @param session
	* @param request
	* @param response
	* @param model
	* @param vo
	* @return
	* @throws Exception
	*/
	@ResponseBody
	@RequestMapping(value="/selApiSearchList.do")
	public ModelAndView selApiSearchList(HttpSession session , HttpServletRequest request,HttpServletResponse response,  ModelMap model , ApiMainVo param, ApiMainSearchVo vo) throws Exception {
		LOG.debug("#######################  ApiInfoController selApiSearchList START ############################");
//		param.setTargetCd("TARGET1020");
//		
//		param.setFirstIndex(paginationInfo.getFirstRecordIndex());
//		param.setLastIndex(paginationInfo.getLastRecordIndex());
//		param.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());
//		
//		model.addAttribute("searchApiList", apiInfoService.selApiSearchList(param));
//		
//		int totCnt  = apiInfoService.selApiSearchListCnt(param);
//		paginationInfo.setTotalRecordCount(totCnt);
//		model.addAttribute("paginationInfo", paginationInfo);//페이징 정보
		
//		model.addAttribute("totalCnt", totCnt);
		
//		return new ModelAndView( "jsonView", model );
		
		LOG.info("API INFO CONTROLLER searchYn ::: " + vo.getSearchYn());
		
		vo.setPageUnit(pageUnit); 	// 페이지당 건수
		vo.setPageSize(pageSize);	// 페이지 리스트에 게시되는 건수
		
		/** pageing setting */
		Pagination paginationInfo = new Pagination();
		paginationInfo.setCurrentPageNo(vo.getPageIndex()); // 현재 페이지 인덱스
		paginationInfo.setRecordCountPerPage(vo.getPageUnit());
		paginationInfo.setPageSize(vo.getPageSize());
		
		vo.setFirstIndex(paginationInfo.getFirstRecordIndex());
		vo.setLastIndex(paginationInfo.getLastRecordIndex());
		vo.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());
		
		model.addAttribute("searchApiList", apiSearchService.selMainList(vo));
		
		int totCnt  = apiSearchService.selMainListTotalCnt(vo);
		paginationInfo.setTotalRecordCount(totCnt);
		paginationInfo.calculate();
		model.addAttribute("paginationInfo", paginationInfo);//페이징 정보
		
		model.addAttribute("totalCnt", totCnt);
		
		return new ModelAndView( "jsonView", model );
	}
}
