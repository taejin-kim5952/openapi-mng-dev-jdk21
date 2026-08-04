package com.kt.openapi.web.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksm.apisdk.KsmApiClient;
import com.ksm.apisdk.KsmShubApiClient;
import com.kt.openapi.fwk.online.page.Pagination;
import com.kt.openapi.web.adptran.util.KsmUtil;
import com.kt.openapi.web.api.dao.ApiRegDAO;
import com.kt.openapi.web.api.service.ApiRegService;
import com.kt.openapi.web.api.vo.ApiCategoryVO;
import com.kt.openapi.web.api.vo.ApiDefVO;
import com.kt.openapi.web.api.vo.ApiRegVO;
import com.kt.openapi.web.auth.vo.AuthVO;
import com.kt.openapi.web.beast.service.BeastService;
import com.kt.openapi.web.cmm.service.CmnService;
import com.kt.openapi.web.mypage.service.MypageService;
import com.kt.openapi.web.mypage.vo.MypageVO;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import com.kt.openapi.web.util.CommonFunc;
import com.kt.openapi.web.util.MessageUtil;
import com.kt.openapi.web.util.SendMailUtil;
import io.swagger.util.Yaml;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.text.StringEscapeUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
@RequestMapping(value="/api/reg")
public class ApiRegController {
	
	private static final Logger LOG = LoggerFactory.getLogger(ApiRegController.class);
	
	static KsmShubApiClient ksmShubApiClient = new KsmShubApiClient();
	
	//메일발송을 위한 SendMailUtil
	@Autowired
	private SendMailUtil sendMailUtil;
	
	//APILink 메일주소
	@Value("${mail.receiver.apilink}")
	private String apiLinkMail;
	
	@Autowired
	@Qualifier("apiRegService")
	private ApiRegService apiRegService;

	@Value("${pageUnit:10}")
	private int pageUnit;

	@Value("${pageSize:10}")
	private int pageSize;

	@Autowired
	@Qualifier("CmnService")
	protected CmnService cmnService;

	//-- [tag:20200913][add]
    @Autowired
    private ApiRegDAO apiRegDAO;

	//-- [tag:job-20200420][add]
	@Value("${apisystem.sysid.arsenal}")
	private String apisystemSysidArsenal;
	
	//-- [tag:job-20210515][add]
	@Value("${apisystem.sysid.biznaru}")
	private String apisystemSysidBiznaru;
	
	//-- ADD CYD:20200506
	@Value("${gitlab.arsenal.host}")
	private String gitlabArsenalHost;
	
	//-- ADD CYD:20200506
	@Value("${gitlab.arsenal.base.path}")
	private String gitlabArsenalBasePath;
	
	/*
	 * 시스템목록정보를 가져오기 위한 서비스
	 * CYD - 2020.0.13
	 */
	@Autowired
	@Qualifier("mypageService")
	private MypageService mypageService;
	
	//-- ADD CYD:20200506
	@Value("${gitlab.arsenal.token}")
	private String gitlabArsenalAccessToken;
	
	//-- ADD CYD:20200506
	@Value("${gitlab.private.token}")
	private String gitlabArsenalPrivateToken;
	
	//SHUB API명 체크 URL
	@Value("${gateway.api.nameCheckURL}")
	private String apiNameCheckURL;

	//-- [tag:PRJ-20220901]
	@Autowired
	@Qualifier("beastService")
	private BeastService beastService;

	/**
	* <pre>
	* 1. 메소드명 : mvApiReg
	* 2. 작성일 : 2017. 11. 10. 오후 2:46:25
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : API 기본정보 등록 페이지로 이동
	* </pre>
	* @param request
	* @param response
	* @param model
	* @return
	* @throws Exception
	*/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/mvApiInfoReg.do")
	public ModelAndView mvApiInfoReg(HttpSession session, HttpServletRequest request,HttpServletResponse response,  ModelMap model, ApiRegVO apiRegVO) throws Exception {
		LOG.debug("#######################  ApiRegController mvApiReg START ############################");
		
		String isAuthYn = "N";
		ModelAndView mv = new ModelAndView();

		//setSession(session);
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		
		// api 인증타입
		mv.addObject("authTypeList", cmnService.selComnList("AUTTYP1000"));
		// http 스키마
		mv.addObject("httpScmList", cmnService.selComnList("HTPSCM1000"));
		// contentType 
		mv.addObject("cntTypeList", cmnService.selComnList("CNTTYP1000"));
		// 인증 그룹 
		mv.addObject("authGrnList", cmnService.selComnList("AUTGRN1000"));
		// api 구분
		mv.addObject("apiCatList", cmnService.selComnList("APIGUB1000"));
		
		LOG.debug(" apiRegVO.getApiSpcNo() ========== {} " , apiRegVO.getApiSpcNo());
		LOG.debug(" apiRegVO.getSysId() ========== {} " , apiRegVO.getSysId());
		
		if( ( apiRegVO.getApiSpcNo()!=null && !apiRegVO.getApiSpcNo().equals("")) ||	//  수정 화면
				( apiRegVO.getRfrnTmpltNo()!=null && !apiRegVO.getRfrnTmpltNo().equals("")) || 	// TEMPLET 번호
				( apiRegVO.getRfrnApiSpcNo()!=null && !apiRegVO.getRfrnApiSpcNo().equals("")) || // API 번호
				( apiRegVO.getImportYn()!=null && !apiRegVO.getImportYn().equals("")) // 외부파일 불러오기
				) {
			HashMap infoMap = new HashMap();
			
			if(apiRegVO.getRfrnTmpltNo()!=null && !apiRegVO.getRfrnTmpltNo().equals("")) {
				apiRegVO.setApiSpcNo(apiRegVO.getRfrnTmpltNo());
				
			}else if(apiRegVO.getRfrnWsdlUrl()!=null && !apiRegVO.getRfrnWsdlUrl().equals("")) {
				apiRegVO.setApiSpcNo(apiRegVO.getRfrnWsdlUrl());
				
			}else if(apiRegVO.getRfrnApiSpcNo()!=null && !apiRegVO.getRfrnApiSpcNo().equals("")) {
				apiRegVO.setApiSpcNo(apiRegVO.getRfrnApiSpcNo());
			}else if(apiRegVO.getImportYn()!=null && !apiRegVO.getImportYn().equals("")) {
				infoMap.put("regSttusCd", "APIREG1010");
				infoMap.put("regSttusNm", "작성중");
				infoMap.put("yamlSbst", apiRegVO.getYamlSbst());
			}
			else {	//-- 수정
			}
			
			if(apiRegVO.getImportYn()!=null && !apiRegVO.getImportYn().equals("")) {
				mv.addObject("info", infoMap);
			} else {
				try {
					if(apiRegVO.getSysId() == null) {
						apiRegVO.setSysId("");
					}
					ApiDefVO map_apiSpc = apiRegService.selApiInfo(apiRegVO);
					mv.addObject("info", map_apiSpc);
					
					if(apiRegVO.getSysId().equalsIgnoreCase(this.apisystemSysidArsenal)) {
						String projectNamespace = map_apiSpc.getProjectNamespace();
						session.setAttribute("projectNamespace", projectNamespace);
						LOG.debug(" Session projectNamespace ========== {} ", projectNamespace);
					}
					
					/*
					** 권한체크기준
					**  1. 등록한 사용자
					**  2. 수정권한이 있는 사용자(관리자 및 운영자가 부여)
				    **  
					** 위 두가지 조건을 제외한 나머지 사용자들은 수정불가
					** Y:권한있음, N:권한없음
					**
					** CYD - 2020.07.14
					*///////////////////////////////////////////////
					//mv.addObject("isAuthYn", "N");
					apiRegVO.setRegr(userJVo.getEnCmbrId());
					LOG.debug("{}.{} REGR ========== {} ", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), apiRegVO.getRegr());
					int authCount = apiRegService.selApiSpcAuthCheck(apiRegVO);
					LOG.debug("{}.{} Auth Count ========== {} ", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), authCount);
					//int adminCount = apiRegService.selMbrAuthCheck(apiRegVO);
					//LOG.debug("{}.{} Admin Count ========== {} ", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), adminCount);
					if(map_apiSpc.getRegr().equalsIgnoreCase(userJVo.getEnCmbrId())
						|| apiRegVO.getSysId().equalsIgnoreCase(this.apisystemSysidArsenal)
						|| authCount > 0) {
						isAuthYn = "Y";
					}
					////////////////////////////////////////////////
					
				} catch (Exception e) {
					LOG.debug("{}.{} Exception ========== {} " , getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getStackTrace());
				}
			}
			
			//-- [tag:SR-20210222][cmt][i][cateInfo, dataTypeInfo deprecated]
			//--##mv.addObject("cateInfo", apiRegService.selCateList(apiRegVO));
			//--##mv.addObject("dataTypeInfo", apiRegService.selApiDataTypeUseList(apiRegVO));
			// 아스날용 Gitlab 서버호스트 및 기본패스 CYD - 20200508
			mv.addObject("gitlabArsenalHost"	, gitlabArsenalHost);
			mv.addObject("gitlabArsenalBasePath", gitlabArsenalBasePath);
			
		}
		
		/*
		 * Obsever권한자는 전체시스템목록 할당하고 아닌 경우에는 신청한 권한 시스템 목록 할당
		 * CYD - 2020.07.14
		 */
		if ("Y".equalsIgnoreCase(userJVo.getObserverYn())) {
			List<Map<String, Object>> sysSelectBox = mypageService.selboxSysNm(null);
			mv.addObject("authList", sysSelectBox);
		}
		else {
			mv.addObject("authList", userJVo.getAuthList());
		}
		
		LOG.debug(" Session Auth List ========== {} ", userJVo.getAuthList());
		
		// 권한 세션 셋업
		session.setAttribute("sIsAuthYn", isAuthYn);
		model.addAttribute("sIsAuthYn", isAuthYn);

		mv.setViewName("api/infoRegForm");
		return mv;
	}

	//배포 프로세스 등록요청 메일발송(재식 추가)
	@RequestMapping(value="/mailSendStart.do")
	public ModelAndView mailSendStartAjax(HttpSession session,@RequestParam(value = "apiId", required = false) String apiId,
			@RequestParam(value = "apiNm", required = false) String apiNm) throws Exception {
		
		LOG.debug("####################### ApiRegController mailSendStartAjax START ############################");
		
		ModelAndView mv= new ModelAndView();
		UserJoinVO userJoinVO = (UserJoinVO)session.getAttribute("ssUserVo");
		
		LOG.debug(" session userJoinVO ========== {} " , userJoinVO);
		
		//메일 발송 내용을 담기위한 map
		Map<String,String> map = new HashMap<>();
				
		//apiId, apiNm을 담기위한 vo
		ApiRegVO vo = new ApiRegVO();

		vo.setApiNm(apiNm);
		vo.setApiId(apiId);
		
		//apiId, apiNm으로 저장된 API인지 조회
		ApiDefVO resultInfo = apiRegService.selectApiNmNoCheck(vo);
		
		//조회 결과가 없다면
		if(resultInfo == null) {
			
			LOG.debug(" ApiDefVO resultInfo null ========== {} " , resultInfo);
			
			mv.addObject("message", "저장되지않은 API입니다.");
			mv.setViewName("jsonView");
			
		}	
		//조회결과가 있다면
		else{ 
			
			LOG.debug(" ApiDefVO resultInfo not null ========== {} " , resultInfo);
			
			//변수에 API명과 API번호를 담는다. (Getter 사용)
			String checkApiNm = resultInfo.getApiNm();
			String checkApiNo = resultInfo.getApiNo();
			
			LOG.debug(" resultInfo.getApiNm() ========== {} " , checkApiNm);
			LOG.debug(" resultInfo.getApiNo() ========== {} " , checkApiNo);
			
			//메일 발송내용에 추가 할 멤버 정보
			String memberName = userJoinVO.getMbrNm();
			String memberId = userJoinVO.getMbrId();
			
			//프로세스 등록 된 API인지 확인 (0이 아니면 이미 배포 포로세스 등록 된 API)
			int apiNoCountCheck = apiRegService.selectApiNoCount(checkApiNo);
			
			if(apiNoCountCheck != 0) {
				mv.addObject("message","이미 배포 프로세스 등록된 API입니다.");
				mv.setViewName("jsonView");
			}
			// 배포 프로세스 미등록 된 API일 경우 map에 정보 추가 후 sendMailUtil을 사용하여 관리자에게 메일 발송
			else {
				map.put("tempId", "100000014");
				map.put("title", "API 배포 프로세스 등록요청");
				map.put("content", memberName+"("+memberId+")님께서 "+checkApiNm+" API 배포 프로세스 등록요청을 하였습니다.(API_NO: "+checkApiNo+")");
				map.put("toMail", apiLinkMail);
				sendMailUtil.sendMailcall(map);	
				mv.addObject("message","관리자에게 배포 프로세스 등록요청 하였습니다.");
				mv.setViewName("jsonView");
			}			
		}
				
		return mv;
	}
	
	
	/**
	 * <pre>
	 * 1. 메소드명 : mvApiPathReg
	 * 2. 작성일   : 2017. 11. 21. 오후 9:05:13
	 * 3. 작성자   : JeonGeun Kang
	 * 4. 설명     : API 패스정보 등록 페이지로 이동
	 * </pre>
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	//-- [tag:adpt][drm][add] POST only
	@RequestMapping(value="/mvApiPathReg.do")
	public ModelAndView mvApiPathReg(HttpSession session ,HttpServletRequest request,HttpServletResponse response,  ModelMap model , ApiRegVO apiRegVO) throws Exception {
		LOG.debug("\n\n### {}.{}() [request.getServletPath(): {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), request.getServletPath());
		LOG.debug("#######################  ApiRegController mvApiReg START ############################");
		
		ModelAndView mv = new ModelAndView();
		
		String isAuthYn = "N";
		// 세션가져오기
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		
		LOG.debug(" apiRegVO ==== > [apiSpcNo:{}][getApiCtgryNo:{}][getApiNo:{}] " , apiRegVO.getApiSpcNo(), apiRegVO.getApiCtgryNo(), apiRegVO.getApiNo());
		
		//-- [tag:adpt][drm][add][validation parameter]
		String apiSpcNo = KsmUtil.fnSafeStr(apiRegVO.getApiSpcNo());
		if (apiSpcNo.length() == 0) {
			LOG.error("\n\n### {}.{}() \n\t[redirect:/api/main/mvMainList.do] \n\t[apiSpcNo: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), apiSpcNo);
			return (ModelAndView) new ModelAndView("redirect:/api/main/mvMainList.do");	// API등록
		}
		
		// http 스키마
		mv.addObject("httpScmList", cmnService.selComnList("HTPSCM1000"));
		// contentType 
		mv.addObject("cntTypeList", cmnService.selComnList("CNTTYP1000"));
		// 인증 그룹 
		mv.addObject("authGrnList", cmnService.selComnList("AUTGRN1000"));
		// 메소드 리스트
		mv.addObject("mthTypeList", cmnService.selComnList("MTHTYP1000")); // apiRegService.selMethodDupList(apiRegVO)
		// api 인증타입
		mv.addObject("dataTypeList", cmnService.selComnList("DATTYP1000"));
		// api 구분
		mv.addObject("apiCatList", cmnService.selComnList("APIGUB1000"));
		
		// api기본정보
		ApiDefVO map_apiSpc = apiRegService.selApiInfo(apiRegVO);
		//-- [tag:PRJ-20220901]
		ApiCategoryVO map_cateInfo = apiRegService.selCateInfo(apiRegVO);
		//-- [tab:job-20200714][add]
		// api정보(KOA_TB_API_DEF)
		ApiDefVO map_apiDef = ((KsmUtil.fnSafeStr(apiRegVO.getApiNo()).length() > 0) ? apiRegService.selApiDef(apiRegVO) : (new ApiDefVO()));

		String yamlSbst = map_apiSpc.getYamlSbst();
		// yaml 데이터 에서 데이터 유형 값만 조회 시작
		HashMap<String,Object> yamlDataType = apiRegService.selYamlDataType(yamlSbst);
		
		List<HashMap> definitionsList = new ArrayList(); 
		
		// 해당 yaml 에 definitions 가 있을 경우에만 
		if (yamlDataType != null) {
			Set set = yamlDataType.keySet();
			Iterator iterator = set.iterator();
			
			while (iterator.hasNext()) {
			  String key = (String)iterator.next();
			  //System.out.println("key" + key);
			  HashMap definitionsMap = new HashMap();
			  definitionsMap.put("typeNm", key);
			  definitionsList.add(definitionsMap);
			}
		}
		mv.addObject("definitionsList", definitionsList);
		// yaml 데이터 에서 데이터 유형 값만 조회 종료
		
		mv.addObject("info", map_apiSpc);
		//-- [tag:PRJ-20220901]
		mv.addObject("cate", map_cateInfo);
		//--[tab:job-20200714][add]
		mv.addObject("apiDef", map_apiDef);

		//-- [tag:SR-20210222][cmt][i][cateInfo, dataTypeInfo deprecated]
		//--##mv.addObject("cateInfo", apiRegService.selCateList(apiRegVO));
		//--##mv.addObject("dataTypeInfo", apiRegService.selApiDataTypeUseList(apiRegVO));

		//--[tag:adpt][drm][add]
		String apiClass = map_apiSpc.getApiClass();
		boolean isPrivateApi = "APIGUB1020".equals(apiClass);	//-- Public: APIGUB1010, Private: APIGUB1020, Internal: APIGUB1030
		
		/*
		** 권한체크기준
		**  1. 등록한 사용자
		**  2. 수정권한이 있는 사용자(관리자 및 운영자가 부여)
	    **  
		** 위 두가지 조건을 제외한 나머지 사용자들은 수정불가
		** Y:권한있음, N:권한없음
		**
		** CYD - 2020.07.14
		*///////////////////////////////////////////////
		//mv.addObject("isAuthYn", "N");
		apiRegVO.setRegr(userJVo.getEnCmbrId());
		LOG.debug("{}.{} REGR ========== {} ", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), apiRegVO.getRegr());
		int authCount = apiRegService.selApiSpcAuthCheck(apiRegVO);
		LOG.debug("{}.{} Auth Count ========== {} ", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), authCount);
		//int adminCount = apiRegService.selMbrAuthCheck(apiRegVO);
		//LOG.debug("{}.{} Admin Count ========== {} ", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), adminCount);
		if(map_apiSpc.getRegr().equalsIgnoreCase(userJVo.getEnCmbrId()) 
			|| map_apiSpc.getSysId().equalsIgnoreCase(this.apisystemSysidArsenal)
			|| authCount > 0) {
			isAuthYn = "Y";
		}
		
		// 권한 세션 셋업
		session.setAttribute("sIsAuthYn", isAuthYn);
		model.addAttribute("sIsAuthYn", isAuthYn);
		////////////////////////////////////////////////

		//--[tag:job-20200420]
		String sysId = map_apiSpc.getSysId();
		boolean isSysIdArsenal = this.apisystemSysidArsenal.equals(sysId);
		if (true == isSysIdArsenal) {
			mv.setViewName("api/pathRegFormArsenal");
			//-- [tag:job-20200506][CYD]
			mv.addObject("gitlabArsenalHost"	, gitlabArsenalHost);
			mv.addObject("gitlabArsenalBasePath", gitlabArsenalBasePath);
			mv.addObject("projectNamespace"     , (String)session.getAttribute("projectNamespace"));
		}
		else {
			if (true == isPrivateApi) {
				// api handler 구분
				mv.addObject("apiHandlerList", cmnService.selComnList("APIHDR1000"));
				// parameter 민감정보 구분
				mv.addObject("apiPersonalDataList", cmnService.selComnList("PRMPDT1000"));
				//--[tag:job-20210515]
				// biznau systemId여부
				mv.addObject("isSysIdBiznaru", (this.apisystemSysidBiznaru.equals(sysId) ? "Y" : ""));
				//-- [tag:SR-20210711]
				//-- ApiProvider목록검색 // KOA_TB_API_PROVIDER
				mv.addObject("apiProviderList", apiRegService.selApiProviderList());
				
				//-- [tag:PRJ-20220901][i][preloadApiInfo from popSimpleApiReg.jsp]
				String preloadApiInfo = KsmUtil.fnSafeStr(request.getParameter("preloadApiInfo"));
				mv.addObject("preloadApiInfo", preloadApiInfo);
	
				mv.setViewName("api/pathRegFormPrivate");
			}
			else {
				mv.setViewName("api/pathRegForm");
			}
		}
		
		return mv;
	}

	//-- [tag:SR-20210222][add]
	/**
	 * <pre>
	 * 1. 메소드명 : selApiDefAjax
	 * 2. 작성일   :
	 * 3. 작성자   :
	 * 4. 설명     : apiDef 조회
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
	@RequestMapping(value="/selApiDefAjax.do")
	public ModelAndView selApiDefAjax(HttpSession session, HttpServletRequest request, HttpServletResponse response, ModelMap model, ApiRegVO vo) throws Exception {
		LOG.debug("#######################  ApiRegController selApiDefAjax START ############################");

		ApiDefVO map_apiDef = apiRegService.selApiDef(vo);
		model.addAttribute("apiDef", map_apiDef);

		return new ModelAndView( "jsonView", model );
	}
	
	/**
	* <pre>
	* 1. 메소드명 : savApiRegPathAjax
	* 2. 작성일 : 2017. 12. 4. 오후 9:39:21
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : API PATH 저장
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
	@RequestMapping(value="/savApiRegPathAjax.do" )
	public ModelAndView savApiRegPathAjax(HttpSession session ,
			HttpServletRequest request,
			HttpServletResponse response,  
			ModelAndView mv , 
			ApiRegVO vo,
			@RequestParam HashMap<String, String> requestMap) throws Exception {
		LOG.debug("#######################  ApiRegController savApiRegAjax START ############################");
		
		setSession(session);
		mv.setViewName("jsonView");
		
		//-- [tag:adpt][drm][add][response.sendError추가]
		try {
			UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
			if (userJVo == null) {
				response.sendError(403, "권한이 없습니다.");
				return null;
			}
			else {

				LOG.debug("vo확인 : {}", vo.toString());
				
				ObjectMapper yamlMapper = Yaml.mapper();
				
				JsonNode rootNode = yamlMapper.readTree(CommonFunc.yamlStrDec(vo.getYamlStr()));
				HashMap<String, Object> jsonResult = new ObjectMapper().readValue(rootNode.toString(), HashMap.class);
				
				LOG.debug("API등록 jsonResult : {}", jsonResult);
				
				Map<String, Object> xCategory = (Map<String, Object>) jsonResult.get("x-category");

			    if (xCategory != null) {

			    	for (Map.Entry<String, Object> versionEntry : xCategory.entrySet()) {
			            Object pathMapObj = versionEntry.getValue();
			            if (!(pathMapObj instanceof Map)) continue;

			            Map<String, Object> pathMap = (Map<String, Object>) pathMapObj;

			            // path 반복
			            for (Map.Entry<String, Object> pathEntry : pathMap.entrySet()) {
			                String path = pathEntry.getKey();
			                Object methodMapObj = pathEntry.getValue();
			                if (!(methodMapObj instanceof Map)) continue;

			                Map<String, Object> methodMap = (Map<String, Object>) methodMapObj;
			                
			                for (Map.Entry<String, Object> methodEntry : methodMap.entrySet()) {
			                    Object apiObj = methodEntry.getValue();
			                    if (!(apiObj instanceof Map)) continue;

			                    Map<String, Object> apiInfo = (Map<String, Object>) apiObj;
			                    String apiNm = (String) apiInfo.get("apiNm");

			                    //API명, API PATH 두 항목 xss 요소 검사 
			                    if (CommonFunc.findXSSChars(path) || CommonFunc.findXSSChars(apiNm)) {
			                    	mv.addObject("returnCode", "0");
			    					return mv;
			                    }
			                }
			            }
			        }
			    }
			    
				vo.setRegr(userJVo.getEnCmbrId());
				vo.setAmdr(userJVo.getEnCmbrId());
				
				ApiRegVO newVo = vo;
				
				newVo = (ApiRegVO)KsmUtil.fmt_dec_HTMLTagFilter_vo(newVo);

				Map<String, Object> info = apiRegService.savApiRegPath(newVo);
				
				LOG.debug("info 확인 : {}", info);

				mv.addObject("returnCode", "1");
				mv.addObject("info", info);
				
				//-- ktj : 오류 발생시 heap memory 크기 확인
				LOG.debug("total Heap Size: " +  Runtime.getRuntime().totalMemory() / (1024 * 1024) + " MB");
				LOG.debug("APP Heap Size: " +  Runtime.getRuntime().maxMemory() / (1024 * 1024) + " MB");
				LOG.debug("NOW  Heap Size free: " +  Runtime.getRuntime().freeMemory() / (1024 * 1024) + " MB");
			}
		}
		catch (Exception e) {
			LOG.debug("Exception : " + e.getMessage());
			/*//-- ktj : 오류 발생시 heap memory 크기 확인*/
			LOG.debug("total Heap Size: " +  Runtime.getRuntime().totalMemory() / (1024 * 1024) + " MB");
			LOG.debug("APP Heap Size: " +  Runtime.getRuntime().maxMemory() / (1024 * 1024) + " MB");
			LOG.debug("NOW  Heap Size free: " +  Runtime.getRuntime().freeMemory() / (1024 * 1024) + " MB");
			
			response.sendError(501, "요청처리시 예외 오류가 발생했습니다.");
		}
		
		return mv;
	}
	
	
	/**
	* <pre>
	* 1. 메소드명 : savApiRegPathAjax
	* 2. 작성일 : 2017. 12. 18. 오후 5:35:43
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 저장할 API 번호 조회
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
	@RequestMapping(value="/selApiPathApiNoAjax.do")
	public ModelAndView selApiPathApiNoAjax(HttpSession session , HttpServletRequest request,HttpServletResponse response,  ModelMap model , ApiRegVO vo) throws Exception {
		LOG.debug("#######################  ApiRegController selApiPathApiNoAjax START ############################");
		
		model.addAttribute("apiNo" , apiRegService.selApiPathApiNo(vo));
		
		return new ModelAndView( "jsonView", model );
	}
	
	
	/**
	 * <pre>
	 * 1. 메소드명 : selApiCategoryNoAjax
	 * 2. 작성일   : 2018. 1. 4. 오후 9:41:01
	 * 3. 작성자   : JeonGeun Kang
	 * 4. 설명     : 카테고리 번호 조회
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
	@RequestMapping(value="/selApiCategoryNoAjax.do")
	public ModelAndView selApiCategoryNoAjax(HttpSession session , HttpServletRequest request, HttpServletResponse response, ModelMap model, ApiRegVO vo) throws Exception {
		LOG.debug("#######################  ApiRegController selApiCategoryNoAjax START ############################");
		model.addAttribute("cateNo" , apiRegService.selApiCategoryNo(vo));
		return new ModelAndView( "jsonView", model );
	}
	
	/**
	 * <pre>
	 * 1. 메소드명 : selApiVerNoAjax
	 * 2. 작성일   : 2019. 7. 15.
	 * 3. 작성자   :
	 * 4. 설명     : apiVerNo 조회
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
	@RequestMapping(value="/selApiVerNoAjax.do")
	public ModelAndView selApiVerNoAjax(HttpSession session, HttpServletRequest request, HttpServletResponse response, ModelMap model, ApiRegVO vo) throws Exception {
		LOG.debug("#######################  ApiRegController selApiVerNoAjax START ############################");
		
		//--[tab:job-20200714][chg]
		ApiDefVO map_apiDef = apiRegService.selApiDef(vo);
		model.addAttribute("apiVerNo", map_apiDef.getApiVerNo());
		//--##model.addAttribute("apiVerNo" , apiRegService.selApiVerNo(vo));

		return new ModelAndView( "jsonView", model );
	}
	
	
	/**
	* <pre>
	* 1. 메소드명 : savApiRegBasicAjax
	* 2. 작성일 : 2017. 11. 27. 오후 5:12:46
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 기본정보 저장
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
	@RequestMapping(value="/savApiRegBasicAjax.do")
	public ModelAndView savApiRegBasicAjax(HttpSession session , HttpServletRequest request,HttpServletResponse response,  ModelAndView mv , ApiRegVO vo) throws Exception {
		LOG.debug("#######################  ApiRegController savApiRegBasicAjax START ############################");
		
		setSession(session);
		
		String startDT  = "";
		String endDT    = "";
		String authId 	= "";
		String isAuthYn = "N";
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		
		vo.setRegr(userJVo.getEnCmbrId());
		vo.setAmdr(userJVo.getEnCmbrId());

		mv.setViewName("jsonView");
		/*
		ObjectMapper yamlMapper = Yaml.mapper();
		
		JsonNode rootNode = yamlMapper.readTree(CommonFunc.yamlStrDec(vo.getYamlStr()));
		HashMap<String, Object> jsonResult = new ObjectMapper().readValue(rootNode.toString(), HashMap.class);
		
		LOG.debug("기본정보등록 jsonResult : {}", jsonResult);
		*/
		// 역치환 하여 xss 요소가 있는지 확인
		if (CommonFunc.findXSSChars(StringEscapeUtils.unescapeHtml4(vo.getApiVeriBaseurl()))) {
			LOG.debug("xss 검출");
			mv.addObject("returnCode", "0");
			return mv;
		}

		/*
		 * 네임스페이스 권한 체크 후 등록
		 * 아스날일 경우에만 해당됨
		 * CYD - 2020.09.02
		 *//////////////////////////////////////////////
		if(vo.getSysId().equalsIgnoreCase(this.apisystemSysidArsenal)) {
			Map<String, Object> authMap = new HashMap<String, Object>();
			authMap.put("sysId", vo.getSysId());
			authMap.put("autNm", vo.getProjectNamespace() + " 개발자 그룹");
			authMap.put("regr" , vo.getRegr());
			
			authId = apiRegService.selGrpAuthCheck(authMap);
			LOG.debug(" Ajax Session AuthId ========== {} " , authId);
			if(authId == null) {
				authId = apiRegService.saveAutGrp(authMap);
			}
			//권한 중복여부 체크
			MypageVO mypageVo = new MypageVO();
			mypageVo.setAutSttusCd("MBRAUT1020");
			mypageVo.setMbrId(vo.getRegr());
			mypageVo.setRegr(vo.getRegr());
			mypageVo.setAmdr(vo.getRegr());
			mypageVo.setAutId(authId);
	
			int duCnt = mypageService.chkDupCnt(mypageVo);
			if(duCnt == 0) {
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				cal.setTime(new Date());
				startDT = dayTime.format(cal.getTime());
				// 현재 날짜에서 1년 추가
				cal.add(Calendar.YEAR, 1);
				endDT 	= dayTime.format(cal.getTime());
				mypageVo.setUsePerdStDt(startDT);
				mypageVo.setUserPerdFndDt(endDT);
				//새로운 권한 요청
				mypageService.newAutReq(mypageVo);

				String message =MessageUtil.getMsg("insert.success.msg");
				LOG.debug("message : {}", message);
				
				List<AuthVO> currentAuthList = userJVo.getAuthList();
				AuthVO newAuthVo = new AuthVO();
				newAuthVo.setAutId(authId);
				newAuthVo.setSysId(vo.getSysId());
				newAuthVo.setAutNm(authMap.get("autNm").toString());
				currentAuthList.add(newAuthVo);
				userJVo.setAuthList(currentAuthList);
				session.setAttribute("ssUserVo", userJVo);
				//setSession(session, userJVo.getMbrId());
			}
			// 새롭게 추가된 네임스페이스 권한 그룹 할당
			vo.setAutId(authId);
		}
		////////////////////////////////////////////////
		
		if(vo.getApiSpcNo() != "") {
			
			LOG.debug("수정인 경우 권한 체크");
			
			ApiDefVO apiSpcMap = apiRegService.selApiInfo(vo);
			
			if (!vo.getSysId().equalsIgnoreCase(this.apisystemSysidArsenal)
					&& !KsmUtil.fnSafeStr(apiSpcMap.getRegr()).equalsIgnoreCase(userJVo.getEnCmbrId())
					&& apiRegService.selApiSpcAuthCheck(vo) < 1) {
				LOG.debug("권한 미보유자");
				
				mv.addObject("apiAuthChk", "N");
				return mv;
			}

		}else {
			LOG.debug("신규등록인 경우 권한 체크");
			Boolean autIdCheck = false;
			for(AuthVO autVo : userJVo.getAuthList()) {			
				if(autVo.getAutId().equals(vo.getAutId())) {
					LOG.debug("API 등록 권한 보유");
					autIdCheck = true;
					break;
				}
			}
			
			if(!autIdCheck) {
				LOG.debug("API 등록 권한 미보유");
				mv.addObject("apiAuthChk", "N");			
				return mv;
			}
			
		}

		HashMap<String,Object> info = apiRegService.savApiRegBasic(vo);

		LOG.debug("info 확인 : {}", info);
		LOG.debug(" Yaml File Path ========== {} ", (info.get("filePath")).toString());
		
		apiRegService.savYamlFile((info.get("filePath")).toString() , (info.get("apiSpcNo")).toString() , (info.get("yamlStr")).toString() );
		
		vo.setApiSpcNo((info.get("apiSpcNo")).toString());
		
		session.setAttribute("projectNamespace",vo.getProjectNamespace());
		LOG.debug(" Ajax Session projectNamespace ========== {} " , vo.getProjectNamespace());

		// api기본정보
		ApiDefVO map_apiSpc = apiRegService.selApiInfo(vo);
		
		LOG.debug("map_apiSpc 확인 : {}", map_apiSpc);
		
		vo.setRegr(userJVo.getEnCmbrId());
		int authCount = apiRegService.selApiSpcAuthCheck(vo);
		if(map_apiSpc.getRegr().equalsIgnoreCase(userJVo.getEnCmbrId())
			|| vo.getSysId().equalsIgnoreCase(this.apisystemSysidArsenal)
			|| authCount > 0) {
			isAuthYn = "Y";
		}
		
		mv.addObject("returnCode", "1");
		mv.addObject("isAuthYn", isAuthYn);
		mv.addObject("apiAuthChk", "Y");	
		mv.addObject("info", info);	
		
		return mv;
		
	}
	
	/**
	* <pre>
	* 1. 메소드명 : salApiDupCheckAjax
	* 2. 작성일 : 2017. 11. 28. 오전 10:49:17
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : API명/ API PATH / API PATH 명 체크
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
	@RequestMapping(value="/salApiDupCheckAjax.do")
	public ModelAndView salApiDupCheckAjax(HttpSession session , HttpServletRequest request,HttpServletResponse response,  ModelMap model , ApiRegVO vo) throws Exception {
		LOG.debug("#######################  ApiRegController salApiDupCheckAjax START ############################");
		
		setSession(session);
		
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		
		vo.setRegr(userJVo.getEnCmbrId());
		vo.setAmdr(userJVo.getEnCmbrId());
		
		model.addAttribute("checkCnt", apiRegService.salApiDupCheck(vo));
		
		
		return new ModelAndView( "jsonView", model );
	}
	
	/**
	 * <pre>
	 * 1. 메소드명 : salApiIdCheckAjax
	 * 2. 작성일   : 2017. 12. 12. 오후 8:28:15
	 * 3. 작성자   : JeonGeun Kang
	 * 4. 설명     : api operationId 체크
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
	@RequestMapping(value="/salApiIdCheckAjax.do")
	public ModelAndView salApiIdCheckAjax(HttpSession session ,HttpServletRequest request, HttpServletResponse response, ModelMap model, ApiRegVO vo) throws Exception {
		LOG.debug("#######################  ApiRegController salApiIdCheckAjax START ############################");
		
		setSession(session);
		
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		
		vo.setRegr(userJVo.getEnCmbrId());
		vo.setAmdr(userJVo.getEnCmbrId());
		
		model.addAttribute("checkCnt", apiRegService.salApiIdCheck(vo));
		
		
		return new ModelAndView( "jsonView", model );
	}
	
	/**
	* <pre>
	* 1. 메소드명 : salApijDupPathCheckAjax
	* 2. 작성일 : 2017. 12. 7. 오전 9:36:14
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 카테고리 안의 동일안 Path 가 존재하는지 체크
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
	@RequestMapping(value="/salApijDupPathCheckAjax.do")
	public ModelAndView salApijDupPathCheckAjax(HttpSession session , HttpServletRequest request,HttpServletResponse response,  ModelMap model , ApiRegVO vo) throws Exception {
		LOG.debug("#######################  ApiRegController salApijDupPathCheckAjax START ############################");
		
		setSession(session);
		
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		
		vo.setRegr(userJVo.getEnCmbrId());
		vo.setAmdr(userJVo.getEnCmbrId());
		
		model.addAttribute("duplYn", apiRegService.salApijDupPathCheck(vo));
		
		return new ModelAndView( "jsonView", model );
	}
	
	/**
	 * <pre>
	 * 1. 메소드명 : selApiCateNmCheckAjax
	 * 2. 작성일   : 2017. 12. 23. 오후 8:18:00
	 * 3. 작성자   : JeonGeun Kang
	 * 4. 설명     : 카테고리명 중복 체크
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
	@RequestMapping(value="/selApiCateNmCheckAjax.do")
	public ModelAndView selApiCateNmCheckAjax(HttpSession session , HttpServletRequest request,HttpServletResponse response,  ModelMap model , ApiRegVO vo) throws Exception {
		LOG.debug("#######################  ApiRegController selApiCateNmCheckAjax START ############################");
		
		model.addAttribute("duplYn", apiRegService.selApiCateNmDupCheck(vo));
		
		return new ModelAndView( "jsonView", model );
	}
	
	/**
	 * <pre>
	 * 1. 메소드명 : selApiNmDupCheckAjax
	 * 2. 작성일   : 2017. 12. 23. 오후 9:00:50
	 * 3. 작성자   : JeonGeun Kang
	 * 4. 설명     : API 이름 중복 조회
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
	@RequestMapping(value="/selApiNmCheckAjax.do")
	public ModelAndView selApiNmCheckAjax(HttpSession session , HttpServletRequest request,HttpServletResponse response,  ModelMap model , ApiRegVO vo) throws Exception {
		LOG.debug("#######################  ApiRegController selApiNmCheckAjax START ############################");
		
		model.addAttribute("duplYn", apiRegService.selApiNmDupCheck(vo));
		
		return new ModelAndView( "jsonView", model );
	}

	/**
	 * <pre>
	 * 1. 메소드명 : selApiInfoNmCheckAjax
	 * 2. 작성일   : 2017. 12. 23. 오후 9:00:50
	 * 3. 작성자   : JeonGeun Kang
	 * 4. 설명     : API 이름 중복 조회 기본정보 페이지
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
	@RequestMapping(value="/selApiInfoNmCheckAjax.do")
	public ModelAndView selApiInfoNmCheckAjax(HttpSession session , HttpServletRequest request,HttpServletResponse response,  ModelMap model , ApiRegVO vo) throws Exception {
		LOG.debug("#######################  ApiRegController selApiInfoNmCheckAjax START ############################");

		model.addAttribute("duplYn", apiRegService.selApiInfoNmDupCheck(vo));
		
		return new ModelAndView( "jsonView", model );
	}
	
	/**
	* <pre>
	* 1. 메소드명 : selImportApiListAjax
	* 2. 작성일 : 2017. 11. 28. 오후 2:55:41
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : API 불러오기/ 템플릿 불러오기
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
	@RequestMapping(value="/selImportApiListAjax.do")
	public ModelAndView selImportApiListAjax(HttpSession session , HttpServletRequest request,HttpServletResponse response,  ModelMap model , ApiRegVO vo) throws Exception {
		LOG.debug("#######################  ApiRegController selImportApiListAjax START ############################");
		
		setSession(session);
		
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		
		ArrayList<String> sysList = new ArrayList<String>();
		ArrayList<String> authList 	= new ArrayList<String>();
		
		if(userJVo.getAuthList()!=null && userJVo.getAuthList().size()>0) {
			for(AuthVO authVo : userJVo.getAuthList()) {
				sysList.add(authVo.getSysId());
				authList.add(authVo.getAutId());
			}
		}
		
		vo.setRegr(userJVo.getEnCmbrId());
		vo.setAmdr(userJVo.getEnCmbrId());
		vo.setUserSysIdList(sysList);
		vo.setUserAutIdList(authList);
		
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
		
		model.addAttribute("list", apiRegService.selImportApiList(vo));
		
		int totCnt  = apiRegService.selImportApiTotalList(vo);
		paginationInfo.setTotalRecordCount(totCnt);
		paginationInfo.calculate();
		model.addAttribute("paginationInfo", paginationInfo);//페이징 정보
		
		return new ModelAndView( "jsonView", model );
	}
	
	/**
	 * <pre>
	 * 1. 메소드명 : mvApiCateInfoReg
	 * 2. 작성일   : 2017. 11. 29. 오후 5:05:26
	 * 3. 작성자   : JeonGeun Kang
	 * 4. 설명     : API 카테고리 등록 페이지로 이동
	 * </pre>
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/mvApiCateInfoReg.do")
	public ModelAndView mvApiCateInfoReg(HttpSession session, HttpServletRequest request,HttpServletResponse response,  ModelMap model , ApiRegVO vo ) throws Exception {
		LOG.debug("#######################  ApiRegController mvApiCateInfoReg START ############################");
		ModelAndView mv = new ModelAndView();
		
		String isAuthYn = "N";
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		
		// api기본정보
		ApiDefVO map_apiSpc = apiRegService.selApiInfo(vo);
		// category정보
		ApiCategoryVO map_cateInfo = apiRegService.selCateInfo(vo);
		
		/*
		** 권한체크기준
		**  1. 등록한 사용자
		**  2. 수정권한이 있는 사용자(관리자 및 운영자가 부여)
	    **  
		** 위 두가지 조건을 제외한 나머지 사용자들은 수정불가
		** Y:권한있음, N:권한없음
		**
		** CYD - 2020.07.14
		*///////////////////////////////////////////////
		//mv.addObject("isAuthYn", "N");
		vo.setRegr(userJVo.getEnCmbrId());
		LOG.debug("{}.{} REGR ========== {} ", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), vo.getRegr());
		int authCount = apiRegService.selApiSpcAuthCheck(vo);
		LOG.debug("{}.{} Auth Count ========== {} ", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), authCount);
		if(map_apiSpc.getRegr().equalsIgnoreCase(userJVo.getEnCmbrId()) 
			|| map_apiSpc.getSysId().equalsIgnoreCase(this.apisystemSysidArsenal)
			|| authCount > 0) {
			isAuthYn = "Y";
		}
		
		// 권한 세션 셋업
		session.setAttribute("sIsAuthYn", isAuthYn);
		model.addAttribute("sIsAuthYn", isAuthYn);
		////////////////////////////////////////////////

		model.addAttribute("info", map_apiSpc);
		model.addAttribute("cate", map_cateInfo);
		//-- [tag:SR-20210222][cmt][i][cateInfo, dataTypeInfo deprecated]
		//--##model.addAttribute("cateInfo", apiRegService.selCateList(vo));
		//--##model.addAttribute("dataTypeInfo", apiRegService.selApiDataTypeUseList(vo));
		
		mv.setViewName("api/cateInfoRegForm");
		
		return mv;
	}
	
	
	
	/**
	* <pre>
	* 1. 메소드명 : savApiCateAjax
	* 2. 작성일 : 2017. 11. 28. 오후 3:26:50
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 카테고리 저장/수정
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
	@RequestMapping(value="/savApiCateInfoAjax.do")
	public ModelAndView savApiCateInfoAjax(HttpSession session , HttpServletRequest request,HttpServletResponse response,  ModelAndView mv , ApiRegVO vo) throws Exception {
		LOG.debug("#######################  ApiRegController savApiCateAjax START ############################");
		
		setSession(session);
		
		mv.setViewName("jsonView");
		
		LOG.debug("\n유입된 CtgryNm 확인 : {}\n유입된 CtgryDesc 확인 : {}\n유입된 yamlStr 확인 : {}", vo.getCtgryNm(), vo.getCtgryDesc(), vo.getYamlStr());
				
		ObjectMapper yamlMapper = Yaml.mapper();
		
		JsonNode rootNode = yamlMapper.readTree(CommonFunc.yamlStrDec(vo.getYamlStr()));
		HashMap<String, Object> jsonResult = new ObjectMapper().readValue(rootNode.toString(), HashMap.class);
		
		LOG.debug("jsonResult : {}", jsonResult);

		Map<String, Object> categorys =  (Map<String, Object>) jsonResult.get("x-category");

		List<String> categoryNames = new ArrayList<>(categorys.keySet());
		
		LOG.debug("categoryNames확인 : {}", categoryNames);
		
		for(String categoryName : categoryNames) {
			if (CommonFunc.findXSSChars(categoryName)) {					
				mv.addObject("returnCode", "0");
				return mv;
			}
		}
		
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		vo.setRegr(userJVo.getEnCmbrId());
		vo.setAmdr(userJVo.getEnCmbrId());
		
		Map<String, Object> info =  apiRegService.savApiCateInfo(vo);
		
		LOG.debug("DB에 저장된 yamlStr 확인 : {}", info.get("yamlStr").toString());
		
		LOG.debug("info 확인 : {} ", info);

		mv.addObject("info", info);
		mv.addObject("returnCode", "1");
		apiRegService.savYamlFile( (info.get("filePath")).toString() , vo.getApiSpcNo() , (info.get("yamlStr")).toString() );

		return mv;
	}
	
	/**
	* <pre>
	* 1. 메소드명 : delApiCateInfoAjax
	* 2. 작성일 : 2017. 11. 28. 오후 5:51:32
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 카테고리 삭제
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
	@RequestMapping(value="/delApiCateInfoAjax.do")
	public ModelAndView delApiCateInfoAjax(HttpSession session , HttpServletRequest request,HttpServletResponse response,  ModelMap model , ApiRegVO vo) throws Exception {
		LOG.debug("#######################  ApiRegController delApiCateInfoAjax START ############################");
		
		setSession(session);
		
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		vo.setRegr(userJVo.getEnCmbrId());
		vo.setAmdr(userJVo.getEnCmbrId());
		Map<String, Object> info = apiRegService.delApiCateInfo(vo);
		
		apiRegService.savYamlFile( (info.get("filePath")).toString() , vo.getApiSpcNo() , (info.get("yamlStr")).toString() );
		
		ApiDefVO map_apiSpc = apiRegService.selApiInfo(vo);
		model.addAttribute("info", map_apiSpc);
		//-- [tag:SR-20210222][cmt][i][cateInfo, dataTypeInfo deprecated]		
		//--##model.addAttribute("cateInfo", apiRegService.selCateList(vo));
		//--##model.addAttribute("dataTypeInfo", apiRegService.selApiDataTypeUseList(vo));
		
		return new ModelAndView( "jsonView", model );
	}
	
	
	public void setSession(HttpSession session) {
		
//    	UserJoinVO userJVo = new UserJoinVO();
//		userJVo.setMbrId("0001M7HGVS7AwV401M4R/0xqmg==");
//		userJVo.setMbrNm("암호화된이름");
//		userJVo.setEnCmbrId("0001M7HGVS7AwV401M4R/0xqmg==");
//		
//		ArrayList<AuthVO> authList = new ArrayList<AuthVO>();
//		AuthVO authInfo = new AuthVO();
//		authInfo.setAutId("26");
//		authInfo.setSysId("IOTMAKERS");
//		authInfo.setSysNm("IOTMAKERS");
//		authInfo.setAutNm("개발자");
//		
//		authList.add(authInfo);
//		
//		authInfo = new AuthVO();
//		authInfo.setAutId("31");
//		authInfo.setSysId("GIGAGENIE");
//		authInfo.setSysNm("GIGAGENIE");
//		authInfo.setAutNm("개발자");
//		
//		authList.add(authInfo);
//		
//		userJVo.setAuthList(authList);
//		
//		session.setAttribute("ssUserVo",userJVo);
	}
	
	/**
	 * <pre>
	 * 1. 메소드명 : mvApiDataTypeReg
	 * 2. 작성일   : 2017. 11. 29. 오후 5:07:36
	 * 3. 작성자   : JeonGeun Kang
	 * 4. 설명     : API DATA TYPE 등록 페이지로 이동
	 * </pre>
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/mvApiDataTypeReg.do")
	public ModelAndView mvApiDataTypeReg(HttpSession session ,HttpServletRequest request,HttpServletResponse response,  ModelMap model , ApiRegVO vo) throws Exception {
		LOG.debug("#######################  ApiRegController mvApiDataTypeReg START ############################");
		ModelAndView mv = new ModelAndView();
		
		// api 인증타입
		mv.addObject("dataTypeList", cmnService.selComnList("DATTYP1000"));
		
		ApiDefVO map_apiSpc = apiRegService.selApiInfo(vo);
		model.addAttribute("info", map_apiSpc);
		//-- [tag:SR-20210222][cmt][i][cateInfo, dataTypeInfo deprecated]
		//--##model.addAttribute("cateInfo", apiRegService.selCateList(vo));
		//--##model.addAttribute("dataTypeInfo", apiRegService.selApiDataTypeUseList(vo));
		
		mv.setViewName("api/dataTypeRegForm");
		return mv;
	}
	
	/**
	* <pre>
	* 1. 메소드명 : savApiDataTypeRegAjax
	* 2. 작성일 : 2017. 12. 4. 오후 2:22:04
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 데이터 타입 저장/수정/삭제
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
	@RequestMapping(value="/savApiDataTypeRegAjax.do")
	public ModelAndView savApiDataTypeRegAjax(HttpSession session , HttpServletRequest request,HttpServletResponse response,  ModelAndView mv , ApiRegVO vo) throws Exception {
		LOG.debug("#######################  ApiRegController savApiDataTypeRegAjax START ############################");
		
		setSession(session);
		mv.setViewName("jsonView");
		
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		
		vo.setRegr(userJVo.getEnCmbrId());
		vo.setAmdr(userJVo.getEnCmbrId());
		
		ObjectMapper yamlMapper = Yaml.mapper();
		
		JsonNode rootNode = yamlMapper.readTree(CommonFunc.yamlStrDec(vo.getYamlStr()));
		HashMap<String, Object> jsonResult = new ObjectMapper().readValue(rootNode.toString(), HashMap.class);
		
		LOG.debug("jsonResult : {}", jsonResult); 

		Map<String, Object> definitions =  (Map<String, Object>) jsonResult.get("definitions");

		List<String> definitionNames = new ArrayList<>(definitions.keySet());
		
		LOG.debug("definitions확인 : {}", definitionNames);
		
		for(String definitionName : definitionNames) {
			if (CommonFunc.findXSSChars(definitionName)) {
				mv.addObject("returnCode", "0");
				return mv;
			}
		}
		
		Map<String, Object> info = apiRegService.savApiDataTypeReg(vo);

		LOG.debug("DATA TYPE 저장 시 info 확인 : {}", info);
		mv.addObject("returnCode", "1");
		mv.addObject("info", info);
		
		return mv;
	}
	
	
	/**
	* <pre>
	* 1. 메소드명 : delApiPathAjax
	* 2. 작성일 : 2017. 12. 7. 오전 10:00:47
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 한건의 PATH 삭제
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
	@RequestMapping(value="/delApiPathAjax.do")
	public ModelAndView delApiPathAjax(HttpSession session , HttpServletRequest request,HttpServletResponse response,  ModelMap model , ApiRegVO vo) throws Exception {
		LOG.debug("#######################  ApiRegController delApiPathAjax START ############################");
		
		setSession(session);
		
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		vo.setRegr(userJVo.getEnCmbrId());
		vo.setAmdr(userJVo.getEnCmbrId());
		
		Map<String, Object> info = apiRegService.delApiPath(vo);
		
		model.addAttribute("info", info);
		//-- [tag:SR-20210222][cmt][i][cateInfo, dataTypeInfo deprecated]
		//--##model.addAttribute("cateInfo", apiRegService.selCateList(vo));
		//--##model.addAttribute("dataTypeInfo", apiRegService.selApiDataTypeUseList(vo));
		
		return new ModelAndView( "jsonView", model );
	}
	
	/**
	* <pre>
	* 1. 메소드명 : delApiPathParamAjax
	* 2. 작성일 : 2017. 12. 7. 오전 10:14:28
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 동일 PATH 의 삭제
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
	@RequestMapping(value="/delApiAllPathAjax.do")
	public ModelAndView delApiAllPathAjax(HttpSession session , HttpServletRequest request,HttpServletResponse response,  ModelMap model , ApiRegVO vo) throws Exception {
		LOG.debug("#######################  ApiRegController delApiAllPathAjax START ############################");
		
		setSession(session);
		
		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		vo.setRegr(userJVo.getEnCmbrId());
		vo.setAmdr(userJVo.getEnCmbrId());
		
		Map<String, Object> info = apiRegService.delApiAllPath(vo);
		
		model.addAttribute("info", info);
		//-- [tag:SR-20210222][cmt][i][cateInfo, dataTypeInfo deprecated]		
		//--##model.addAttribute("cateInfo", apiRegService.selCateList(vo));
		//--##model.addAttribute("dataTypeInfo", apiRegService.selApiDataTypeUseList(vo));
		
		return new ModelAndView( "jsonView", model );
	}
	/**
	 * <pre>
	 * 1. 메소드명 : yamlDownload
	 * 2. 작성일 : 2017. 12. 7. 오전 10:14:28
	 * 3. 작성자 : JungHwan Hwang
	 * 4. 설명 :  목서버에서 파일 불러오기 
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
	@RequestMapping(value="/yamlDownload.do")
	public void yamlDownload(ApiRegVO vo, HttpServletResponse res) throws Exception {
		LOG.debug("#######################  ApiRegController yamlDownload START ############################");
		res.setContentType("text/yaml; charset=UTF-8");   //한글설정
		
		PrintWriter out = res.getWriter();
		
        vo.setApiSpcNo(vo.getApiNo());
        ApiDefVO map_apiSpc = apiRegService.selApiInfo(vo);
        out.println(map_apiSpc.getYamlSbst());
	}
	
	
	
	/**
	* <pre>
	* 1. 메소드명 : getYamlAjax
	* 2. 작성일 : 2017. 12. 14. 오전 12:21:22
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : URL 에서 YAMl 데이터 가져오기
	* </pre>
	* @param vo
	* @param res
	* @throws Exception
	*/
	@ResponseBody
	@RequestMapping(value="/selUrlToYamlAjax.do")
	public ModelAndView selUrlToYamlAjax(ApiRegVO vo, HttpSession session , HttpServletRequest request,HttpServletResponse response,  ModelMap model ) throws Exception {
        vo.setApiSpcNo(vo.getApiNo());
        HashMap<String,Object> eMap = (HashMap<String,Object>)apiRegService.selUrlToYamlAjax(vo);
        model.addAttribute(eMap);		
        return new ModelAndView( "jsonView", model );
	}
	
	
	/**
	* <pre>
	* 1. 메소드명 : regApidocAjax
	* 2. 작성일 : 2018. 1. 5. 오후 1:53:54
	* 3. 작성자 : user
	* 4. 설명 : apidoc 파일 yaml 로 추출
	* </pre>
	* @param vo
	* @param uploadFile
	* @param session
	* @param request
	* @param response
	* @param model
	* @return
	* @throws Exception
	*/
	@ResponseBody
	@RequestMapping(value="/regApidocAjax.do")
	public ModelAndView regApidocAjax(HttpSession session , HttpServletRequest request,HttpServletResponse response, 
			@ModelAttribute("uploadFile")  MultipartFile uploadFile,ModelMap model ) throws Exception {
		
		LOG.debug("#######################  ApiRegController regApidocAjax START #####  :: {}", uploadFile);
		Map<String, Object> map = apiRegService.regApidocAjax(uploadFile);
		
		model.addAttribute("yamlInfo", map);
        
		return new ModelAndView( "jsonView", model );
	}
	
	/**
	 * <pre>
	 * 1. 메소드명 : savApiYamlAjax
	 * 2. 작성일   : 2018. 1. 9. 오후 6:47:35
	 * 3. 작성자   : JeonGeun Kang
	 * 4. 설명     : yaml 조회
	 * </pre>
	 * @param vo
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="/savApiYamlAjax.do")
	public ModelAndView savApiYamlAjax(ApiRegVO vo, HttpSession session , HttpServletRequest request,HttpServletResponse response,  ModelMap model ) throws Exception {
		ApiDefVO map_apiSpc = apiRegService.selApiInfo(vo);
		model.addAttribute("yamlInfo", map_apiSpc);
		return new ModelAndView( "jsonView", model );
	}
	
	//--[tag:adpt][add]
	@ResponseBody
	@RequestMapping(value="/selNextApiId.do")
	public ModelAndView selNextApiId(HttpServletRequest request, ModelMap model) throws Exception {

		String prefix = KsmUtil.fnSafeStr(request.getParameter("prefix"));

		model.addAttribute("NextApiId", apiRegService.selNextApiId(prefix));
		return new ModelAndView( "jsonView", model );
	}
	
	//-- [tag:SR-20220328]
	@ResponseBody
	@RequestMapping(value="/selNextApiIdInfo.do")
	public ModelAndView selNextApiIdInfo(HttpServletRequest request, ModelMap model) throws Exception {

		HashMap<String, Object> map_in = new HashMap<String, Object>();
		map_in.put("prefix", KsmUtil.fnSafeStr(request.getParameter("prefix")));
		map_in.put("rangeLen", KsmUtil.parseInt(request.getParameter("rangeLen"), 0));
		map_in.put("sysId", KsmUtil.fnSafeStr(request.getParameter("sysId")));
		map_in.put("maxId", KsmUtil.parseInt(request.getParameter("maxId"), 0));
		map_in.put("minId", KsmUtil.parseInt(request.getParameter("minId"), 0));

		model.addAllAttributes(apiRegService.selNextApiIdInfo(map_in));
		return new ModelAndView( "jsonView", model );
	}

	@ResponseBody
	@RequestMapping(value="/selDeployProc.do")
	public ModelAndView selDeployProc(HttpServletRequest request, ModelMap model) throws Exception {

		Map<String, Object> map_in = new HashMap<>();
		map_in.put("apiNo", KsmUtil.fnSafeStr(request.getParameter("apiNo")));
		map_in.put("top", "1");

		model.addAttribute("deployProc", apiRegService.selDeployProc(map_in));
		return new ModelAndView( "jsonView", model );
	}

	//-- [tag:20200913][add] {
	@ResponseBody
	@RequestMapping(value="/apiNoCheck.do")
	public ModelAndView apiNoCheck(HttpServletRequest request, ModelMap model) throws Exception {
		//-- query-input
		HashMap<String, Object> map_in = new HashMap<String, Object>();
		map_in.put("apiSpcNo", KsmUtil.fnSafeStr(request.getParameter("apiSpcNo")));
		map_in.put("apiNo", KsmUtil.fnSafeStr(request.getParameter("apiNo")));
		map_in.put("path", KsmUtil.fnSafeStr(request.getParameter("path")));
		map_in.put("method", KsmUtil.fnSafeStr(request.getParameter("method")));
		//-- query-output
		String queryId = "apiReg.select_APINO_CHECK";

		Map<String, Object> map_out = apiRegDAO.select_APINO_CHECK(map_in);
		
		model.addAttribute("path", KsmUtil.fnSafeStr(map_out.get("path")));
		model.addAttribute("method", KsmUtil.fnSafeStr(map_out.get("method")));
		model.addAttribute("max_apino", KsmUtil.fnSafeStr(map_out.get("max_apino")));

		return new ModelAndView("jsonView", model);
	}

	@ResponseBody
	@RequestMapping(value="/saveYamlFileAjax.do", method={RequestMethod.POST})
	public ModelAndView saveYamlFileAjax(HttpSession session , HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
		String apiSpcNo = KsmUtil.fnSafeStr(request.getParameter("apiSpcNo"));
		String yamlSbst = KsmUtil.fnSafeStr(request.getParameter("yamlSbst"));

		ApiRegVO apiRegVO = new ApiRegVO();

		UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		if (userJVo == null) {
			response.sendError(403, "권한이 없습니다.");
			return null;
		}

		apiRegVO.setRegr(userJVo.getEnCmbrId());
		apiRegVO.setApiSpcNo(apiSpcNo);
		apiRegVO.setYamlStr(yamlSbst);
		//--##apiRegVO.setYamlSbst(yamlSbst);

		HashMap<String,Object> map_data = new HashMap<String,Object>();
		map_data.put("result", "INIT");
		try {
			apiRegService.savYamlToFile(apiRegVO);
			map_data.put("result", "OK");
			map_data.put("apiSpcNo", apiRegVO.getApiSpcNo());
			map_data.put("yamlSbst", apiRegVO.getYamlSbst());
		} catch(Exception e) {
			map_data.put("result", "EXCEPT");
			map_data.put("result_message", e.getMessage());
			//--##response.sendError(501, "요청처리시 예외 오류." + e.getMessage());
		}
		model.addAllAttributes(map_data);
		return new ModelAndView( "jsonView", model);
	}
	//-- [tag:20200913][add] }
	
	/**
	 * <pre>
	 * 1. 메소드명 : existsNSAtGitlab
	 * 2. 작성일   : 2020. 09. 02
	 * 3. 작성자   : CYD
	 * 4. 설명     : 아스날 프로젝트 등록 조회
	 * </pre>
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @param inputData
	 * @param accessToken
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="/existsNSAtGitlabAjax.do", method={RequestMethod.POST, RequestMethod.GET})
	public ModelAndView existsNSAtGitlabAjax(HttpSession session, HttpServletRequest request, HttpServletResponse response, ModelMap model, ApiRegVO vo) throws Exception {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
		
		StringBuffer sb = new StringBuffer();
		
		String szNamespace;
		String szErrorCode;
		String szHostUrl;
		String szMethodType;
		
		JSONParser inputParse = new JSONParser();
		JSONObject jsonObject = null;
		
		szNamespace  = vo.getProjectNamespace();
		//szNamespace  = inputData;
		szMethodType = "1";
		// 공백제거
		szNamespace  = szNamespace.replaceAll(" ", "");
		
		if(szNamespace.equalsIgnoreCase("")) {
			model.addAttribute("returncode"		  , "0");
			model.addAttribute("returndescription", "Fail");
			model.addAttribute("errorcode"		  , "E00003");
			model.addAttribute("errordescription" , "namespace 파라메터는 필수입력입니다.");
			return new ModelAndView( "jsonView", model );
		}
		
		/* 네임스페이스 정보 가져오기
		 * CYD 2020.09.02
		 *///////////////////////////////////////////////////

		sb.append(gitlabArsenalHost)
		  .append("api/v4/namespaces?search=")
		  .append(szNamespace);
		
		szHostUrl = sb.toString();
		LOG.debug("==== HostUrl = {} ", szHostUrl);
		HashMap<String,String> map;
		map = new HashMap<String,String>();
		map.put("hostUrl"	   , szHostUrl);
		map.put("methodType"   , szMethodType);
		map.put("apiJsonParams", "");
		
		HashMap<String,String> projectMap = this.doApiResult(map);
		LOG.debug("==== namespaceMap = {} ", projectMap);
		
		try {
			jsonObject = (JSONObject)inputParse.parse(projectMap.get("jsonResponse"));
		} catch (ParseException e) {
			model.addAttribute("returncode"		  , "0");
			model.addAttribute("returndescription", "Fail");
			model.addAttribute("errorcode"		  , "E00006");
			model.addAttribute("errordescription" , e.getMessage());

			return new ModelAndView( "jsonView", model );
		}
		
		szErrorCode = jsonObject.containsKey("errorCode") == true ? jsonObject.get("errorCode").toString() : "";
		
		if(szErrorCode.equalsIgnoreCase("404")) {
			model.addAttribute("returncode"		  , "0");
			model.addAttribute("returndescription", "Fail");
			model.addAttribute("errorcode"		  , "E00004");
			model.addAttribute("errordescription" , "네임스페이스가 존재하지 않습니다.");

			return new ModelAndView( "jsonView", model );
		} else if(szErrorCode.equalsIgnoreCase("500")) {
			model.addAttribute("returncode"		  , "0");
			model.addAttribute("returndescription", "Fail");
			model.addAttribute("errorcode"		  , "E00006");
			model.addAttribute("errordescription" , "네임스페이스 정보가 올바르지 않습니다!\n만약 네임스페이스명에 한글이 포함되어 있다면 수정해주세요.");

			return new ModelAndView( "jsonView", model );
		} else if(szErrorCode.equalsIgnoreCase("-1")) {
			model.addAttribute("returncode"		  , "0");
			model.addAttribute("returndescription", "Fail");
			model.addAttribute("errorcode"		  , "E00006");
			model.addAttribute("errordescription" , "오류입니다!\n계속 오류가 발생한다면 운영팀(adc@kt.com)으로 문의 주세요.");
	
			return new ModelAndView( "jsonView", model );
		}
		
		model.addAttribute("returncode"		  , "1");
		model.addAttribute("returndescription", "Success");
		model.addAttribute("response", jsonObject);
		
		return new ModelAndView( "jsonView", model );
	}
	
	// SHUB Api 호출
	@SuppressWarnings("unused")
	private HashMap<String,String> doApiResult(HashMap<String,String> map) {
			String response	 	 = null;
			String returnCode	 = "0";
			String hostUrl 		 = map.get("hostUrl").toString();
			String methodType 	 = map.get("methodType").toString();
			String apiJosnParams = map.get("apiJsonParams").toString();
				
			HashMap<String,String> mapResponse = new HashMap<String,String>();
				
			//URL, API가저오기
			ksmShubApiClient.setApiUrl(hostUrl);
			ksmShubApiClient.setApiHeader("PRIVATE-TOKEN", gitlabArsenalPrivateToken);
			//ksmShubApiClient.setApiHeader("PRIVATE-TOKEN", "yyuCn4SDn_N5Abyyzbsa");

			if(methodType.equalsIgnoreCase("1")) {
				ksmShubApiClient.setApiMethod(KsmApiClient.METHOD_GET);
			} else if(methodType.equalsIgnoreCase("2")) {
				ksmShubApiClient.setApiMethod(KsmApiClient.METHOD_POST);
			} else if(methodType.equalsIgnoreCase("3")) {
				ksmShubApiClient.setApiMethod(KsmApiClient.METHOD_PUT);
			}
			
			ksmShubApiClient.setApiParamsJson(apiJosnParams);

			boolean bSuccess = false;
			bSuccess = ksmShubApiClient.sendRestRequest();
					
			if(bSuccess) {
				if(ksmShubApiClient.getHttpResponse().getCode() == 200 || ksmShubApiClient.getHttpResponse().getCode() == 201) {
					returnCode = "1";
					if(methodType.equalsIgnoreCase("1")) {
						response = ksmShubApiClient.getApiResult().getJsonString();

						if(response.equalsIgnoreCase("{}")) {
							response = ksmShubApiClient.getHttpResponse().getBody();
							response = response.replace("[", "");
							response = response.replace("]", "");
							if(response.equalsIgnoreCase("")) {
								returnCode = "0";
								response = "{\"errorCode\":\"404\"}";
							}
						}
						LOG.debug("==== response() = {} " , response);
					} else {
						if( ksmShubApiClient.getApiResult().getJsonString().equals("{}")) {
							response = apiJosnParams;
						} else {
							response = ksmShubApiClient.getApiResult().getJsonString();
						}
					}
				} else {
					//response = "404 Not Found";
					response = "{\"errorCode\":\"" + String.valueOf(ksmShubApiClient.getHttpResponse().getCode()) + "\"}";
				}
			}else {
				response = "{\"errorCode\":\"-1\"}";
			}
			
			response = response.replace("{", "{\r\t");
			response = response.replace("\",", "\",\r\t");
			response = response.replace("}", "\r}");

			mapResponse.put("returnCode"  , returnCode);
			mapResponse.put("jsonResponse", response);
					
			return mapResponse;
		}
	
	//SHUB API연동으로 API명 중복체크(재식)
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/apiNameValidCheckAjax.do")
	public ModelAndView apiNameValidCheckAjax(ApiRegVO vo) throws Exception{
		
		ModelAndView mv = new ModelAndView();
		
		boolean isNotExists = true;
		String ver = vo.getVer();
		String apiNm = vo.getApiNm();
		
		LOG.debug("\n\n### apiNameValidCheckAjax : {}","ver = "+ver ,"apiNm = "+apiNm);
		
		RestTemplate restTemplate = new RestTemplate();

		HashMap<String, Object> map = new HashMap<String, Object>();
		
		//SHUB API 연동 URL에 API명과 버전 셋팅
		String pullUrl = apiNameCheckURL+apiNm+"_v"+ver;
		
		LOG.debug("\n\n### apiNameValidCheckAjax.SHUB API호출 URL 체크: {}",pullUrl);
		
		//호출 성공 시 중복 API명 존재
		try {
			
			map = restTemplate.getForObject(pullUrl, HashMap.class);
			
			Map<String, Object> resultMap = (HashMap<String, Object>)map.get("common");
			
			if(resultMap != null) {
				Integer code = (Integer) resultMap.get("code");
				LOG.debug("## getApiDplyById result code : {}", code);
				
				if(code == 404) {
					LOG.debug("\n\n### apiNameValidCheckAjax.SHUB API호출결과 (중복된 API명 없음): {}", map);
				} else {
					LOG.debug("\n\n### apiNameValidCheckAjax.SHUB API호출결과(중복된 API명 존재): {}", map);
					isNotExists = false;
				}
			} else {
				LOG.debug("\n\n### apiNameValidCheckAjax.SHUB API호출결과(중복된 API명 존재): {}", map);
				isNotExists = false;
			}
//			mv.addObject("apiNmChk", false);
	    }
		//404에러 발생 시 API명 등록 가능
		catch (HttpClientErrorException e) {	
	        		       	        
	        if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
	        	 LOG.debug("\n\n### apiNameValidCheckAjax.SHUB API호출결과 (중복된 API명 없음) 에러코드 {}",e.getStatusCode());
	        } else {
				LOG.debug("\n\n### apiNameValidCheckAjax.SHUB API호출결과(중복된 API명 존재): {}", map);
				isNotExists = false;
			}  	        	
	    } 	
		
		mv.addObject("apiNmChk", isNotExists);
		mv.setViewName("jsonView");
		
		return mv;	
	}
	
	//API아이디 중복체크(재식)
	@RequestMapping(value="/apiIdValidCheckAjax.do")
	public ModelAndView apiIdValidCheckAjax(ApiRegVO vo) throws Exception{
	
		ModelAndView mv = new ModelAndView();
		
		String apiSpcNo = vo.getApiSpcNo();
		String apiId = vo.getApiId();
	
		LOG.debug("\n\n### apiIdValidCheckAjax : {}","apiSpcNo = "+apiSpcNo, "apiId = "+apiId);
			
		boolean idCheckResult = apiRegService.selectApiIdChk(apiId);
						
		LOG.debug("\n\n### apiIdValidCheckAjax.idCheckResult : {}",idCheckResult);

		mv.addObject("apiIdChk", idCheckResult);
	
		mv.setViewName("jsonView");
		
		return mv;
	}
	
	//-- [tag:PRJ-20220901] {
	@RequestMapping(value = "/{pathVal}/ajax_query.do")
	public ModelAndView ajaxQuery(HttpServletRequest request, @PathVariable(value="pathVal") String pathVal) {
		LOG.debug("\n\n### {}.{}() [request.getServletPath(): {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), request.getServletPath());

		ModelMap model = apiRegService.ajaxQuery(request, pathVal);
		return new ModelAndView("jsonView", model);
	}
	@RequestMapping(value = "/{pathVal}/ajax_proc.do")
	public ModelAndView ajaxProc(HttpServletRequest request, @PathVariable(value="pathVal") String pathVal, @RequestBody String requestBody) {
		LOG.debug("\n\n### {}.{}() [request.getServletPath(): {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), request.getServletPath());

		ModelMap model = apiRegService.ajaxProc(request, pathVal, requestBody);
		return new ModelAndView("jsonView", model);
	}
	//-- [tag:PRJ-20220901] }
}


