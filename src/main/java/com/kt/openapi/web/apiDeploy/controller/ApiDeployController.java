package com.kt.openapi.web.apiDeploy.controller;

import com.kt.openapi.fwk.online.page.Pagination;
import com.kt.openapi.web.adptran.api.service.AdptranApiService;
import com.kt.openapi.web.adptran.util.KsmUtil;
import com.kt.openapi.web.api.vo.ApiDefVO;
import com.kt.openapi.web.api.vo.ApiSpcVO;
import com.kt.openapi.web.apiDeploy.service.ApiDeployService;
import com.kt.openapi.web.apiDeploy.util.ApiDeployResultCode;
import com.kt.openapi.web.apiDeploy.util.ApiDeployUtil;
import com.kt.openapi.web.apiDeploy.vo.*;
import com.kt.openapi.web.apigw.entity.lamp.LampResponse;
import com.kt.openapi.web.apigw.entity.lamp.LampResponseBody;
import com.kt.openapi.web.apigw.exception.ServiceException;
import com.kt.openapi.web.apigw.services.lamp.LampLogService;
import com.kt.openapi.web.apigw.type.GwProfile;
import com.kt.openapi.web.auth.vo.AuthVO;
import com.kt.openapi.web.cmmn.ApiException;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import com.kt.openapi.web.util.CommonFunc;
import com.kt.openapi.web.util.SendMailUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
@RequestMapping(value = "/api/deploy")
public class ApiDeployController {

	private static final Logger LOG = LoggerFactory.getLogger(ApiDeployController.class);

	@Value("${pageUnit:10}")
	private int pageUnit;

	@Value("${pageSize:10}")
	private int pageSize;

	@Autowired
	private ApiDeployService apiDeployService;

	@Autowired
	private LampLogService lampLogService;

	// --[tag:adpt][drm][add]
	@Autowired
	private AdptranApiService adptranApiService;
	
	@Autowired
	private SendMailUtil sendMailUtil;
	
	//배포요청 시 메일 발송 리스트
	@Value("${deployapply.mail.list}")
	private String deployapplyMailList; 
	
	static final int maskingCnt = 2;

	/**
	 * <pre>
	 * 1. 메소드명 : mvDeployList
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 : deploy리스트
	 * </pre>
	 */
	@RequestMapping(value = "/mvDeployList.do")
	public ModelAndView mvDeployList() {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelAndView mv = new ModelAndView();
		mv.setViewName("api/deploy/deployList");

		return mv;

	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvApprovalListN
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 : deploy리스트
	 * </pre>
	 */
	@RequestMapping(value = "/mvApprovalListN.do")
	public ModelAndView mvApprovalListN() {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelAndView mv = new ModelAndView();
		mv.setViewName("api/deploy/approvalListNew");

		return mv;

	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvApprovalList
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 : deploy리스트
	 * </pre>
	 */
	@RequestMapping(value = "/mvApprovalList.do")
	public ModelAndView mvApprovalList() {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelAndView mv = new ModelAndView();
		mv.setViewName("api/deploy/approvalListNew");

		return mv;

	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvDeployListAjax
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 : deploy리스트 data ajax
	 * </pre>
	 * 
	 * @throws ApiException
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/mvDeployListAjax.do")
	public ModelAndView mvDeployListAjax(HttpServletRequest request, HttpServletResponse response, ApiDeploySearchVo apiDeploySearchVo ) throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
		String maskingId = null;
		List<ApiDeployVO> deployList = null;
		HashMap deployMap = new HashMap();

		ModelMap model = new ModelMap();
		// ApiDeploySearchVo apiDeploySearchVo = new ApiDeploySearchVo();
		CommCodeVo commCodeVo = new CommCodeVo();
		UserJoinVO userVO = (UserJoinVO)request.getSession().getAttribute("ssUserVo");
		

		apiDeploySearchVo.setPageUnit(pageUnit); // 페이지당 건수
		apiDeploySearchVo.setPageSize(pageSize);// 페이지 리스트에 게시되는 건수
		/** pageing setting */
		Pagination paginationInfo = new Pagination();
		paginationInfo.setCurrentPageNo(apiDeploySearchVo.getPageIndex()); // 현재 페이지 인덱스
		paginationInfo.setRecordCountPerPage(apiDeploySearchVo.getPageUnit());
		paginationInfo.setPageSize(apiDeploySearchVo.getPageSize());

		apiDeploySearchVo.setFirstIndex(paginationInfo.getFirstRecordIndex());
		apiDeploySearchVo.setLastIndex(paginationInfo.getLastRecordIndex());
		apiDeploySearchVo.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());
		
		/*
	     * API Link(Studio) Gateway Observer 권한 설정
	     *   Y: Being Observer(=Read)
	     *   N: Not Observer
	     * CYD - 2020.11.16
	     */
		apiDeploySearchVo.setObserverYn(userVO.getObserverYn());
		LOG.debug("[ObserverYn: {}][userVO.getAuthList().size(): {}]", userVO.getObserverYn(), userVO.getAuthList().size());
		try {
			ArrayList<String> authSysIdList= new ArrayList<String>();
			if ("Y".equalsIgnoreCase(userVO.getObserverYn())) {
				//kos운영자 계정인 경우 아이디 값을 세팅 하지 않고 전체 리스트가 나오도록 함
				apiDeploySearchVo.setRegr("");
//				List<AuthVO> list = userVO.getAuthList();
//				Iterator<AuthVO> itr = list.iterator();
//				while (itr.hasNext()) {
//					String autId =  itr.next().getAutId();
//					if ("72".equals(autId)) {
//						LOG.debug("<<< 들어옴 >>> ");
//						apiDeploySearchVo.setRegr("");
//					}
//				}
			}
			else {
				if (null != userVO) {
					//apiDeploySearchVo.setRegr(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
					apiDeploySearchVo.setRegr(CommonFunc.safeDbEncrypt(userVO.getMbrId()));
					Iterator<AuthVO> authitr = userVO.getAuthList().iterator();
					while (authitr.hasNext()) {
						AuthVO authVO = authitr.next();
						//--##[dep]authEgoMap = apiDeployService.selSysIdByAutId(authVO.getAutId());
						String sysId = authVO.getSysId();
						// 중복이면 패스 CYD-2020.11.16
						if (authSysIdList.contains(sysId)) continue;
						authSysIdList.add(sysId);
					}
				}
			}
			apiDeploySearchVo.setAuthSysIdList(authSysIdList);

			deployList = apiDeployService.selApiDeployList(apiDeploySearchVo);
			//저회 조건 세팅
			/*LOG.debug("<<< authList value => [authList[0] : {}] ", authList.size() );
			deployMap.put("searchVerifi",apiDeploySearchVo.getSearchVerifi());
			deployMap.put("searchDeploy",apiDeploySearchVo.getSearchDeploy());
			deployMap.put("searchSystem",apiDeploySearchVo.getSearchSystem());
			deployMap.put("searchSpc", apiDeploySearchVo.getSearchSpc());
			deployMap.put("stDate", apiDeploySearchVo.getStDate());
			deployMap.put("endDate", apiDeploySearchVo.getEndDate());
			deployMap.put("searchKeyword", apiDeploySearchVo.getSearchKeyword());
			deployMap.put("seq", apiDeploySearchVo.getSeq());
			deployMap.put("authList", authList);
			
			deployList = apiDeployService.selApiDeployListAuth(deployMap);*/
		
		}
		catch (Exception e1) {
			LOG.debug("<<< mvDeployListAjax Exception : [e1 => {}] ", e1);
			apiDeploySearchVo.setRegr("");
		}
		
		Iterator<ApiDeployVO> itr = deployList.iterator();
		// 사용자 정보를 복호화 하여 저장
		while (itr.hasNext()) {
			ApiDeployVO vo = itr.next();
			try {
				maskingId = ApiDeployUtil.mvPrivacyMask(CommonFunc.safeDbDecrypt(vo.getRegr()), maskingCnt);
				vo.setRegr(maskingId);
			} catch (Exception e) {
				break;
			}
		}

		model.addAttribute("nlist", deployList);// 목록 정보

		int totCnt = apiDeployService.selApiDeployListCnt(apiDeploySearchVo);
		paginationInfo.setTotalRecordCount(totCnt);
		paginationInfo.calculate();
		model.addAttribute("paginationInfo", paginationInfo);// 페이징 정보

		return new ModelAndView("jsonView", model);

	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvDeployView
	 * 2. 작성일 : 2019. 5. 27. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 : deploy view 호면
	 * </pre>
	 * 
	 * @throws ApiException @ModelAttribute("memberVO") @Valid MemberVO memberVO,
	 * 
	 */
	@RequestMapping(value = "/mvDeployView.do")
	public ModelAndView mvDeployView(HttpServletRequest request, HttpServletResponse response, ModelMap model, ApiDeploySearchVo apiDeploySearchVo, HttpSession session) {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		String maskingId = null;
		ModelAndView mv = new ModelAndView();

		ApiDeployVO vmap = new ApiDeployVO();
		try {
			LOG.debug("<<< procSeq>>> {}", CommonFunc.getSession("procSeq", session));
			apiDeploySearchVo.setSeq(CommonFunc.getSession("procSeq", session));
			vmap = apiDeployService.selDeployView(apiDeploySearchVo);

			maskingId = ApiDeployUtil.mvPrivacyMask(CommonFunc.safeDbDecrypt(vmap.getRegr()), maskingCnt);
			vmap.setRegr(maskingId);

			// 날짜 포맷 변경
			String regDt = vmap.getRegDt();
			if (regDt != null) {
				vmap.setRegDt(regDt.replaceAll("/", "-"));
			}
		} catch (ApiException e1) {
			// TODO Auto-generated catch block
			LOG.debug(e1.getMessage());
			vmap.setErrorMsg(ApiDeployResultCode.CD_DEPLOY_PROCESS_ERROR.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.debug(e.getMessage());
			vmap.setErrorMsg(ApiDeployResultCode.CD_DECRYPT_ERROR.getMessage());
		}

		// 아이디 마스킹

		/* apiDeploySearchVo.setApplyMsg("CCCCC <br> ddd"); */

		model.addAttribute("deployMap", vmap);

		mv.setViewName("api/deploy/deployView");

		return mv;

	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvDeployViewProcAjax
	 * 2. 작성일 : 2019. 5. 27. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 : deploy view 프로세스 이미지
	 * </pre>
	 * 
	 * @throws ApiException
	 * 
	 */
	@RequestMapping(value = "/mvDeployViewProcAjax.do")
	public ModelAndView mvDeployViewProcAjax(ApiDeploySearchVo param) throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelMap model = new ModelMap();

		ApiDeployVO map = apiDeployService.selDeployView(param);

		model.addAttribute("vmap", map);

		return new ModelAndView("jsonView", model);
	
	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvSelectListAjax
	 * 2. 작성일 : 2019. 5. 27. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 : view 검색 리스트 항목
	 * </pre>
	 * 
	 * @throws ApiException
	 * 
	 */
	@RequestMapping(value = "/mvSelectListAjax.do")
	public ModelAndView mvSelectListAjax(ApiDeploySearchVo param, HttpServletRequest request) throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
		
		//-- [tag:PRJ-20220901]
		String direct = ";%s;".formatted(KsmUtil.fnSafeStr((String)request.getParameter("direct")));
		boolean bIsSystemListOnly = (direct.indexOf(";systemlist_only;") != -1);

		ModelMap model = new ModelMap();

		// 시스템 리스트
		List<ApiDeploySystemVO> systemList = apiDeployService.selSystemList(param);
		model.addAttribute("systemList", systemList);

		if (false == bIsSystemListOnly) {
			CommCodeVo commCodeVo = new CommCodeVo();
			// 검증 상태 코드 리스트
			commCodeVo.setGroupCd(ApiDeployResultCode.CD_1000_VERIFI_BASE_CODE.getCode());
			List<ApiCommCodeVO> veriflist = apiDeployService.selCommCodeList(commCodeVo);

			// 배포상태
			commCodeVo.setGroupCd(ApiDeployResultCode.CD_1000_DEPLOY_BASE_CODE.getCode());
			List<ApiCommCodeVO> deployList = apiDeployService.selCommCodeList(commCodeVo);

			model.addAttribute("verifilist", veriflist);
			model.addAttribute("deployList", deployList);
		}

		return new ModelAndView("jsonView", model);

	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvApprovalListAjax
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 : Approval 리스트 data ajax
	 * </pre>
	 */
	@RequestMapping(value = "/mvApprovalListAjax.do")
	public ModelAndView mvApprovalListAjax(HttpServletRequest request, HttpServletResponse response, ApiDeploySearchVo param) throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelMap model = new ModelMap();

		String maskingId = null;

		param.setPageUnit(pageUnit); 	// 페이지당 건수
		param.setPageSize(pageSize);	//페이지 리스트에 게시되는 건수
		/** pageing setting */
		Pagination paginationInfo = new Pagination();
		paginationInfo.setCurrentPageNo(param.getPageIndex()); // 현재 페이지 인덱스
		paginationInfo.setRecordCountPerPage(param.getPageUnit());
		paginationInfo.setPageSize(param.getPageSize());

		param.setFirstIndex(paginationInfo.getFirstRecordIndex());
		param.setLastIndex(paginationInfo.getLastRecordIndex());
		param.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

		List<ApiDeployApplyVO> list = apiDeployService.selApiDeployApplyList(param);
		Iterator<ApiDeployApplyVO> itr = list.iterator();

		// 사용자 정보를 복호화 하여 저장
		while (itr.hasNext()) {

			ApiDeployApplyVO vo = itr.next();
			try {
				maskingId = ApiDeployUtil.mvPrivacyMask(CommonFunc.safeDbDecrypt(vo.getRegr()),
						maskingCnt);

				LOG.debug(" maskingId ++ []{}", maskingId);

				vo.setRegr(maskingId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				break;
			}

		}

		model.addAttribute("nlist", list);// 목록 정보

		int totCnt = apiDeployService.selApiDeployApplyListCnt(param);
		paginationInfo.setTotalRecordCount(totCnt);
		paginationInfo.calculate();
		model.addAttribute("paginationInfo", paginationInfo);// 페이징 정보

		return new ModelAndView("jsonView", model);

	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvSpcListAjax
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 : 2depth list
	 * </pre>
	 * 
	 * @throws ApiException
	 * @throws Exception
	 */
	@RequestMapping(value = "/mvSpcListAjax.do")
	public ModelAndView mvSpcListAjax(HttpServletRequest request, HttpServletResponse response, ApiDeploySearchVo param) throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelMap model = new ModelMap();

		// 시스템 리스트
		List<ApiSpcVO> spcList = apiDeployService.selSpcList(param);

		model.addAttribute("spcList", spcList);

		return new ModelAndView("jsonView", model);
	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvDeployApply
	 * 2. 작성일 : 2019. 5. 27. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 : 상용 배포 요청
	 * </pre>
	 * 
	 */
	@RequestMapping(value = "/mvDeployApply.do")
	public ModelAndView mvDeployApply(HttpServletRequest request, HttpServletResponse response, DeployApplyVo vo, HttpSession session) throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(),Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelMap model = new ModelMap();
		ApiDeployInsertVo apiDeployInsertVo = new ApiDeployInsertVo();
		Map<String, String> mailMap = new HashMap<String, String>();
	
		int deployApplySeq = 0;

		try {
			
			// 아이디 암호화
			// vo.setApplyRegr(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
			vo.setApplyRegr(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));

			// 배포신청은 기본값으로 들어감
			vo.setDeployCd(ApiDeployResultCode.CD_1050_DEPLOY_APPLY_CODE.getCode());	//-- DEPLOY1050-배포신청
			// 시스템 리스트
			deployApplySeq = apiDeployService.deployApply(vo);

			if (deployApplySeq > 0) { // 단계 진행 프로세스 실행

				//apiDeployInsertVo.setProcessGubun(ResultCode.GB_DEPLOY_UPDATE_GUBUN.getCode());
				apiDeployInsertVo.setSeq(vo.getDeployProcSeq());
				// apiDeployInsertVo.setRegr("0001AopbSP0gd4nq/w24TWxPeg==");
				apiDeployInsertVo.setRegr(vo.getApplyRegr());
				apiDeployInsertVo.setDeployCd(ApiDeployResultCode.CD_1050_DEPLOY_APPLY_CODE.getCode());	//-- DEPLOY1050-배포신청
				apiDeployInsertVo.setDeployAdm(vo.getApplyRegr());
				
				//int deployProcSeq = mvTempExcute(request, response, apiDeployInsertVo, session);
				int rtn = apiDeployService.updateReDeployApply(apiDeployInsertVo);
			}

			// 요청 완료시 메일 발송 			
			String [] deployApplyMailArr = deployapplyMailList.split(","); 
			
			for(String mail : deployApplyMailArr) {
				
				mailMap.put("tempId", "100000081"); //APILink G/W 운영자 통지용 tempId
				mailMap.put("title", ApiDeployResultCode.MAIL_TITLE_1050.getMessage());
				mailMap.put("content", vo.getBigo()+("\n신청자 ID : "+session.getAttribute("mbrId").toString()).replaceAll("(\r\n|\r|\n|\n\r)", "<br>"));
				mailMap.put("toMail", mail);
				
				sendMailUtil.sendMailcall(mailMap);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			vo.setApplyRegr("");
			// vo.setApplyRegr("0001AopbSP0gd4nq/w24TWxPeg==");
			LOG.debug(e.getMessage());
		}

		model.addAttribute("deployApplySeq", deployApplySeq);
		return new ModelAndView("jsonView", model);
	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvTempExcute
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 :
	 */
	@RequestMapping(value = "/mvTempExcute.do")
	public int mvTempExcute(HttpServletRequest request, HttpServletResponse response, ApiDeployInsertVo vo, HttpSession session) throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		int rtn = 0;

		// 아이디 암호화
		try {
			vo.setRegr(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
			// vo.setRegr("0001AopbSP0gd4nq/w24TWxPeg==");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			vo.setRegr("");
			LOG.debug(e.getMessage());
			
		}

		if ("insert".equals(vo.getProcessGubun())) {
			rtn = apiDeployService.insertDeployInfo(vo);

		} else if ("update".equals(vo.getProcessGubun())) {

			rtn = apiDeployService.updateDeployProc(vo);

			LOG.debug("{}[] rtn []{}", rtn);
		}
		// 프로세스 기본정보 등록

		// model.addAttribute("procInt", 3);
		return rtn;

	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvTempForm
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 : 임시입력 폼
	 */
	@RequestMapping(value = "/mvTempForm.do")
	public ModelAndView mvTempForm() throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelAndView mv = new ModelAndView();

		mv.setViewName("api/deploy/tempForm");

		return mv;
	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvTempFormDrm
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 :
	 */
	@RequestMapping(value = "/mvTempFormDrm.do")
	public ModelAndView mvTempFormDrm() throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelAndView mv = new ModelAndView();

		mv.setViewName("api/deploy/tempFormDrm");

		return mv;
	}

	/**
	 * <pre>
	 * 1. 메소드명 : fnUpdateProc
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 :
	 * 
	 * @throws ApiException
	 */
	public void fnUpdateProc(ApiDeployInsertVo param) throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ApiDeploySearchVo apiDeploySearchVo = new ApiDeploySearchVo();
		apiDeploySearchVo.setSeq(param.getSeq());
		ApiDeployVO egovProcInfo = new ApiDeployVO();

		// 현재의 프로세스 정보 뽑아오기
		List<ApiDeployVO> procMap = apiDeployService.selApiDeployList(apiDeploySearchVo);

		Iterator<ApiDeployVO> itr = procMap.iterator();
		// 사용자 정보를 복호화 하여 저장
		while (itr.hasNext()) {
			egovProcInfo = itr.next();
		}

		LOG.debug("## seq value ##" + egovProcInfo.getSeq());
	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvDeployApplyListAjax
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 : deploy리스트 data ajax
	 * </pre>
	 * 
	 * @throws ApiException
	 * @throws Exception
	 */
	@RequestMapping(value = "/mvDeployApplyListAjax.do")
	public ModelAndView mvDeployApplyListAjax(HttpServletRequest request, HttpServletResponse response, ApiDeploySearchVo apiDeploySearchVo) throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		String maskingId = null;

		ModelMap model = new ModelMap();
		// ApiDeploySearchVo apiDeploySearchVo = new ApiDeploySearchVo();
		CommCodeVo commCodeVo = new CommCodeVo();

		apiDeploySearchVo.setPageUnit(pageUnit); // 페이지당 건수
		apiDeploySearchVo.setPageSize(pageSize);// 페이지 리스트에 게시되는 건수
		/** pageing setting */
		Pagination paginationInfo = new Pagination();
		paginationInfo.setCurrentPageNo(apiDeploySearchVo.getPageIndex()); // 현재 페이지 인덱스
		paginationInfo.setRecordCountPerPage(apiDeploySearchVo.getPageUnit());
		paginationInfo.setPageSize(apiDeploySearchVo.getPageSize());

		apiDeploySearchVo.setFirstIndex(paginationInfo.getFirstRecordIndex());
		apiDeploySearchVo.setLastIndex(paginationInfo.getLastRecordIndex());
		apiDeploySearchVo.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

		// apiDeploySearchVo.setSearchKeyword(requestMap.get("apiNm"));

		List<ApiDeployApplyVO> list = apiDeployService.selApiDeployApplyList(apiDeploySearchVo);
		Iterator<ApiDeployApplyVO> itr = list.iterator();

		// 사용자 정보를 복호화 하여 저장
		while (itr.hasNext()) {

			ApiDeployApplyVO vo = itr.next();
			try {

				maskingId = ApiDeployUtil.mvPrivacyMask(CommonFunc.safeDbDecrypt(vo.getRegr()),
						maskingCnt);
				vo.setRegr(maskingId);
				LOG.debug("####");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				break;
			}

		}

		model.addAttribute("nlist", list);// 목록 정보

		int totCnt = apiDeployService.selApiDeployApplyListCnt(apiDeploySearchVo);
		paginationInfo.setTotalRecordCount(totCnt);
		paginationInfo.calculate();
		model.addAttribute("paginationInfo", paginationInfo);// 페이징 정보

		return new ModelAndView("jsonView", model);

	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvDeployApplyPrint
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 : deploy리스트 data ajax
	 * </pre>
	 */
	@RequestMapping(value = "/mvDeployApplyPrintAjax.do")
	public ModelAndView mvDeployApplyPrint(DeployApplyVo deployApplyVo) {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelMap model = new ModelMap();

		try {
			ApiDeployApplyVO deployApplyView = apiDeployService.selDeployApplyView(deployApplyVo);
			model.addAttribute("deployView", deployApplyView);
		} catch (ApiException e) {
			// TODO Auto-generated catch blockd
			LOG.error("## mvDeployApplyPrint Error :: {}", ApiDeployResultCode.RC_200_NO_EXIST_DATA);
			model.addAttribute("error", ApiDeployResultCode.RC_200_NO_EXIST_DATA);
			LOG.debug(e.getMessage());
		}

		return new ModelAndView("jsonView", model);
	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvCbDeployViewAjax
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 : 상용 배포 이력 ajax
	 * </pre>
	 */
	@RequestMapping(value = "/mvCbDeployViewAjax.do")
	public ModelAndView mvCbDeployViewAjax(DeployHstVo deployHstVo) throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelMap model = new ModelMap();

		List<ApiDeployHstVO> list = apiDeployService.selApiDeployView(deployHstVo);

		model.addAttribute("nlist", list);// 목록 정보

		return new ModelAndView("jsonView", model);

	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvTbDeployHstAjax
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 : TB배포 이력 보기
	 * </pre>
	 */
	@RequestMapping(value = "/mvTbDeployHstAjax.do")
	public ModelAndView mvTbDeployHstAjax(DeployHstVo deployHstVo) throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelMap model = new ModelMap();

		List<ApiDeployHstVO> list = apiDeployService.selApiTbDeployHst(deployHstVo);

		model.addAttribute("nlist", list);// 목록 정보

		return new ModelAndView("jsonView", model);

	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvVerifiResltAjax
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 : 검증결과 리스트  ajax
	 * </pre>
	 * 
	 * @throws ApiException
	 */
	@RequestMapping(value = "/mvVerifiResltAjax.do")
	public ModelAndView mvVerifiResltAjax(VerifiResultVo verifiResultVo) throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelMap model = new ModelMap();

		List<ApiVerifiResultVO> list = null;

		LOG.debug("getDeployProcSeq >>>" + verifiResultVo.getDeployProcSeq());
		try {
			list = apiDeployService.selVerifiList(verifiResultVo);
			LOG.debug("##list SIZE#" + list.size());
		} catch (ApiException e) {
			model.addAttribute("error", ApiDeployResultCode.RC_200_NO_EXIST_DATA);
			LOG.debug(e.getMessage());
		}

		model.addAttribute("nlist", list);

		return new ModelAndView("jsonView", model);
	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvDeployHstAjax
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 : 배포 결과 리스트
	 * </pre>
	 * 
	 * @throws ApiException
	 */
	@RequestMapping(value = "/mvDeployHstAjax.do")
	public ModelAndView mvDeployHstAjax(DeployHstVo deployHstVo) throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelMap model = new ModelMap();

		deployHstVo.setPageUnit(pageUnit); // 페이지당 건수
		deployHstVo.setPageSize(pageSize);// 페이지 리스트에 게시되는 건수
		/** pageing setting */
		Pagination paginationInfo = new Pagination();
		paginationInfo.setCurrentPageNo(deployHstVo.getPageIndex()); // 현재 페이지 인덱스
		paginationInfo.setRecordCountPerPage(deployHstVo.getPageUnit());
		paginationInfo.setPageSize(deployHstVo.getPageSize());

		deployHstVo.setFirstIndex(paginationInfo.getFirstRecordIndex());
		deployHstVo.setLastIndex(paginationInfo.getLastRecordIndex());
		deployHstVo.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

		List<ApiDeployHstVO> list = null;

		try {
			list = apiDeployService.selDeployHstList(deployHstVo);
			LOG.debug("##list SIZE#" + list.size());

		} catch (ApiException e) { // TODO Auto-generated catch block
			model.addAttribute("error", ApiDeployResultCode.RC_200_NO_EXIST_DATA);
			LOG.debug(e.getMessage());
		}

		model.addAttribute("nlist", list);// 목록 정보

		int totCnt = apiDeployService.selDeployHstCnt(deployHstVo);
		paginationInfo.setTotalRecordCount(totCnt);
		paginationInfo.calculate();
		model.addAttribute("paginationInfo", paginationInfo);// 페이징 정보

		return new ModelAndView("jsonView", model);
	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvCbDeployProc
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 : 상용 배포 진행 프로세스
	 * </pre>
	 * 
	 * @throws ApiException
	 */
	@RequestMapping(value = "/mvCbDeployProc.do")
	public ModelAndView mvCbDeployProc(@RequestParam HashMap<String, String> requestMap, HttpSession session) throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelMap model = new ModelMap();

		ApiDeployInsertVo apiDeployInsertVo = new ApiDeployInsertVo();
		ApiDeploySearchVo apiDeploySearchVo = new ApiDeploySearchVo();
		DeployHstVo deployHstVo = new DeployHstVo();

		DeployApplyVo deployApplyVo = new DeployApplyVo();

		String returnCode = ApiDeployResultCode.CD_RETURN_FAIL.getCode();

		int deployApplySeq;
		int deployProcSeq;

		try {

			// -- 1-- 키 값으로 상용 배포 신청 seq값을 받음
			// int deployProcSeq = Integer.parseInt(requestMap.get("seq"));
			LOG.debug(" requestMap seq >>> {}  ", requestMap);
			deployApplySeq = Integer.parseInt(requestMap.get("seq"));
			LOG.debug(" deployApplySeq seq >>> {}  ", deployApplySeq);
			deployApplyVo.setSeq(deployApplySeq);
			deployApplyVo.setDeployApplySeq(deployApplySeq);

			// -- 2-- 배포 신청 seq로 Deploy Proc의 seq값 출력
			ApiDeployApplyVO deployApplyMap = apiDeployService.selDeployApplyView(deployApplyVo);

			// -- 3-- 상용 배포 성공 여부를 request로 받는다
			//-- DEPLOY1070-배포완료, DEPLOY1063-배포실패
			String deployCd = "SUCCESS".equals(requestMap.get("sucessYn")) ? ApiDeployResultCode.CD_1070_DEPLOY_APPLY_CODE.getCode() : ApiDeployResultCode.CD_1063_DEPLOY_APPLY_CODE.getCode();
			// 변경 사항이 없는 현재의 컬럼 값 가져오기
			// apiDeploySearchVo.setSeq(deployProcSeq);

			// -- 4 --proc Seq를 세팅하고 배포 프로세스 테이블의 정보 search
			deployProcSeq = Integer.parseInt(deployApplyMap.getDeployProcSeq() != null ? deployApplyMap.getDeployProcSeq().toString() : "0");
			apiDeploySearchVo.setSeq(deployProcSeq);

			// -- 5 --Deploy Proc테이블의 정보들을 뽑아온다
			ApiDeployVO deploySearchMap = apiDeployService.selDeployView(apiDeploySearchVo);

			LOG.debug(" <<< Deploy PROC UPDATE >>>  ");

			// STEP 1 -- Deploy PROC UPDATE
			apiDeployInsertVo.setSeq(deployProcSeq);
			apiDeployInsertVo.setDeployCd(deployCd); //-- DEPLOY1070-배포완료, DEPLOY1063-배포실패
			apiDeployInsertVo.setVerifiCd(deploySearchMap.getVerifiCd()); // 검증코드는 현재와 동일하게 입력

			apiDeployInsertVo.setRegr(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));

			apiDeployInsertVo.setDeployAdm(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
			apiDeployInsertVo.setUseYn("Y");
			apiDeployInsertVo.setBigo((deploySearchMap.getBigo() == null) ? "" : deploySearchMap.getBigo()); // 비고도 현재와 동일하게 입력

			// STEP 2 -- KOA_DEPLOY_APPLY UPDATE
			deployApplyVo.setDeployCd(deployCd);	//-- DEPLOY1070-배포완료, DEPLOY1063-배포실패
			deployApplyVo.setDeployDesc(""); // 설명은 예비 필드
			deployApplyVo.setDeployAdm(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
			deployApplyVo.setBigo((deployApplyMap.getBigo() == null) ? "" : deployApplyMap.getBigo());
			deployApplyVo.setComment("");

			// STEP 3-- Deploy HISTORY INSERT
			deployHstVo.setDeployAdm(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
			deployHstVo.setSuccessYn("SUCCESS".equals(requestMap.get("sucessYn")) ? "Y" : "N");
			deployHstVo.setResultCd((requestMap.get("resultCd") == null) ? "" : requestMap.get("resultCd"));
			deployHstVo.setResultMsg((requestMap.get("resultMsg") == null) ? "" : requestMap.get("resultMsg"));
			deployHstVo.setDeployApplySeq(deployApplySeq);
			deployHstVo.setDeployGb(ApiDeployResultCode.CD_DEPLOY_CB_GUBUN.getCode());
			deployHstVo.setDeployProcSeq(deployProcSeq); // tb일경우 Proc seq를 입력해 준다

			returnCode = apiDeployService.updateDeployExcuteProc(apiDeployInsertVo, deployApplyVo, deployHstVo);

			model.addAttribute("returnCode", returnCode);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.debug(e.getMessage());
			model.addAttribute("returnCode", ApiDeployResultCode.CD_RETURN_FAIL.getCode());
		}

		/*
		 * DEPLOY_CD = #deployCd#, VERIFI_CD = #verifiCd#, DEPLOY_ST_DT = #deployStDt#,
		 * 
		 * UPDATE_DT = getdate(), UPDATE_USR = #regr#, DEPLOY_ADM = #deployAdm#, USE_YN
		 * = #useYn#, bigo = #bigo#
		 */
		// 배포

		// apiDeployService.updateDeployFailProc(apiDeployInsertVo, deployApplyVo, deployHstVo);

		return new ModelAndView("jsonView", model);
	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvTbDeployProc
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 : 상용 배포 진행 프로세스
	 * </pre>
	 * 
	 * @throws ApiException
	 */
	@RequestMapping(value = "/mvTbDeployProc.do")
	public ModelAndView mvTbDeployProc(@RequestParam HashMap<String, String> requestMap, HttpSession session) throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelMap model = new ModelMap();

		ApiDeployInsertVo apiDeployInsertVo = new ApiDeployInsertVo();
		ApiDeploySearchVo apiDeploySearchVo = new ApiDeploySearchVo();
		DeployHstVo deployHstVo = new DeployHstVo();
		int deployProcSeq = Integer.parseInt(requestMap.get("deployProcSeq"));
		String deployGb = requestMap.get("deployGb");
		String returnCode = ApiDeployResultCode.CD_RETURN_FAIL.getCode();

		//-- DEPLOY1020-TB배포완료, DEPLOY1013-TB배포실패
		String deployCd = "SUCCESS".equals(requestMap.get("sucessYn")) ? ApiDeployResultCode.CD_1020_DEPLOY_APPLY_CODE.getCode() : ApiDeployResultCode.CD_1013_DEPLOY_APPLY_CODE.getCode();
		try {
			// STEP 1 -- Deploy PROC UPDATE
			LOG.debug("<<< STEP 1 >>>");

			apiDeployInsertVo.setSeq(deployProcSeq);
			apiDeployInsertVo.setDeployCd(deployCd); //-- DEPLOY1020-TB배포완료, DEPLOY1013-TB배포실패
			apiDeployInsertVo.setVerifiCd(ApiDeployResultCode.CD_1010_VERIFI_BASE_CODE.getCode()); // 검증코드 전이기 기본값 입력
			apiDeployInsertVo.setRegr(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
			apiDeployInsertVo.setDeployAdm(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString())); // TB배포 단계에서 담당자가 없음
			apiDeployInsertVo.setUseYn("Y");
			apiDeployInsertVo.setBigo(""); // 최초 단계에서는 비고값을 입력하지 않음

			// STEP 3-- Deploy HISTORY INSERT
			LOG.debug("<<< STEP 3 >>>");
			deployHstVo.setDeployAdm(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
			deployHstVo.setSuccessYn("SUCCESS".equals(requestMap.get("sucessYn")) ? "Y" : "N");
			deployHstVo.setResultCd((requestMap.get("resultCd") == null) ? "" : requestMap.get("resultCd"));
			deployHstVo.setResultMsg((requestMap.get("resultMsg") == null) ? "" : requestMap.get("resultMsg"));
			deployHstVo.setDeployApplySeq(0); // TB일경우 배포 SEQ를 기본 0으로 입력
			deployHstVo.setDeployGb(deployGb);
			deployHstVo.setDeployProcSeq(deployProcSeq); // tb일경우 Proc seq를 입력해 준다

			returnCode = apiDeployService.updateDeployTbExcuteProc(apiDeployInsertVo, deployHstVo);

			model.addAttribute("returnCode", returnCode);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			LOG.debug("mvTbDeployProc ERROR >>> {} ", e);
			model.addAttribute("returnCode", ApiDeployResultCode.CD_RETURN_FAIL.getCode());
		}

		return new ModelAndView("jsonView", model);
	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvVerifiInsert
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 :
	 * </pre>
	 * 
	 * @throws ApiException
	 */
	@RequestMapping(value = "/mvVerifiInsert.do")
	public ModelAndView mvVerifiInsert(@RequestParam HashMap<String, String> requestMap, HttpSession session) throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelMap model = new ModelMap();
		VerifiResultVo verifiResultVo = new VerifiResultVo();
		int returnCode = 0;

		try {

			LOG.debug("<< MEMBER ID >> {}", session.getAttribute("mbrId").toString());

			int deployProcSeq = Integer.parseInt(requestMap.get("deployProcSeq"));
			String resultCd = (requestMap.get("resultCd") == null) ? "" : requestMap.get("resultCd");
			String resultMsg = (requestMap.get("resultMsg") == null) ? "" : requestMap.get("resultMsg");
			String successYn = (requestMap.get("successYn") == null) ? "" : requestMap.get("successYn");

			LOG.debug("<< successYn >> {}", successYn);

			verifiResultVo.setDeployProcSeq(deployProcSeq);
			verifiResultVo.setResultCd(resultCd);
			verifiResultVo.setResultMsg(resultMsg);
			verifiResultVo.setVerifiUsr(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
			verifiResultVo.setSuccessYn(("SUCCESS".equals(successYn)) ? "Y" : "N");

			returnCode = apiDeployService.updateVerifiExcuteProc(verifiResultVo);

			model.addAttribute("returnCode",
					(returnCode > 0) ? ApiDeployResultCode.CD_RETURN_SUCCESS.getCode() : ApiDeployResultCode.CD_RETURN_FAIL.getCode());

		} catch (Exception e) {

			
			LOG.debug("mvVerifiInsert ERROR >>> {} ", e);
			model.addAttribute("returnCode", ApiDeployResultCode.CD_RETURN_FAIL.getCode());

		}

		return new ModelAndView("jsonView", model);
	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvVerifiStart
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 :
	 * </pre>
	 * 
	 * @throws ApiException
	 */
	@RequestMapping(value = "/mvVerifiStart.do")
	public ModelAndView mvVerifiStart(@RequestParam HashMap<String, String> requestMap, HttpSession session) throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelMap model = new ModelMap();

		ApiDeployInsertVo apiDeployInsertVo = new ApiDeployInsertVo();
		ApiDeploySearchVo apiDeploySearchVo = new ApiDeploySearchVo();

		String returnCode = ApiDeployResultCode.CD_RETURN_FAIL.getCode();

		int deployProcSeq;

		try {

			// -- 2 --proc Seq를 세팅하고 배포 프로세스 테이블의 정보 search
			deployProcSeq = Integer.parseInt(requestMap.get("deployProcSeq").toString());
			apiDeployInsertVo.setSeq(deployProcSeq);

			// -- 5 --Deploy Proc테이블의 정보들을 뽑아온다
			apiDeploySearchVo.setSeq(deployProcSeq);
			ApiDeployVO deploySearchMap = apiDeployService.selDeployView(apiDeploySearchVo);

			// -- 1-- 검증 기본 코드 시작

			LOG.debug(" <<< Deploy PROC UPDATE >>>  ");

			// STEP 1 -- Deploy PROC UPDATE

			LOG.debug(" <<<requestMap.get(\"verifiGb\") >>>  {}", requestMap.get("verifiGb"));
			apiDeployInsertVo.setSeq(deployProcSeq); // DEPLOY PROC SEQ
			//-- DEPLOY1030-검증시작, DEPLOY1040-검증완료
			apiDeployInsertVo.setDeployCd(("START".equals(requestMap.get("verifiGb"))) ? ApiDeployResultCode.CD_1030_DEPLOY_APPLY_CODE.getCode() : ApiDeployResultCode.CD_1040_DEPLOY_APPLY_CODE.getCode()); // 검증 시작인지 끝인지 구분
			apiDeployInsertVo.setVerifiCd(("START".equals(requestMap.get("verifiGb"))) ? ApiDeployResultCode.CD_1020_VERIFI_BASE_CODE.getCode() : ApiDeployResultCode.CD_1030_VERIFI_BASE_CODE.getCode());

			apiDeployInsertVo.setRegr(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));

			// apiDeployInsertVo.setDeployAdm(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
			apiDeployInsertVo.setUseYn("Y");
			apiDeployInsertVo
					.setBigo((deploySearchMap.getBigo() == null) ? "" : deploySearchMap.getBigo()); // 비고도
																													// 현재와
																													// 동일하게
																													// 입력

			int returnSeq = apiDeployService.updateVerifiStartProc(apiDeployInsertVo);

			model.addAttribute("returnCode", returnCode);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.debug(e.getMessage());
			model.addAttribute("returnCode", ApiDeployResultCode.CD_RETURN_FAIL.getCode());
		}

		/*
		 * DEPLOY_CD = #deployCd#, VERIFI_CD = #verifiCd#, DEPLOY_ST_DT = #deployStDt#,
		 * 
		 * UPDATE_DT = getdate(), UPDATE_USR = #regr#, DEPLOY_ADM = #deployAdm#, USE_YN
		 * = #useYn#, bigo = #bigo#
		 */
		// 배포

		// apiDeployService.updateDeployFailProc(apiDeployInsertVo, deployApplyVo, deployHstVo);

		return new ModelAndView("jsonView", model);
	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvTescCaseListAjax
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 : 테스트 케이스 ajax
	 * </pre>
	 * 
	 * @throws ApiException
	 */
	@RequestMapping(value = "/mvTescCaseListAjax.do")
	public ModelAndView mvTescCaseListAjax(TestCaseListVo testCaseListVo, HttpSession session) {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelMap model = new ModelMap();

		try {

			testCaseListVo.setApiNo(CommonFunc.getSession("apiNo", session));

			List<ApiTestCaseVO> list = apiDeployService.selTescCaseResult(testCaseListVo);

			model.addAttribute("nlist", list);
			model.addAttribute("returnCd", ApiDeployResultCode.CD_RETURN_FAIL);

		} catch (ApiException e) {

			model.addAttribute("returnCd", ApiDeployResultCode.CD_RETURN_FAIL);
			// TODO Auto-generated catch block
			LOG.debug(e.getMessage());
		}

		return new ModelAndView("jsonView", model);
	}

	
	
	/**
	 * <pre>
	 * 1. 메소드명 : mvVerifyExecute
	 * 2. 작성일 : 2019. 6. 25.
	 * 3. 작성자 :
	 * 4. 설명 : API검증
	 * </pre>
	 */
	@RequestMapping(value="/mvVerifyExecute.do") 
	public ModelAndView mvVerifyExecute(HttpSession session) {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelAndView mv = new ModelAndView();

		mv.addObject("apiNo", String.valueOf(CommonFunc.getSession("apiNo", session)));
		mv.addObject("procSeq", String.valueOf(CommonFunc.getSession("procSeq", session)));

		mv.setViewName("api/deploy/verifyExecute");
		
		return mv;
	}

	/**
	 * <pre>
	 * 1. 메소드명 : setSession
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 :
	 * </pre>
	 */
	@RequestMapping(value = "/setSession.do")
	public ModelAndView setSession(@RequestParam HashMap<String, String> requestMap, HttpSession session) {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
		
		ModelMap model = new ModelMap();

		LOG.debug("<<<@RequestParam >>>{}, {}", requestMap.get("sessionNm"), requestMap.get("sessionValue"));

		CommonFunc.setSession(requestMap.get("sessionNm"), requestMap.get("sessionValue"), session);

		if(CommonFunc.getSession(requestMap.get("sessionNm"), session) == Integer.parseInt(requestMap.get("sessionValue"))){
			model.addAttribute("returnCd", ApiDeployResultCode.CD_RETURN_SUCCESS.getCode());
		}else{
			model.addAttribute("returnCd", ApiDeployResultCode.CD_RETURN_FAIL.getCode());
		}

	
		return new ModelAndView("jsonView", model);

	}

	/**
	 * <pre>
	 * 1. 메소드명 : lampLogAjax
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 : LAMP로그 AJAX
	 * </pre>
	 */

	@RequestMapping(value = "/lampLogAjax.do")
	public ModelAndView lampLogAjax() {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelMap model = new ModelMap();

		String searchDate = "20190603";
		String transactionId = "f851bc65-4963-42a2-b8fa-b4804df50d15";
		String apiId = "";
		int i = 0;

		try {
			LampResponse rtn = lampLogService.getByTransaction(GwProfile.TB, searchDate, transactionId, apiId);

			String totalRtn = lampLogService.getRawByTransaction(GwProfile.TB, searchDate, transactionId, apiId);
			// String rtn = lampLogService.getRawByTransaction(GwProfile.TB, searchDate,
			// transactionId, apiId);
			LOG.debug("<<< getMessage >>> {}", rtn.getMessage());
			LOG.debug("<<< getStatus >>> {}", rtn.getStatus());
			LOG.debug("<<< getData >>> {}", rtn.getData().iterator());

			model.addAttribute("status", rtn.getStatus());
			model.addAttribute("message", rtn.getMessage());
			model.addAttribute("totRtn", totalRtn);
			// model.addAttribute("getData", rtn.attributeValue);

			Iterator<LampResponseBody> itr = rtn.getData().iterator();

			while (itr.hasNext()) {
				LOG.debug("  <<<  >>> ");
				LampResponseBody logEntry = itr.next();
				try {

					LOG.debug("  <<< TEST CASE SEQ >>> {} :: {}", i, logEntry.getResponse());
					LOG.debug("  <<< getCaller >>> {} ::     {}", i, logEntry.getCaller());
					LOG.debug("  <<< getDestination >>> {}   {}", i, logEntry.getDestination());
					LOG.debug("  <<< getDevice >>> {} ::     {}", i, logEntry.getDevice());
					LOG.debug("  <<< getLogType >>> {} ::    {}", i, logEntry.getLogType());
					LOG.debug("  <<< getOperation >>> {} ::  {}", i, logEntry.getOperation());
					LOG.debug("  <<< getResponse >>> {} ::    {}", i, logEntry.getResponse());
					LOG.debug("  <<< getPayload >>> {} ::    {}", i, logEntry.getPayload());
					LOG.debug("  <<< getServiceCode >>> {} ::    {}", i, logEntry.getServiceCode());
					LOG.debug("  <<< getTimestamp >>> {} ::    {}", i, logEntry.getTimestamp());
					LOG.debug("  <<< getUrl >>> {} ::    {}", i, logEntry.getUrl());
					LOG.debug("  <<< getUser >>> {} ::    {}", i, logEntry.getUser());

					if ("IN_RES".equals(logEntry.getLogType())) {
						LOG.debug("들어옴");
						model.addAttribute("response", (String) logEntry.getResponse().toString());
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					LOG.debug("  <<< e >>>  {}", e);
					break;
				}
				i++;
			}

		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			LOG.debug("<<< LAMP LOG >>>{} ", e);
			LOG.debug(e.getMessage());
		}

		return new ModelAndView("jsonView", model);

	}

	/**
	  * <pre> 
	  * 1. 메소드명 : newApiListAjax 
	  * 2. 작성일 : 2019. 5. 20. 오후 4:49:56 
	  * 3. 작성자 :tt 
	  * 4. 설명 :
	  * </pre>
	  * @throws ApiException
	  */
	@RequestMapping(value = "/newApiListAjax.do")
	public ModelAndView newApiListAjax(@RequestParam HashMap<String, String> requestMap, HttpSession session)
			throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
		
		ModelMap model = new ModelMap();
		ModelAndView mv = new ModelAndView();

		TestCaseListVo testCaseListVo = new TestCaseListVo();
		List<ApiDefVO> list = apiDeployService.selApiList(testCaseListVo);

		model.addAttribute("nlist", list);

		return new ModelAndView("jsonView", model);
	}

	/**
	 * <pre>
	 * 1. 메소드명 : deployRejectAjax
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 : 배포 반려
	 * </pre>
	 *
	 * @throws ApiException
	 */
	@RequestMapping(value = "/deployRejectAjax.do")
	public ModelAndView deployRejectAjax(@RequestBody DeployRejectVo deployRejectVo, HttpSession session) throws ApiException, ParseException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelAndView mv = new ModelAndView();
		ModelMap model = new ModelMap();
		
		Map<String, String> mailMap = new HashMap<String, String>();
		MailVo mailVo = new MailVo();
		
		ApiDeploySearchVo apiDeploySearchVo = new ApiDeploySearchVo();
		ApiDeployInsertVo apiDeployInsertVo = new ApiDeployInsertVo();
		
		DeployApplyVo deployApplyVo = new DeployApplyVo();
		
		try {
			
			deployApplyVo.setComment(deployRejectVo.getRejectMsg());
		
			for(DeployRejectVo listVo : deployRejectVo.getDeployApiList()) {
				LOG.debug("<<<  getDeployapplyseq >>> {}  , getDeployapplyseq >>> {}", listVo.getDeployapplyseq() , listVo.getProcseq());
			
				// Deploy Proc테이블의 정보들을 뽑아온다
				apiDeploySearchVo.setSeq(listVo.getProcseq());
				ApiDeployVO deploySearchMap = apiDeployService.selDeployView(apiDeploySearchVo);

				apiDeployInsertVo.setSeq(listVo.getProcseq());
				apiDeployInsertVo.setDeployCd(ApiDeployResultCode.CD_1065_DEPLOY_APPLY_CODE.getCode());	//-- DEPLOY1065-배포반려
				apiDeployInsertVo.setVerifiCd(deploySearchMap.getVerifiCd());
				apiDeployInsertVo.setRegr(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
				apiDeployInsertVo.setBigo((deploySearchMap.getBigo() == null)? "": deploySearchMap.getBigo());
				apiDeployInsertVo.setUseYn("Y");
				
				deployApplyVo.setDeployAdm(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
				deployApplyVo.setDeployCd(ApiDeployResultCode.CD_1065_DEPLOY_APPLY_CODE.getCode());	//-- DEPLOY1065-배포반려
				deployApplyVo.setSeq(listVo.getDeployapplyseq());
				
				apiDeployService.updateDeployReject(apiDeployInsertVo, deployApplyVo );
					
			}
		
			model.addAttribute("resultCd", ApiDeployResultCode.CD_RETURN_SUCCESS.getCode());
		
		}catch(Exception e) {
			
			model.addAttribute("resultCd", ApiDeployResultCode.CD_RETURN_FAIL.getCode());
		}
		


		//-- { 요청 완료시 메일 발송 
			//mailMap.put("tempId", ApiDeployResultCode.MAIL_TEMPLATE_KOS_CODE.getCode());
			//mailMap.put("title", ApiDeployResultCode.MAIL_TITLE_1065.getMessage());
			//mailMap.put("content", deployRejectVo.getRejectMsg().replaceAll("(\r\n|\r|\n|\n\r)", "<br>"));
			//mailVo.setAutNm(ApiDeployResultCode.MAIL_MBR_KOS_DEVELOPER.getMessage());  //메일 수신자 그룹명을 세팅해 준다. 수신자 그룹 ID는 환경마다 다름
			//mailVo.setMailSendCode(ResultCode.CD_1065_DEPLOY_APPLY_CODE.getCode());
			
			//apiDeployService.mailSend(mailVo, mailMap);
		// -- }



		
		return new ModelAndView("jsonView", model);
	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvDrmTest
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt 
	 * 4. 설명 :
	 * </pre>
	 * 
	 * @throws ApiException
	 */
	@RequestMapping(value = "/mvDrmTest.do")
	public ModelAndView mvDrmTest(@RequestParam HashMap<String, String> requestMap, HttpSession session)
			throws Exception {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(),Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelMap model = new ModelMap();
		String returnCd = "200";
		try{
			String serviceGb = requestMap.get("serviceGb").toString();
			LOG.debug("<<< serviceGb >>> {}", serviceGb);
			if (serviceGb.equals(ApiDeployResultCode.CD_1010_DEPLOY_APPLY_CODE.getCode())) {	//-- DEPLOY1010-TB배포전
				LOG.debug("  <<< procInsert Start >>>  ");
				ApiDeployInsertVo apiDeployInsertVo = new ApiDeployInsertVo();

				// --{ 샘플 데이트 및 실제 serviceg호출
				// -- 배포 기본정보 등록
				// apiDeployInsertVo.setApiNo("15894");
				apiDeployInsertVo.setApiNo(requestMap.get("apiNo"));
				apiDeployInsertVo.setProcessGubun("insert");
				apiDeployInsertVo.setRegr(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));

				returnCd = apiDeployService.callPrivateProcessBaseInsert(apiDeployInsertVo);
				// --}
			} else if (serviceGb.equals(ApiDeployResultCode.CD_1020_DEPLOY_APPLY_CODE.getCode())) {	//-- DEPLOY1020-TB배포완료
				LOG.debug("  <<< tbDeploySuccess Start >>>  ");

				ApiDeployInsertVo apiDeployInsertVo = new ApiDeployInsertVo();
				DeployHstVo deployHstVo = new DeployHstVo();

				// --{ 샘플데이터 및 실제 serviceg호출 ApiDeployInsertVo
				// ex) apiDeployInsertVo.setDeployProcSeq(11) -- (int)apiDeployInsertVo.setRegr("0001M7HGVS7AwV401M4R/0xqmg=="); -- (String) SafeDb 암호화한 data
				// apiDeployInsertVo.setDeployAdm("0001M7HGVS7AwV401M4R/0xqmg=="); -- 로그인한 아이디
				// deployHstVo.setDeployAdm("0001M7HGVS7AwV401M4R/0xqmg=="); -- 로그인한 아이디'
				// deployHstVo.setResultCd -- 연동 하여 전달 받은 결과 코드 값 (String)
				// deployHstVo.setResultMsg -- 연동 하여 전달 받은 결과 메세지 값 (String)
				// deployHstVo.setDeployProcSeq(1212); -- tb일경우 Proc seq를 입력해 준다
				// apiDeployInsertVo.setSeq(23);

				apiDeployInsertVo.setSeq(Integer.parseInt(requestMap.get("procSeq")));

				apiDeployInsertVo.setRegr(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
				apiDeployInsertVo.setDeployAdm(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
				apiDeployInsertVo.setTbProSuccessYn("Y");

				deployHstVo.setDeployAdm(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
				deployHstVo.setResultCd("000");
				deployHstVo.setResultMsg("SUCCESS");
				deployHstVo.setDeployProcSeq(Integer.parseInt(requestMap.get("procSeq")));

				returnCd = apiDeployService.callPrivateTbDeploy(apiDeployInsertVo, deployHstVo);
				// --}
			} else if (serviceGb.equals(ApiDeployResultCode.CD_1013_DEPLOY_APPLY_CODE.getCode())) {	//-- DEPLOY1013-TB배포실패
				LOG.debug("  <<< tbDeployFail Start >>>  ");

				ApiDeployInsertVo apiDeployInsertVo = new ApiDeployInsertVo();
				DeployHstVo deployHstVo = new DeployHstVo();

				apiDeployInsertVo.setSeq(Integer.parseInt(requestMap.get("procSeq")));
				apiDeployInsertVo.setRegr(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
				apiDeployInsertVo.setDeployAdm(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
				apiDeployInsertVo.setTbProSuccessYn("N");

				deployHstVo.setDeployAdm(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
				deployHstVo.setResultCd("999");
				deployHstVo.setResultMsg("FAIL");
				deployHstVo.setDeployProcSeq(Integer.parseInt(requestMap.get("procSeq")));

				returnCd = apiDeployService.callPrivateTbDeploy(apiDeployInsertVo, deployHstVo);
			} else if (serviceGb.equals(ApiDeployResultCode.CD_1030_DEPLOY_APPLY_CODE.getCode())) {	//-- DEPLOY1030-검증시작
				LOG.debug("  <<< verification Start >>>  ");

				// --{
				// ex) apiDeploySearchVo.setVerifiCd("VERIFI1020") 검증중 VERIFI1020 검증 완료 VERIFI1030
				// apiDeploySearchVo.setSeq(23) 프로세스 테이블의seq
				// apiDeploySearchVo.setRegr("0001M7HGVS7AwV401M4R/0xqmg==");
				// apiDeploySearchVo.setDeployCd("DEPLOY1030");

				ApiDeploySearchVo apiDeploySearchVo = new ApiDeploySearchVo();

				apiDeploySearchVo.setVerifiCd("VERIFI1020");
				// apiDeploySearchVo.setSeq(23);
				apiDeploySearchVo.setSeq(Integer.parseInt(requestMap.get("procSeq")));
				apiDeploySearchVo.setRegr(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
				apiDeploySearchVo.setDeployCd(serviceGb);	//-- DEPLOY1030-검증시작

				returnCd = apiDeployService.callPrivateVerifiProc(apiDeploySearchVo);
				// --}
			} else if ("VERIFIS".equals(serviceGb)) {
				LOG.debug("  <<< verification HST INSERT >>>  ");

				VerifiResultVo verifiResultVo = new VerifiResultVo();
				// ex) verifiResultVo.getDeployProcSeq(23) 검증중 VERIFI1020 검증 완료 VERIFI1030
				// verifiResultVo.setResultCd("000");
				// verifiResultVo.setResultMsg("SUCCESS")
				// verifiResultVo.setSuccessYn("Y")

				// verifiResultVo.setDeployProcSeq(23);
				verifiResultVo.setDeployProcSeq(Integer.parseInt(requestMap.get("procSeq")));
				verifiResultVo.setResultCd("000");
				verifiResultVo.setResultMsg("SUCCESS");
				verifiResultVo.setSuccessYn("Y");
				verifiResultVo.setVerifiUsr(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
				//verifiResultVo.setTestCaseSeq(Integer.parseInt(requestMap.get("testCaseSeq")));

				// --{ 검증 성공 이력 입력
				returnCd = apiDeployService.callPrivateVerifiHst(verifiResultVo);
				// --}
			} else if ("VERIFIF".equals(serviceGb)) {
				LOG.debug("  <<< verification HST INSERT >>>  ");

				VerifiResultVo verifiResultVo = new VerifiResultVo();
				verifiResultVo.setDeployProcSeq(Integer.parseInt(requestMap.get("procSeq")));
				verifiResultVo.setResultCd("999");
				verifiResultVo.setResultMsg("FAIL");
				verifiResultVo.setSuccessYn("N");
				verifiResultVo.setVerifiUsr(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
				//verifiResultVo.setTestCaseSeq(Integer.parseInt(requestMap.get("testCaseSeq")));
				verifiResultVo.setTestCaseSeq(requestMap.get("testCaseSeq"));

				// --{ 검증 실패 이력 입력
				returnCd = apiDeployService.callPrivateVerifiHst(verifiResultVo);
				// --}
			} else if (serviceGb.equals(ApiDeployResultCode.CD_1040_DEPLOY_APPLY_CODE.getCode())) {	//-- DEPLOY1040-검증완료
				ApiDeploySearchVo apiDeploySearchVo = new ApiDeploySearchVo();

				apiDeploySearchVo.setVerifiCd("VERIFI1030");
				// apiDeploySearchVo.setSeq(23);
				apiDeploySearchVo.setSeq(Integer.parseInt(requestMap.get("procSeq")));
				apiDeploySearchVo.setRegr(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
				apiDeploySearchVo.setDeployCd(serviceGb);	//-- DEPLOY1040-검증완료

				returnCd = apiDeployService.callPrivateVerifiProc(apiDeploySearchVo);
			} else if (serviceGb.equals(ApiDeployResultCode.CD_1070_DEPLOY_APPLY_CODE.getCode())) {	//-- DEPLOY1070-배포완료
				LOG.debug("### 상용배포 성공 ###");
				try {
					ApiDeploySearchVo apiDeploySearchVo = new ApiDeploySearchVo();
					DeployApplyVo deployApplyVo = new DeployApplyVo();
					DeployHstVo deployHstVo = new DeployHstVo();

					apiDeploySearchVo.setSeq(Integer.parseInt(requestMap.get("procSeq")));
					apiDeploySearchVo.setDeployCd(serviceGb);	//-- 1070-배포완료
					apiDeploySearchVo.setRegr(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));

					deployApplyVo.setCbProSuccessYn("Y");
					// deployApplyVo.setSeq(9);

					deployApplyVo.setSeq(Integer.parseInt(requestMap.get("deployApplySeq")));

					deployHstVo.setResultCd("000");// GW로 부터 전달 받은 코드값
					deployHstVo.setResultMsg("SUCCESS");// GW로 부터 전달 받은 코드값

					returnCd = apiDeployService.callPrivateCbDeploySuccess(deployApplyVo, apiDeploySearchVo, deployHstVo);
				}catch(Exception e) {
					LOG.debug(e.getMessage());
					returnCd = "999";
				}
			} else if (serviceGb.equals(ApiDeployResultCode.CD_1063_DEPLOY_APPLY_CODE.getCode())) {	//-- DEPLOY1063-배포실패
				ApiDeploySearchVo apiDeploySearchVo = new ApiDeploySearchVo();
				DeployApplyVo deployApplyVo = new DeployApplyVo();
				DeployHstVo deployHstVo = new DeployHstVo();

				// apiDeploySearchVo.setSeq(23);
				LOG.debug("<<< procSeq >>>");
				apiDeploySearchVo.setSeq(Integer.parseInt(requestMap.get("procSeq")));

				apiDeploySearchVo.setDeployCd(serviceGb);	//-- DEPLOY1063-검증실패
				apiDeploySearchVo.setRegr(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));

				deployApplyVo.setCbProSuccessYn("N");
				deployApplyVo.setSeq(Integer.parseInt(requestMap.get("deployApplySeq")));

				deployHstVo.setResultCd("999");// GW로 부터 전달 받은 코드값
				deployHstVo.setResultMsg("FAIL");// GW로 부터 전달 받은 코드값

				returnCd = apiDeployService.callPrivateCbDeploySuccess(deployApplyVo, apiDeploySearchVo, deployHstVo);
			} else if ("TESTCASE".equals(serviceGb)) {
				TestCaseListVo testCaseListVo = new TestCaseListVo();
				// apiDeploySearchVo.setSeq(23);
				testCaseListVo.setApiNo(Integer.parseInt(requestMap.get("apiNo")));
				testCaseListVo.setCaseCd(requestMap.get("testCaseCd")); // 상용 배포 성공
				LOG.debug("!@@! mbrId =>  {}", session.getAttribute("mbrId").toString());
				testCaseListVo.setRegr(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
				testCaseListVo.setCaseNm(requestMap.get("caseNm"));

				returnCd = apiDeployService.insertTestCaseList(testCaseListVo);
			}
		}catch(Exception e) {
			LOG.debug(e.getMessage());
		}
		
		model.addAttribute("returnCode", returnCd);
		return new ModelAndView("jsonView", model);
	}

	/**
	 * <pre>
	 * 1. 메소드명 : testCaseInser
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56
	 * 3. 작성자 : tt
	 * 4. 설명 : 테스트 케이스 입력 처리
	 * </pre>
	 * 
	 * @throws ApiException
	 */
	@RequestMapping(value = "/testCaseInser.do")
	public ModelAndView testCaseInser(HttpSession session) throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelAndView mv = new ModelAndView();


		VerifiResultVo verifiResultVo = new VerifiResultVo();
		TestCaseListVo vo = new TestCaseListVo();

		vo.setApiNo(CommonFunc.getSession("apiNo", session));

		List<ApiTestCaseVO> list = apiDeployService.selTescCaseResult(vo);

		LOG.debug("  <<< list.SIZE >>>  {}", list.size());

		Iterator<ApiTestCaseVO> itr = list.iterator();

		// 사용자 정보를 복호화 하여 저장
		while (itr.hasNext()) {
			LOG.debug("  <<<  >>> ");
			ApiTestCaseVO vo2 = itr.next();
			try {

				LOG.debug("  <<< TEST CASE SEQ >>>  {}", vo2.getTestCaseSeq());

				verifiResultVo.setDeployProcSeq(CommonFunc.getSession("procSeq", session));
				verifiResultVo.setResultCd("000");
				verifiResultVo.setResultMsg("SUCCESS");
				verifiResultVo.setSuccessYn("Y");
				verifiResultVo.setVerifiUsr(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
				verifiResultVo.setTestCaseSeq(vo2.getTestCaseSeq().toString());

				// 검증 성공 이력 입력
				String returnCd = apiDeployService.callPrivateVerifiHst(verifiResultVo);

				Thread.sleep(1000);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOG.debug("  <<< e >>>  {}", e);
				break;
			}

		}
		// ex) verifiResultVo.getDeployProcSeq(23) 검증중 VERIFI1020 검증 완료 VERIFI1030
		// verifiResultVo.setResultCd("000");
		// verifiResultVo.setResultMsg("SUCCESS")
		// verifiResultVo.setSuccessYn("Y")

		// verifiResultVo.setDeployProcSeq(23);

		return mv;
	}

	/**
	 * <pre> 
	 * 1. 메소드명 : updateVerifiAjax 
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56 
	 * 3. 작성자 : tt
	 * 4. 설명 : 검증 완료로 프로세스 업데이트 
	 * </pre>
	 * 
	 * @throws ApiException
	 */
	@RequestMapping(value = "/updateVerifiAjax.do")
	public ModelAndView updateVerifiAjax(HttpSession session) throws Exception {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelAndView mv = new ModelAndView();

		ApiDeploySearchVo apiDeploySearchVo = new ApiDeploySearchVo();

		apiDeploySearchVo.setVerifiCd("VERIFI1030");
		// apiDeploySearchVo.setSeq(23);
		apiDeploySearchVo.setSeq(CommonFunc.getSession("procSeq", session));
		apiDeploySearchVo.setRegr(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
		apiDeploySearchVo.setDeployCd(ApiDeployResultCode.CD_1040_DEPLOY_APPLY_CODE.getCode());	//-- DEPLOY1040-검증완료

		String returnCd = apiDeployService.callPrivateVerifiProc(apiDeploySearchVo);

		return mv;

	}
	
	/**
	 * <pre> 
	 * 1. 메소드명 : verifyStartAjax 
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56 
	 * 3. 작성자 : tt
	 * 4. 설명 : 검증 중
	 * </pre>
	 * 
	 * @throws ApiException
	 */
	@RequestMapping(value = "/verifyStartAjax.do")
	public ModelAndView verifyStartAjax(@RequestParam HashMap<String, String> requestMap , HttpSession session) throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		ModelMap model = new ModelMap();
		
		ApiDeploySearchVo apiDeploySearchVo = new ApiDeploySearchVo();
		String returnCd = ApiDeployResultCode.CD_RETURN_SUCCESS.getCode();
		
		try {
			
			apiDeploySearchVo.setVerifiCd(ApiDeployResultCode.CD_1020_VERIFI_BASE_CODE.getCode());
			apiDeploySearchVo.setSeq(Integer.parseInt(requestMap.get("procSeq")));
			apiDeploySearchVo.setRegr(CommonFunc.safeDbEncrypt(session.getAttribute("mbrId").toString()));
			apiDeploySearchVo.setDeployCd(ApiDeployResultCode.CD_1030_DEPLOY_APPLY_CODE.getCode());	//-- 1030-검증시작
			
			returnCd = apiDeployService.callPrivateVerifiProc(apiDeploySearchVo);
			
			if(!"000".equals(returnCd)) {
				returnCd =  ApiDeployResultCode.CD_RETURN_FAIL.getCode();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.debug(e.getMessage());
			returnCd =  ApiDeployResultCode.CD_RETURN_FAIL.getCode();
		}
		
		model.addAttribute("returnCd", returnCd);
		
		return new ModelAndView("jsonView", model);
		

	}
	
	/**
	 * <pre> 
	 * 1. 메소드명 : mvProcDelApiSearchAjax 
	 * 2. 작성일 : 2019. 8. 21
	 * 3. 작성자 :tt 
	 * 4. 설명 : API명으로 API NO조회
	 * </pre>
	 * 
	 * @throws ApiException
	 */
	@RequestMapping(value = "/mvProcDelApiSearchAjax.do")
	public ModelAndView mvProcDelApiSearchAjax(@RequestParam HashMap<String, String> requestMap, HttpSession session) throws ApiException {
		
		ModelMap model = new ModelMap();
		ModelAndView mv = new ModelAndView();
		ApiDeploySearchVo apiDeploySearchVo = new ApiDeploySearchVo();
		
		apiDeploySearchVo.setApiNm(requestMap.get("apiNm"));
		
		List<ApiDefVO> apiDefList = apiDeployService.selDelApiSearch(apiDeploySearchVo);

		model.addAttribute("nlist", apiDefList);
		
		return new ModelAndView("jsonView", model);
	}
	
	/**
	 * <pre> 
	 * 1. 메소드명 : mvProcDelAjax 
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56 
	 * 3. 작성자 :tt 
	 * 4. 설명 :
	 * </pre>
	 * 
	 * @throws ApiException
	 */
	@RequestMapping(value = "/mvProcDelAjax.do")
	public ModelAndView mvProcDelAjax(@RequestParam HashMap<String, String> requestMap, HttpSession session) throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
		
		ModelMap model = new ModelMap();
		ModelAndView mv = new ModelAndView();
		
		DeployApplyVo deployApplyVo = new DeployApplyVo();
		int returnValue = 0;
	
		String a =  requestMap.get("apiNo");
		
		deployApplyVo.setDeployProcSeq(Integer.parseInt(requestMap.get("deployProcSeq")));
		deployApplyVo.setApiNo(Integer.parseInt(requestMap.get("apiNo")));

		
		returnValue = apiDeployService.delDeployProc(deployApplyVo);
		
		if(returnValue > 0){
			model.addAttribute("returnCd", ApiDeployResultCode.CD_RETURN_SUCCESS.getCode());
		}else{
			model.addAttribute("returnCd", ApiDeployResultCode.CD_RETURN_FAIL.getClass());
		}

		return new ModelAndView("jsonView", model);
	}
	
	
	
	/**
	 * <pre> 
	 * 1. 메소드명 : mvProcDelApi 
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56 
	 * 3. 작성자 :tt 
	 * 4. 설명 :
	 * </pre>
	 * 
	 * @throws ApiException
	 */
	@RequestMapping(value = "/mvProcDelApi.do")
	public ModelAndView mvProcDelApi(@RequestParam HashMap<String, String> requestMap, HttpSession session) throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
		
		ModelMap model = new ModelMap();
		
		DeployApplyVo deployApplyVo = new DeployApplyVo();
		int returnValue = 0;
	
		
		deployApplyVo.setApiNo(Integer.parseInt(requestMap.get("apiNo")));
		
		returnValue = apiDeployService.delDeployApi(deployApplyVo);
		
		if(returnValue > 0){
			model.addAttribute("returnCd", ApiDeployResultCode.CD_RETURN_SUCCESS.getCode());
		}else{
			model.addAttribute("returnCd", ApiDeployResultCode.CD_RETURN_FAIL.getClass());
		}


		return new ModelAndView("jsonView", model);
	}
	
	/**
	 * <pre> 
	 * 1. 메소드명 : mvDeployApply 
	 * 2. 작성일 : 2019. 5. 20. 오후 4:49:56 
	 * 3. 작성자 :tt 
	 * 4. 설명 :
	 * </pre>
	 * 
	 * @throws ApiException
	 */
	@RequestMapping(value = "/mvDeployApplyDel.do")
	public ModelAndView mvDeployApply(@RequestParam HashMap<String, String> requestMap, HttpSession session) throws ApiException {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
		
		ModelMap model = new ModelMap();
		
		DeployApplyVo deployApplyVo = new DeployApplyVo();
		int returnValue = 0;
	
		
		deployApplyVo.setSeq(Integer.parseInt(requestMap.get("seq")));
		
		returnValue = apiDeployService.delDeployApply(deployApplyVo);
		
		if(returnValue > 0){
			model.addAttribute("returnCd", ApiDeployResultCode.CD_RETURN_SUCCESS.getCode());
		}else{
			model.addAttribute("returnCd", ApiDeployResultCode.CD_RETURN_FAIL.getClass());
		}


		return new ModelAndView("jsonView", model);
	}
	
	/**
	 * <pre> 
	 * 1. 메소드명 : mvProcDeployApplySearchAjax 
	 * 2. 작성일 : 2019. 8. 21 
	 * 3. 작성자 :tt 
	 * 4. 설명 : API명으로 API NO조회
	 * </pre>
	 * 
	 * @throws ApiException
	 */
	@RequestMapping(value = "/mvProcDeployApplySearchAjax.do")
	public ModelAndView mvProcDeployApplySearchAjax(@RequestParam HashMap<String, String> requestMap, HttpSession session) throws ApiException {
		
		ModelMap model = new ModelMap();
		ModelAndView mv = new ModelAndView();
		ApiDeploySearchVo apiDeploySearchVo = new ApiDeploySearchVo();
		
		apiDeploySearchVo.setApiNm(requestMap.get("apiNm"));
		
		List<ApiDeployApplyVO> deployApplyList = apiDeployService.selDeployApplySearch(apiDeploySearchVo);

		model.addAttribute("nlist", deployApplyList);

		return new ModelAndView("jsonView", model);
	}

	/**
	 * <pre>
	 * 1. 메소드명 : mvProcDeployStateAjax
	 * 2. 작성일 : 2019. 8. 21
	 * 3. 작성자 :tt
	 * 4. 설명 :
	 * </pre>
	 *
	 * @throws ApiException
	 */
	@RequestMapping(value = "/mvProcDeployStateAjax.do")
	public ModelAndView mvProcDeployStateAjax(@RequestParam HashMap<String, String> requestMap, HttpSession session) throws ApiException {

		ModelMap model = new ModelMap();
		ModelAndView mv = new ModelAndView();
		ApiDeploySearchVo apiDeploySearchVo = new ApiDeploySearchVo();

		List<ApiDeployStateVO> deployStateList = apiDeployService.selDeployStateTotal(apiDeploySearchVo);

		model.addAttribute("nlist", deployStateList);
		
		return new ModelAndView("jsonView", model);
	}
	
	//apiVerNo 수정 (재식)
	@RequestMapping(value = "/apiVerNoUpdateAjax.do")
	public ModelAndView apiVerNoUpdateAjax(@RequestParam String apiNo, @RequestParam String setApiVerNo, HttpServletRequest request) throws ApiException {
		
		ModelAndView mv = new ModelAndView();
		
		HashMap<String, String> apiVerNoUpdateInfo =new HashMap<>();
		apiVerNoUpdateInfo.put("apiNo", apiNo);
		apiVerNoUpdateInfo.put("setApiVerNo", setApiVerNo.replaceAll(" ", ""));
		
		int result = apiDeployService.apiVerNoUpdate(apiVerNoUpdateInfo);
					
		mv.setViewName("jsonView");
		mv.addObject("result", result);
		
		return mv;
	}
	
}