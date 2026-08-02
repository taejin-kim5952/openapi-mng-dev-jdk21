package com.kt.openapi.web.devsupport.controller;

import com.kt.openapi.fwk.online.page.Pagination;
import com.kt.openapi.web.bbs.cmn.service.BbsCmnService;
import com.kt.openapi.web.bbs.cmn.vo.BbsSearchVo;
import com.kt.openapi.web.cmm.upload.UploadFileUtils;
import com.kt.openapi.web.devsupport.service.DevSupportService;
import com.kt.openapi.web.devsupport.vo.DevSupportFileVo;
import com.kt.openapi.web.devsupport.vo.DevSupportManagerVo;
import com.kt.openapi.web.devsupport.vo.DevSupportSaveVo;
import com.kt.openapi.web.devsupport.vo.DevSupportVo;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import com.kt.openapi.web.util.MessageUtil;
import com.kt.openapi.web.util.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * <pre>
 * 1. 클래스명 : DevSupportController
 * 2. 작성일   : 2017. 12. 02. 오후 2:21:33
 * 4. 설명     : 기술 지원 (마이그레이션: Legacy Validator 제거)
 * </pre>
 */
@Controller
@RequestMapping(value="/devsupport")
public class DevSupportController {
	private static final Logger LOG = LoggerFactory.getLogger(DevSupportController.class);
	
	@Autowired
	private DevSupportService  devSupportService;

	@Value("${pageUnit:10}")
	private int pageUnit;

	@Value("${pageSize:10}")
	private int pageSize;
	
	@Autowired
	private BbsCmnService  bbsCmnService;
	
	@Autowired
    private UploadFileUtils uploadFileUtiles;

	/**
	 * TEST DATA등록 가이드
	 */
	@RequestMapping(value="/tdapply/testdataapply.do")
	public ModelAndView mvTestData(HttpServletRequest request,HttpServletResponse response,  ModelMap model) throws Exception {
		LOG.debug("#######################  devVmGuideController /testdataapply START ############################");
		ModelAndView mv = new ModelAndView();
		mv.setViewName("devsupport/tdapply/testdataapply");
		
		return mv;
	}
	
	/**
	 * 기술 지원 목록
	 */
	@RequestMapping(value="/devsupport/devSupportList.do")
	public ModelAndView mvDevSupportList(HttpServletRequest request,HttpServletResponse response,  ModelMap model) throws Exception {
		LOG.debug("#######################  devVmGuideController /devSupportList START ############################");
		ModelAndView mv = new ModelAndView();
		mv.setViewName("devsupport/devsupport/list");
		
		return mv;
	}
	
	/**
	 * 기술지원 요청 입력 폼
	 */	 
	 @RequestMapping(value="/devsupport/mvDevsupportReg.do")
	 public ModelAndView mvDevSupportWrite(HttpServletRequest request,HttpServletResponse response,  ModelMap model ,  HttpSession session, 
			 @ModelAttribute("devSupportSaveVo")  DevSupportSaveVo devSupportSaveVo, 
			 @ModelAttribute("devSupportVo")  DevSupportVo devSupportVo, 
			 @ModelAttribute("devSupportManagerVo")  DevSupportManagerVo devSupportManagerVo) throws Exception {
		
		LOG.debug("#######################  DevSupportController /devSupportWrite START ############################");
		ModelAndView mv = new ModelAndView();
		BbsSearchVo bbsSearchVo = new  BbsSearchVo();
		DevSupportVo vmap = new DevSupportVo();

		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		Map<String,String> map = new LinkedHashMap<String,String>();

		List<DevSupportVo> list= devSupportService.selDevSupportMyServiceList(devSupportVo);
		
		for(int i=0;i<list.size();i++){
		  DevSupportVo item = list.get(i);
		  map.put(item.getMyServiceInfraNm(), item.getMyServiceInfraNm());
		 }
		
		if(devSupportSaveVo != null && !"".equals( StringUtil.isNullToString(devSupportSaveVo.getPstingId())   ) ) {
			bbsSearchVo.setPstingId(devSupportSaveVo.getPstingId());
			bbsSearchVo.setBbsTypeCd(devSupportSaveVo.getBbsTypeCd());
			vmap = devSupportService.selDevSupportView(devSupportVo);
			devSupportSaveVo.setTitle( StringUtil.isNullToString( vmap.getTitle()));
			devSupportSaveVo.setSbst( StringUtil.isNullToString( vmap.getSbst()));
			model.addAttribute("vmap", vmap);
			List<DevSupportFileVo> fList = devSupportService.selDevSupportFileList(devSupportVo);
			model.addAttribute("fList", fList);
		}else {
			devSupportSaveVo.setSbst("""
					== vm 기술 지원 요청 ==
					[프로젝트 명 ] : (예:CAPRI, SCAP)
					[등록 요청  날짜]  : (예:2017-12-12 까지) [일정 변경 가능 여부 ]  : (예:변경 가능 ) """);
			model.addAttribute("vmap", vmap);
		}
	
		devSupportSaveVo.setRegrN((String)userJVo.getMbrNm());
		devSupportSaveVo.setRegr((String)userJVo.getMbrId());
		
		model.addAttribute("mySList", map);
		model.addAttribute("devSupportSaveVo", devSupportSaveVo);
		mv.setViewName("devsupport/devsupport/write");
		return mv;
	 }
	
	/**
	 * 기술 지원 글 등록/수정
	 */
	@RequestMapping(value = "/devsupport/saveForum.do")
	public ModelAndView saveForum(@ModelAttribute("devSupportSaveVo") @Valid DevSupportSaveVo devSupportSaveVo	
			 , HttpSession session
			 , BindingResult bindingResult 
			 , ModelAndView mv
			 , @ModelAttribute("uploadFile")  MultipartFile uploadFile) throws Exception {
		
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		
		if (bindingResult.hasErrors()) {
			List<ObjectError> list = bindingResult.getAllErrors();
            for (ObjectError e : list) {
            	String message = "";
            	if(list.get(0).getCode().equals("errors.integer" )  || e.getArguments().equals("devSupportSaveVo.adcId") ){
            		 message = "ADC 관리번호는 숫자만 입력 가능합니다.";
            	}else{
            		 message = MessageUtil.getMsg(e.getDefaultMessage(), new String[] {e.getArguments().toString()});
            	}           	
            	mv.addObject("msg", message);
            	mv.setViewName("devsupport/devsupport/write");
            }
		}else{
			if(! "".equals(StringUtil.isNullToString(devSupportSaveVo.getPstingId() )) ) {
				devSupportSaveVo.setAmdr(userJVo.getMbrId());
				devSupportSaveVo.setRegr(userJVo.getMbrId()); 
				int cnt = devSupportService.updDevSupport(devSupportSaveVo , uploadFile); 
				if(  cnt  == 0 ) {
					mv.addObject("msg", MessageUtil.getMsg("fail.common.msg"));
				}else {
					mv.addObject("msg", MessageUtil.getMsg("update.success.msg"));
				}
				mv.addObject("pstingId", devSupportSaveVo.getPstingId() );
			}else {
				devSupportSaveVo.setRegr(userJVo.getMbrId());
				String pstingId = devSupportService.saveForum(devSupportSaveVo, uploadFile);
				if( "".equals(StringUtil.isNullToString(pstingId) )) {
					mv.addObject("msg", MessageUtil.getMsg("fail.common.msg"));
				}else {
					mv.addObject("msg", MessageUtil.getMsg("insert.success.msg"));
				}
				mv.addObject("pstingId", pstingId);
			}
			mv.setViewName("devsupport/devsupport/write");
		}
		return mv; 
	}
	
	/**
	 * 기술 지원 상세보기
	 */
	@RequestMapping(value="/devsupport/mvDevSupportView.do")
	public ModelAndView mvDevSupportView(HttpServletRequest request,HttpServletResponse response,  ModelMap model , DevSupportVo param) throws Exception {
		ModelAndView mv = new ModelAndView();
		model.addAttribute("vmap", devSupportService.selDevSupportView(param));
		List<DevSupportFileVo> fList = devSupportService.selDevSupportFileList(param);
		model.addAttribute("fList", fList);
		mv.setViewName("devsupport/devsupport/view");
		return mv;
	}
	
	/**
	 * 기술지원 목록 조회 (Ajax)
	 */
	@RequestMapping(value="/devsupport/selDevSupportListAjax.do")
	public ModelAndView selNoticeListAjax(HttpServletRequest request,HttpServletResponse response,  ModelMap model , DevSupportVo param, HttpSession session) throws Exception {
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		param.setRegr(userJVo.getMbrId());
		param.setPageUnit(pageUnit); 
		param.setPageSize(pageSize);
		
		Pagination paginationInfo = new Pagination();
		paginationInfo.setCurrentPageNo(param.getPageIndex()); 
		paginationInfo.setRecordCountPerPage(param.getPageUnit());
		paginationInfo.setPageSize(param.getPageSize());
	
		param.setFirstIndex(paginationInfo.getFirstRecordIndex());
		param.setLastIndex(paginationInfo.getLastRecordIndex());
		param.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());
		model.addAttribute("nlist", devSupportService.selDevSupportList(param));
		
		int totCnt  = devSupportService.selDevSupportListCnt(param);
		paginationInfo.setTotalRecordCount(totCnt);
		model.addAttribute("paginationInfo", paginationInfo);
		
		return new ModelAndView( "jsonView", model );
	}
	
	/**
	 * 담당자 정보 리스트 (Ajax)
	 */
	@RequestMapping(value="/devsupport/selManagerListAjax.do")
	public ModelAndView selManagerListAjax(HttpServletRequest request,HttpServletResponse response,  
			 ModelMap model , 
			 @ModelAttribute("devSupportManagerVo")  DevSupportManagerVo devSupportManagerVo) throws Exception {
		
		devSupportManagerVo.setPageUnit(pageUnit); 
		devSupportManagerVo.setPageSize(pageSize); 
		
		Pagination paginationInfo = new Pagination();
		paginationInfo.setCurrentPageNo(devSupportManagerVo.getPageIndex()); 
		paginationInfo.setRecordCountPerPage(devSupportManagerVo.getPageUnit());
		paginationInfo.setPageSize(devSupportManagerVo.getPageSize());
	
		devSupportManagerVo.setFirstIndex(paginationInfo.getFirstRecordIndex());
		devSupportManagerVo.setLastIndex(paginationInfo.getLastRecordIndex());
		devSupportManagerVo.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());
		
		List<DevSupportManagerVo> listn =  devSupportService.selDevSupportManagerList(devSupportManagerVo);
		model.addAttribute("nlist", listn);
		int totCnt  = devSupportService.selDevSupportManagerCnt(devSupportManagerVo);
		paginationInfo.setTotalRecordCount(totCnt);
		model.addAttribute("paginationInfo", paginationInfo);
		
		return new ModelAndView("jsonManaPopView", model );
	}
}
