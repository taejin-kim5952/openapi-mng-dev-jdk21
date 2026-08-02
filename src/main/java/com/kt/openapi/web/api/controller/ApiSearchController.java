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
import com.kt.openapi.web.api.service.ApiSearchService;
import com.kt.openapi.web.api.vo.ApiMainSearchVo;
import com.kt.openapi.web.auth.vo.AuthVO;
import com.kt.openapi.web.cmm.service.CmnService;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;



/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.api.controller
* 2. 타입명 : ApiSearchController.java
* 3. 작성일 : 2017. 12. 8. 오후 1:11:03
* 4. 작성자 : JungHwan Hwang
* 5. 설명 : OPEN API 검색 
* </pre>
*/
@Controller
@RequestMapping(value="/api/search")
public class ApiSearchController {
	
	private static final Logger LOG = LoggerFactory.getLogger(ApiSearchController.class);
	  
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
	 * 1. 메소드명 : mainList
	 * 2. 작성일   : 2017. 11. 10. 오후 2:21:33
	 * 3. 작성자   : JeonGeun Kang
	 * 4. 설명     : API 검색 화면
	 * </pre>
	 * @param request
	 * @param response
	 * @param model
	 * @return ModelAndView
	 * @throws Exception
	 */
	@RequestMapping(value="/mvMainList.do")
	public ModelAndView mvMainList(HttpSession session, HttpServletRequest request,HttpServletResponse response,  ModelMap model, ApiMainSearchVo vo ) throws Exception {
		
		LOG.debug("#######################  ApiSearchController mvMainList START ############################");
		ModelAndView mv = new ModelAndView();
		
		model.addAttribute("sysList", apiSearchService.selMainSysList(vo));
		
		model.addAttribute("totalCnt", apiSearchService.selMainListTotalCnt(vo));
		
		model.addAttribute("cateList", apiSearchService.selMainCateList(vo));
		
		mv.setViewName("api/searchMain");

		//setSession(session);
		
		return mv;
	}   
	
	
	/**
	 * <pre>
	 * 1. 메소드명 : selMainListAjax
	 * 2. 작성일   : 2017. 11. 10. 오후 5:43:47
	 * 3. 작성자   : JeonGeun Kang
	 * 4. 설명     : API 조회
	 * </pre>
	 * @param model
	 * @param vo
	 * @return ModelAndView
	 * @throws Exception
	 * TODO 나중에 코드 명칭 정해지면 mainList대신 코드 명칭으로 수정
	 */
	@ResponseBody
	@RequestMapping(value="/selMainListAjax.do", method=RequestMethod.POST)
	public ModelAndView selMainListAjax(HttpSession session , ModelMap model, ApiMainSearchVo vo) throws Exception {
		
		LOG.debug("#######################  ApiSearchController selMainListAjax START ############################");
		ModelAndView mv = new ModelAndView();
		LOG.info("vo.getSchText()" + vo.getSchText());
		LOG.info("vo.getSysId()" + vo.getSysId());
		
		vo.setPageUnit(pageUnit); 	// 페이지당 건수
		vo.setPageSize(pageSize);	// 페이지 리스트에 게시되는 건수
		
		/** pageing setting */
		Pagination paginationInfo = new Pagination();
		paginationInfo.setCurrentPageNo(vo.getPageIndex()); // 현재 페이지 인덱스
		paginationInfo.setRecordCountPerPage(vo.getPageUnit());
		paginationInfo.setPageSize(vo.getPageSize());
		
		LOG.info("vo.paginationInfo()" + paginationInfo);
		vo.setFirstIndex(paginationInfo.getFirstRecordIndex());
		vo.setLastIndex(paginationInfo.getLastRecordIndex());
		vo.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());
		
		model.addAttribute("info", apiSearchService.selMainList(vo));
		
		int totCnt  = apiSearchService.selMainListTotalCnt(vo);
		paginationInfo.setTotalRecordCount(totCnt);
		paginationInfo.calculate();
		model.addAttribute("paginationInfo", paginationInfo);//페이징 정보
		
		model.addAttribute("totalCnt", totCnt);
		
		return new ModelAndView( "jsonView", model );
	}
	
	public void setSession(HttpSession session) {
		
    	UserJoinVO userJVo = new UserJoinVO();
		userJVo.setMbrId("0001EDL7paepLUowDTqveogIbg==");
		userJVo.setMbrNm("암호화된이름");
		userJVo.setEnCmbrId("0001EDL7paepLUowDTqveogIbg==");
		
		ArrayList<AuthVO> authList = new ArrayList<AuthVO>();
		AuthVO authInfo = new AuthVO();
		authInfo.setAutId("1");
		authInfo.setSysId("SHUB");
		authInfo.setSysNm("SHUB");
		authInfo.setAutNm("관리자");
		
		authList.add(authInfo);
		
		authInfo = new AuthVO();
		authInfo.setAutId("2");
		authInfo.setSysId("SHUB");
		authInfo.setSysNm("SHUB");
		authInfo.setAutNm("네비게이션");
		
		authList.add(authInfo);
		
		authInfo = new AuthVO();
		authInfo.setAutId("3");
		authInfo.setSysId("IOT");
		authInfo.setSysNm("IOT");
		authInfo.setAutNm("테스트");
		
		authList.add(authInfo);
		
		authInfo = new AuthVO();
		authInfo.setAutId("4");
		authInfo.setSysId("SHUB");
		authInfo.setSysNm("SHUB");
		authInfo.setAutNm("그룹 관리자");
		
		authList.add(authInfo);
		
		authInfo = new AuthVO();
		authInfo.setAutId("5");
		authInfo.setSysId("IOT");
		authInfo.setSysNm("IOT");
		authInfo.setAutNm("시스템 관리자");
		
		authList.add(authInfo);
		
		authInfo = new AuthVO();
		authInfo.setAutId("6");
		authInfo.setSysId("IOT");
		authInfo.setSysNm("IOT");
		authInfo.setAutNm("운전");
		
		authList.add(authInfo);
		
		authInfo = new AuthVO();
		authInfo.setAutId("7");
		authInfo.setSysId("SHUB");
		authInfo.setSysNm("SHUB");
		authInfo.setAutNm("네비게이션");
		
		authList.add(authInfo);
		
		authInfo = new AuthVO();
		authInfo.setAutId("8");
		authInfo.setSysId("IOT");
		authInfo.setSysNm("IOT");
		authInfo.setAutNm("화상");
		
		authList.add(authInfo);
		
		userJVo.setAuthList(authList);
		
		session.setAttribute("ssUserVo",userJVo);
		
	}
	
	/**
	 * 신규 룰에 맞춘 API 검색 화면
	 * 
	 * 룰 안내
	 * 1. SHUB API(User Authentication, Service Authentication, Application Service)는 2차와 동일
	 * 2. 1번 항목을 제외한 나머지 API들은 전과 같음(이전 버전도 노출)
	 */
	@RequestMapping(value="/apiSearch.do")
	public ModelAndView apiSearch(HttpSession session, HttpServletRequest request,HttpServletResponse response,  ModelMap model, ApiMainSearchVo vo ) throws Exception {
		
		LOG.debug("#######################  ApiSearchController apiSearch START ############################");
		ModelAndView mv = new ModelAndView();

		model.addAttribute("serviceList", apiSearchService.selSystemList());//시스템 목록 조회
		
		model.addAttribute("searchList", cmnService.selComnList("APITPY1000"));//공통코드 - 검색조건
		
		mv.setViewName("api/apiSearchList");

		return mv;
	}
	
	/**
	 * 신규 룰에 맞춘 API 검색 화면
	 * 
	 * 룰 안내
	 * 1. SHUB API(User Authentication, Service Authentication, Application Service)는 2차와 동일
	 * 2. 1번 항목을 제외한 나머지 API들은 전과 같음(이전 버전도 노출)
	 */
	@ResponseBody
	@RequestMapping(value = "/apiSearchListAjax.do")
	public ModelAndView apiSearchListAjax(HttpSession session , ModelMap model, ApiMainSearchVo vo) throws Exception {
		
		LOG.debug("#######################  ApiSearchController apiSearchListAjax START ############################");
		LOG.info("vo.getSchText()" + vo.getSchText());
		LOG.info("vo.getSysId()" + vo.getSysId());
		
		LOG.info("searchYn ::: " + vo.getSearchYn());
		try {
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
			
			model.addAttribute("nlist", apiSearchService.selMainList(vo));
			
			int totCnt  = apiSearchService.selMainListTotalCnt(vo);
			paginationInfo.setTotalRecordCount(totCnt);
			paginationInfo.calculate();
			model.addAttribute("paginationInfo", paginationInfo);//페이징 정보
			
			model.addAttribute("totalCnt", totCnt);
			
			
		}catch(Exception e) {
			e.getMessage();
			LOG.debug("!@@!==>" + e.getMessage());
		}
		
		return new ModelAndView( "jsonView", model );
		
		
	}
	
}
