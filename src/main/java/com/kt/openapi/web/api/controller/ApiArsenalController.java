package com.kt.openapi.web.api.controller;

import com.ksm.apisdk.KsmShubApiClient;
import com.kt.openapi.web.api.dao.ApiRegDAO;
import com.kt.openapi.web.api.service.ApiArsenalService;
import com.kt.openapi.web.api.service.ApiRegService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.HashMap;
import java.util.Map;


/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.api.controller
 * 2. 타입명   : ApiArsenalController.java
 * 3. 작성일   : 2020. 05. 25
 * 4. 작성자   : CYD 
 * 5. 설명     : API Arsenal Controller
 * </pre>
 */
@RestController
@RequestMapping(value="/api/arsenal")
public class ApiArsenalController {
	
	private static final Logger LOG = LoggerFactory.getLogger(ApiMainController.class);
	
	static KsmShubApiClient ksmShubApiClient = new KsmShubApiClient();
	
	@Autowired
	private ApiRegService apiRegService;
	
	@Autowired
	private ApiArsenalService apiArsenalService;
	
	@Autowired
	private ApiRegDAO apiRegDAO;

	// 20250821 CodeScanning
//	//-- [CYD][202005--][add]
//	@Value("${gitlab.arsenal.host}")
//	private String gitlabArsenalHost;
//
//	//-- [CYD][202005--][add]
//	@Value("${gitlab.arsenal.base.path}")
//	private String gitlabArsenalBasePath;
//	
//	//-- ADD CYD:20200506
//	@Value("${gitlab.arsenal.token}")
//	private String gitlabArsenalAccessToken;
//	
//	//-- ADD CYD:20200506
//	@Value("${gitlab.private.token}")
//	private String gitlabArsenalPrivateToken;
	
	
	/**
	 * <pre>
	 * 1. 메소드명 : selApiHistoryAjax
	 * 2. 작성일   : 2020. 07. 02
	 * 3. 작성자   : CYD
	 * 4. 설명     : 동기화 이력 조회
	 * </pre>
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @param inputData
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="/selApiHistoryAjax.do")
	public ModelAndView selApiHistoryAjax(HttpSession session, HttpServletRequest request, HttpServletResponse response, ModelMap model, @RequestBody String inputData) throws Exception {
		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		//UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
		Map<String, Object> map = new HashMap<>();
		String szApiSpcNo = "";

		String szKindOf   = "";
		
		JSONParser inputParse = new JSONParser();
		JSONObject jsonObject = null;
		try {
			jsonObject = (JSONObject)inputParse.parse(inputData);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			model.addAttribute("returncode"		  , "0");
			model.addAttribute("returndescription", "Fail");
			model.addAttribute("errorcode"		  , "E00006");
			model.addAttribute("errordescription" , e.getMessage());

			return new ModelAndView( "jsonView", model );
		}
		
		szApiSpcNo = jsonObject.containsKey("apiSpcNo") == true ? jsonObject.get("apiSpcNo").toString() : "";
		szKindOf   = jsonObject.containsKey("kindOf"  ) == true ? jsonObject.get("kindOf"  ).toString() : "";

		// 공백제거
		szApiSpcNo = szApiSpcNo.replaceAll(" ", "");
		szKindOf   = szKindOf.replaceAll(" ", "");
		map.put("apiSpcNo", szApiSpcNo);
		map.put("kindOf"  , szKindOf);
		
		LOG.debug("==== Arsenal History Map Strart = {} ", map);
		try {
			model.addAttribute("nlist", apiArsenalService.selApiSpcHistory(map));
			//LOG.debug("==== Arsenal History Map End = {} ", apiArsenalService.selApiSpcHistory(map));
			
		} catch (Exception e) {
			// TODO: handle exception
			LOG.debug("==== Arsenal History Map End = {} ", e.getStackTrace().toString());
			model.addAttribute("returncode"		  , "0");
			model.addAttribute("returndescription", "Fail");
			model.addAttribute("errorcode"		  , "E00006");
			model.addAttribute("errordescription" , e.getStackTrace());

			return new ModelAndView( "jsonView", model );
		}
		
		return new ModelAndView( "jsonView", model );
	}
	
	// 20250821 CodeScanning
//	/**
//	 * <pre>
//	 * 1. 메소드명 : exitsProjectAtStudio
//	 * 2. 작성일   : 2020. 07. 02
//	 * 3. 작성자   : CYD
//	 * 4. 설명     : 아스날 프로젝트 등록 조회
//	 * </pre>
//	 * @param session
//	 * @param request
//	 * @param response
//	 * @param model
//	 * @param inputData
//	 * @param accessToken
//	 * @return
//	 * @throws Exception
//	 */
//	@ResponseBody
//	@RequestMapping(value="/existsProjectAtStudio", method={RequestMethod.POST, RequestMethod.GET})
//	public ModelAndView existsProjectAtStudio(HttpSession session, HttpServletRequest request, HttpServletResponse response, ModelMap model, @RequestBody String inputData, @RequestHeader(value="Access-token", required=false) String accessToken) throws Exception {
//		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
//		
//		String szProjectName;
//		String szNamespace;
//		String szAccessToken;
//		
//		szAccessToken = accessToken == null ? "" : accessToken;
//		
//		if(szAccessToken.equalsIgnoreCase("") == true || szAccessToken.equalsIgnoreCase(gitlabArsenalAccessToken) == false) {
//			model.addAttribute("returncode"		  , "0");
//			model.addAttribute("returndescription", "Fail");
//			model.addAttribute("errorcode"		  , "E00001");
//			model.addAttribute("errordescription" , "토근 인증 오류입니다.");
//			return new ModelAndView( "jsonView", model );
//		}
//		
//		JSONParser inputParse = new JSONParser();
//		JSONObject jsonObject = null;
//		try {
//			jsonObject = (JSONObject)inputParse.parse(inputData);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//			model.addAttribute("returncode"		  , "0");
//			model.addAttribute("returndescription", "Fail");
//			model.addAttribute("errorcode"		  , "E00006");
//			model.addAttribute("errordescription" , e.getMessage());
//
//			return new ModelAndView( "jsonView", model );
//		}
//		
//		szProjectName = jsonObject.containsKey("projectName") == true ? jsonObject.get("projectName").toString() : "";
//		szNamespace   = jsonObject.containsKey("namespace"	) == true ? jsonObject.get("namespace"	).toString() : "";
//		
//		// 공백제거
//		szProjectName = szProjectName.replaceAll(" ", "");
//		szNamespace   = szNamespace.replaceAll(" ", "");
//		
//		if(szProjectName.equalsIgnoreCase("")) {
//			model.addAttribute("returncode"		  , "0");
//			model.addAttribute("returndescription", "Fail");
//			model.addAttribute("errorcode"		  , "E00003");
//			model.addAttribute("errordescription" , "projectName 파라메터는 필수입력입니다.");
//			return new ModelAndView( "jsonView", model );
//		}
//		
//		if(szNamespace.equalsIgnoreCase("")) {
//			model.addAttribute("returncode"		  , "0");
//			model.addAttribute("returndescription", "Fail");
//			model.addAttribute("errorcode"		  , "E00003");
//			model.addAttribute("errordescription" , "namespace 파라메터는 필수입력입니다.");
//			return new ModelAndView( "jsonView", model );
//		}
//		
//		// 프로젝트코드 가져오기
//		EgovMap apiSpcMap = new EgovMap();
//		apiSpcMap.put("projectNS", szNamespace);
//		apiSpcMap.put("projectNM", szProjectName);
//		LOG.debug("==== EgovMap Data = {} ",apiSpcMap);
//				
//		try {
//			// 네임스페스의 프로젝트 API_SPC_NO 가져오기
//			EgovMap apiSpcInfoMap = apiArsenalService.selApiSpcInfoByProjectNsWithNm(apiSpcMap);
//			
//			if(apiSpcInfoMap != null && apiSpcInfoMap.containsKey("apiSpcNo"))
//			{
//				model.addAttribute("response", "true");
//			} else {
//				model.addAttribute("response", "false");
//			}
//			
//			LOG.debug("==== Arsenal exitsProjectAtStudio = {} ", apiSpcInfoMap);
//			
//		} catch (Exception e) {
//			// TODO: handle exception
//			LOG.debug("==== Exception exitsProjectAtStudio = {} ", e.getStackTrace().toString());
//			model.addAttribute("returncode"		  , "0");
//			model.addAttribute("returndescription", "Fail");
//			model.addAttribute("errorcode"		  , "E00006");
//			model.addAttribute("errordescription" , e.getStackTrace());
//
//			return new ModelAndView( "jsonView", model );
//		}
//		
//		return new ModelAndView( "jsonView", model );
//	}
//	
//	/**
//	 * <pre>
//	 * 1. 메소드명 : exitsProjectAtStudio
//	 * 2. 작성일   : 2020. 07. 02
//	 * 3. 작성자   : CYD
//	 * 4. 설명     : 아스날 프로젝트 등록 조회
//	 * </pre>
//	 * @param session
//	 * @param request
//	 * @param response
//	 * @param model
//	 * @param inputData
//	 * @param accessToken
//	 * @return
//	 * @throws Exception
//	 */
//	@ResponseBody
//	@RequestMapping(value="/existsNSAtGitlab", method={RequestMethod.POST, RequestMethod.GET})
//	public ModelAndView existsNSAtGitlab(HttpSession session, HttpServletRequest request, HttpServletResponse response, ModelMap model, @RequestBody String inputData) throws Exception {
//		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
//		
//		StringBuffer sb = new StringBuffer();
//		
//		String szNamespace;
//		String szAccessToken;
//		String szErrorCode;
//		String szHostUrl;
//		String szMethodType;
//		
//		JSONParser inputParse = new JSONParser();
//		JSONObject jsonObject = null;
//		try {
//			jsonObject = (JSONObject)inputParse.parse(inputData);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//			model.addAttribute("returncode"		  , "0");
//			model.addAttribute("returndescription", "Fail");
//			model.addAttribute("errorcode"		  , "E00006");
//			model.addAttribute("errordescription" , e.getMessage());
//
//			return new ModelAndView( "jsonView", model );
//		}
//		
//		szNamespace  = jsonObject.containsKey("namespace") == true ? jsonObject.get("namespace").toString() : "";
//		//szNamespace  = inputData;
//		szMethodType = "1";
//		// 공백제거
//		szNamespace  = szNamespace.replaceAll(" ", "");
//		
//		if(szNamespace.equalsIgnoreCase("")) {
//			model.addAttribute("returncode"		  , "0");
//			model.addAttribute("returndescription", "Fail");
//			model.addAttribute("errorcode"		  , "E00003");
//			model.addAttribute("errordescription" , "namespace 파라메터는 필수입력입니다.");
//			return new ModelAndView( "jsonView", model );
//		}
//		
//		/* 네임스페이스 정보 가져오기
//		 * CYD 2020.09.02
//		 *///////////////////////////////////////////////////
//
//		sb.append(gitlabArsenalHost)
//		  .append("api/v4/namespaces?search=")
//		  .append(szNamespace);
//		
//		szHostUrl = sb.toString();
//		LOG.debug("==== HostUrl = {} ", szHostUrl);
//		HashMap<String,String> map;
//		map = new HashMap<String,String>();
//		map.put("hostUrl"	   , szHostUrl);
//		map.put("methodType"   , szMethodType);
//		map.put("apiJsonParams", "");
//		
//		HashMap<String,String> projectMap = this.doApiResult(map);
//		LOG.debug("==== namespaceMap = {} ", projectMap);
//		
//		try {
//			jsonObject = (JSONObject)inputParse.parse(projectMap.get("jsonResponse"));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//			model.addAttribute("returncode"		  , "0");
//			model.addAttribute("returndescription", "Fail");
//			model.addAttribute("errorcode"		  , "E00006");
//			model.addAttribute("errordescription" , e.getMessage());
//
//			return new ModelAndView( "jsonView", model );
//		}
//		
//		szErrorCode = jsonObject.containsKey("errorCode") == true ? jsonObject.get("errorCode").toString() : "";
//		
//		if(szErrorCode.equalsIgnoreCase("404")) {
//			model.addAttribute("returncode"		  , "0");
//			model.addAttribute("returndescription", "Fail");
//			model.addAttribute("errorcode"		  , "E00004");
//			model.addAttribute("errordescription" , "네임스페이스가 존재하지 않습니다.");
//
//			return new ModelAndView( "jsonView", model );
//		}
//		
//		model.addAttribute("response", jsonObject);
//		
//		return new ModelAndView( "jsonView", model );
//	}
//	
//	/**
//	 * <pre>
//	 * 1. 메소드명 : getApiInfoAtStudio
//	 * 2. 작성일   : 2020. 07. 02
//	 * 3. 작성자   : CYD
//	 * 4. 설명     : 네임스페스에 등록된 API 갯수 및 최신 업데이트 날짜 조회
//	 * </pre>
//	 * @param session
//	 * @param request
//	 * @param response
//	 * @param model
//	 * @param inputData
//	 * @param accessToken
//	 * @return
//	 * @throws Exception
//	 */
//	@ResponseBody
//	@RequestMapping(value="/getApiInfoAtStudio", method={RequestMethod.POST, RequestMethod.GET})
//	public ModelAndView getApiInfoAtStudio(HttpSession session, HttpServletRequest request, HttpServletResponse response, ModelMap model, @RequestHeader(value="Access-token", required=false) String accessToken) throws Exception {
//		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
//		
//		String szNamespace;
//		String szAccessToken;
//		
//		szAccessToken = accessToken == null ? "" : accessToken;
//
//		if(szAccessToken.equalsIgnoreCase("") == true || szAccessToken.equalsIgnoreCase(gitlabArsenalAccessToken) == false) {
//			model.addAttribute("returncode"		  , "0");
//			model.addAttribute("returndescription", "Fail");
//			model.addAttribute("errorcode"		  , "E00001");
//			model.addAttribute("errordescription" , "토근 인증 오류입니다.");
//			return new ModelAndView( "jsonView", model );
//		}
//
////		JSONParser inputParse = new JSONParser();
////		JSONObject jsonObject = null;
////		try {
////			jsonObject = (JSONObject)inputParse.parse(inputData);
////		} catch (ParseException e) {
////			// TODO Auto-generated catch block
////			//e.printStackTrace();
////			model.addAttribute("returncode"		  , "0");
////			model.addAttribute("returndescription", "Fail");
////			model.addAttribute("errorcode"		  , "E00006");
////			model.addAttribute("errordescription" , e.getMessage());
////
////			return new ModelAndView( "jsonView", model );
////		}
//		
////		szNamespace = jsonObject.containsKey("namespace") == true ? jsonObject.get("namespace").toString() : "";
//		szNamespace = "";
//		// 공백제거
////		szNamespace = szNamespace.replaceAll(" ", "");
////		
////		if(szNamespace.equalsIgnoreCase("")) {
////			model.addAttribute("returncode"		  , "0");
////			model.addAttribute("returndescription", "Fail");
////			model.addAttribute("errorcode"		  , "E00003");
////			model.addAttribute("errordescription" , "namespace 파라메터는 필수입력입니다.");
////			return new ModelAndView( "jsonView", model );
////		}
//		
//		// 프로젝트코드 가져오기
//		EgovMap apiSpcMap = new EgovMap();
//		apiSpcMap.put("projectNS", szNamespace);
//		LOG.debug("==== EgovMap Data = {} ",apiSpcMap);
//
//		try {
//			// 네임스페스의 프로젝트 정보 가져오기
//			ArrayList<EgovMap> apiSpcInfoMap = apiArsenalService.selApiCountAndInfoByProjectNs(apiSpcMap);
//			
//			if(apiSpcInfoMap != null && apiSpcInfoMap.get(0).containsKey("apiCount"))
//			{
//				model.addAttribute("response", apiSpcInfoMap);
//			} else {
//				LOG.debug("==== Exception getApiInfoAtStudio ===== ");
//				model.addAttribute("returncode"		  , "0");
//				model.addAttribute("returndescription", "Fail");
//				model.addAttribute("errorcode"		  , "E00006");
//				model.addAttribute("errordescription" , "등록된 API정보가 없습니다");
//			}
//			
//			LOG.debug("==== Arsenal getApiInfoAtStudio = {} ", apiSpcInfoMap);
//			
//		} catch (Exception e) {
//			// TODO: handle exception
//			LOG.debug("==== Exception getApiInfoAtStudio = {} ", e.getStackTrace().toString());
//			model.addAttribute("returncode"		  , "0");
//			model.addAttribute("returndescription", "Fail");
//			model.addAttribute("errorcode"		  , "E00006");
//			model.addAttribute("errordescription" , e.getStackTrace());
//
//			return new ModelAndView( "jsonView", model );
//		}
//		
//		return new ModelAndView( "jsonView", model );
//	}
//	
//
//	/**
//	 * <pre>
//	 * 1. 메소드명 : syncApiFromArsenal
//	 * 2. 작성일   : 2020. 06. 22
//	 * 3. 작성자   : CYD
//	 * 4. 설명     : 아스날 API 역동기화 처리
//	 * </pre>
//	 * @param session
//	 * @param request
//	 * @param response
//	 * @param model
//	 * @param inputData
//	 * @param accessToken
//	 * @return
//	 * @throws Exception
//	 */
//	@SuppressWarnings("unchecked")
//	@ResponseBody
//	@RequestMapping(value="/syncApiFromArsenal", method=RequestMethod.POST)
//	public ModelAndView syncApiFromArsenal(HttpSession session, HttpServletRequest request, ModelMap model, @RequestBody String inputData, @RequestHeader(value="Access-token", required=false) String accessToken) throws Exception {
//		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
//
//		StringBuffer sb = new StringBuffer();
//		
//		String szHostUrl;
//		String szUserId;
//		String szProjectId;
//		String szProjectName;
//		String szNamespace;
//		String szUpTime;
//		String szMethodType;
//		String szErrorCode;
//		String szAccessToken;
//		
//		szAccessToken = accessToken == null ? "" : accessToken;
//		
//		if(szAccessToken.equalsIgnoreCase("") == true || szAccessToken.equalsIgnoreCase(gitlabArsenalAccessToken) == false) {
//			model.addAttribute("returncode"		  , "0");
//			model.addAttribute("returndescription", "Fail");
//			model.addAttribute("errorcode"		  , "E00001");
//			model.addAttribute("errordescription" , "토근 인증 오류입니다.");
//			return new ModelAndView( "jsonView", model );
//		}
//
//		HashMap<String,String> map;
//
//		JSONParser inputParse = new JSONParser();
//		JSONObject jsonObject = null;
//		try {
//			jsonObject = (JSONObject)inputParse.parse(inputData);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//			model.addAttribute("returncode"		  , "0");
//			model.addAttribute("returndescription", "Fail");
//			model.addAttribute("errorcode"		  , "E00006");
//			model.addAttribute("errordescription" , e.getMessage());
//
//			return new ModelAndView( "jsonView", model );
//		}
//		
//		szProjectName = jsonObject.containsKey("projectName") == true ? jsonObject.get("projectName").toString() : "";
//		szUserId      = jsonObject.containsKey("userId"   	) == true ? jsonObject.get("userId"   	).toString() : "";
//		szNamespace   = jsonObject.containsKey("namespace"	) == true ? jsonObject.get("namespace"	).toString() : "";
//		szUpTime   	  = jsonObject.containsKey("upTime"		) == true ? jsonObject.get("upTime"		).toString() : "";
//		szMethodType  = "1";
//		
//		// 공백제거
//		szProjectName = szProjectName.replaceAll(" ", "");
//		szNamespace   = szNamespace.replaceAll(" ", "");
//		szUserId 	  = szUserId.replaceAll(" ", "");
//		
//		if(szProjectName.equalsIgnoreCase("")) {
//			model.addAttribute("returncode"		  , "0");
//			model.addAttribute("returndescription", "Fail");
//			model.addAttribute("errorcode"		  , "E00003");
//			model.addAttribute("errordescription" , "projectName 파라메터는 필수입력입니다.");
//			return new ModelAndView( "jsonView", model );
//		}
//
//		if(szUserId.equalsIgnoreCase("")) {
//			model.addAttribute("returncode"		  , "0");
//			model.addAttribute("returndescription", "Fail");
//			model.addAttribute("errorcode"		  , "E00003");
//			model.addAttribute("errordescription" , "userId 파라메터는 필수입력입니다.");
//			return new ModelAndView( "jsonView", model );
//		}
//		
//		if(szNamespace.equalsIgnoreCase("")) {
//			model.addAttribute("returncode"		  , "0");
//			model.addAttribute("returndescription", "Fail");
//			model.addAttribute("errorcode"		  , "E00003");
//			model.addAttribute("errordescription" , "namespace 파라메터는 필수입력입니다.");
//			return new ModelAndView( "jsonView", model );
//		}
//		
//		if(szUpTime.equalsIgnoreCase("")) {
//			long nowTime = System.currentTimeMillis();
//			SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
//			szUpTime = dayTime.format(new Date(nowTime));
//		}
//		
//		// 네임스페스정보 셋업
//		EgovMap apiSpcMap = new EgovMap();
//		apiSpcMap.put("projectNS", szNamespace);
//		apiSpcMap.put("projectNM", szProjectName);
//		LOG.debug("==== EgovMap Data = {} ",apiSpcMap);
//		
//		String apiSpcNo;
//		String filePath;
//				
//		// 네임스페스의 프로젝트 API_SPC_NO 가져오기
//		EgovMap apiSpcInfoMap = apiArsenalService.selApiSpcInfoByProjectNsWithNm(apiSpcMap);
//		if(apiSpcInfoMap != null && apiSpcInfoMap.containsKey("apiSpcNo"))
//		{
//			apiSpcNo = apiSpcInfoMap.get("apiSpcNo"	   ).toString();
//			filePath = apiSpcInfoMap.get("yamlFilePath").toString();
//			LOG.debug("==== Yaml API_SPC_NO = {}, {} ",apiSpcNo, filePath);
//		} else {
//			model.addAttribute("returncode"		  , "0");
//			model.addAttribute("returndescription", "Fail");
//			model.addAttribute("errorcode"		  , "E00004");
//			model.addAttribute("errordescription" , "프로젝트가 존재하지 않습니다.");
//			return new ModelAndView( "jsonView", model );
//		}
//		
//		/* 프로젝트명 가져오기
//		 * CYD 2020.06.22
//		 *///////////////////////////////////////////////////
//
//		sb.append(gitlabArsenalHost)
//		  .append("api/v4/projects/")
//		  .append(szNamespace)
//		  .append("%2F")
//		  .append(szProjectName);
//		
//		szHostUrl = sb.toString();
//		
//		map = new HashMap<String,String>();
//		map.put("hostUrl"	   , szHostUrl);
//		map.put("methodType"   , szMethodType);
//		map.put("apiJsonParams", "");
//		
//		HashMap<String,String> projectMap = this.doApiResult(map);
//		LOG.debug("==== projectMap = {} ", projectMap);
//		
//		try {
//			jsonObject = (JSONObject)inputParse.parse(projectMap.get("jsonResponse"));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//			model.addAttribute("returncode"		  , "0");
//			model.addAttribute("returndescription", "Fail");
//			model.addAttribute("errorcode"		  , "E00006");
//			model.addAttribute("errordescription" , e.getMessage());
//
//			return new ModelAndView( "jsonView", model );
//		}
//		
//		szErrorCode = jsonObject.containsKey("errorCode") == true ? jsonObject.get("errorCode").toString() : "";
//		
//		if(szErrorCode.equalsIgnoreCase("404")) {
//			model.addAttribute("returncode"		  , "0");
//			model.addAttribute("returndescription", "Fail");
//			model.addAttribute("errorcode"		  , "E00004");
//			model.addAttribute("errordescription" , "프로젝트가 존재하지 않습니다.");
//
//			return new ModelAndView( "jsonView", model );
//		}
//		
//		szProjectId = jsonObject.containsKey("id") == true ? jsonObject.get("id").toString() : "";
//		
//		/////////////////////////////////////////////////////
//
//		sb.setLength(0); // 초기화
//		sb.append(gitlabArsenalHost)
//		  .append("api/v4/projects/")
//		  .append(szProjectId)
//		  .append("/repository/files/devops%2Fswagger%2F")
//		  .append(szProjectName)
//		  .append("%2Eyaml?ref=master");
//		
//		szHostUrl = sb.toString();
//
//		map.put("hostUrl", szHostUrl);
//
//		//LOG.debug("==== doApiResult() = {} " , this.doApiResult(map));
//		projectMap = this.doApiResult(map);
//		LOG.debug("==== projectMap = {} ", projectMap);
//		
//		try {
//			jsonObject = (JSONObject)inputParse.parse(projectMap.get("jsonResponse"));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//			model.addAttribute("returncode"		  , "0");
//			model.addAttribute("returndescription", "Fail");
//			model.addAttribute("errorcode"		  , "E00006");
//			model.addAttribute("errordescription" , e.getMessage());
//
//			return new ModelAndView( "jsonView", model );
//		}
//		
//		// Yaml 파일이 존재하지 않을 경우
//		szErrorCode = jsonObject.containsKey("errorCode") == true ? jsonObject.get("errorCode").toString() : "";
//		
//		if(szErrorCode.equalsIgnoreCase("404")) {
//			model.addAttribute("returncode"		  , "0");
//			model.addAttribute("returndescription", "Fail");
//			model.addAttribute("errorcode"		  , "E00006");
//			model.addAttribute("errordescription" , szProjectName + ".yaml 파일이 존재하지 않습니다.");
//
//			return new ModelAndView( "jsonView", model );
//		}
//		
//		String szContent = jsonObject.containsKey("content") == true ? jsonObject.get("content").toString() : "";
//		
//		// Base64 Decoding
//		String szDecodeContent = getDecoded(szContent);
//		
//		/* YAML String Parser
//		 * 
//		 *//////////////////////////////////////////////////////////////////
//		ObjectMapper objMapper = new ObjectMapper(new YAMLFactory());
//
//		HashMap<String, Object> config = objMapper.readValue(szDecodeContent, HashMap.class);
//		//config.put("x-namespace", "edu40");
//		// x-namespace: 추가
//		//String contentYaml = objMapper.writeValueAsString(szDecodeContent + "x-namespace: edu40");
//		
//		LOG.debug("==== Yaml Info = {} " ,config.get("info"));
//		LOG.debug("==== Yaml Info2 = {} ",config.get("paths"));
//		LOG.debug("==== Yaml Info3 = {} ",szDecodeContent);
//		LOG.debug("==== Yaml Info4 = {} ",config.get("x-category"));
//		/////////////////////////////////////////////////////////////////////
//		
//		/* YAML 데이터 처리
//		 * 
//		 *///////////////////////////////////////////////////////////////////
//		
//		/** PATH START */
//		
//		/*
//		 * HashMap<String,Object> pathsMap =
//		 * (HashMap<String,Object>)config.get("paths");
//		 * 
//		 * for(Entry<String, Object> eMap : pathsMap.entrySet()) { String pathsName =
//		 * eMap.getKey(); HashMap<String,Object> childPathMap =
//		 * (HashMap<String,Object>)eMap.getValue();
//		 * LOG.debug("==== Yaml Path Info1 \n {}={} " ,pathsName, childPathMap);
//		 * 
//		 * for(Entry<String, Object> eMap1 : childPathMap.entrySet()) { String
//		 * pathsName1 = eMap1.getKey(); HashMap<String,Object> childPathMap1 =
//		 * (HashMap<String,Object>)eMap1.getValue();
//		 * LOG.debug("==== Yaml Path Info2 \n {}={} " ,pathsName1, childPathMap1);
//		 * 
//		 * LOG.debug("==== Yaml Path Info3 : {}={} " ,"summary",
//		 * childPathMap1.get("summary").toString()); for(Entry<String, Object> eMap2 :
//		 * childPathMap1.entrySet()) { String pathsName2 = eMap2.getKey();
//		 * //HashMap<String,Object> childPathMap2 =
//		 * (HashMap<String,Object>)eMap2.getValue();
//		 * LOG.debug("==== Yaml Path Info3 : {}={} " ,pathsName2, eMap2.getValue()); } }
//		 * }
//		 */
//		HashMap<String,Object> cateMap = (HashMap<String,Object>)config.get("x-category");
//		
//		String categoryNm = "";//((Entry<String, Object>)cateMap.entrySet()).getKey().toString();
//		for(Entry<String, Object> eMap : cateMap.entrySet()) { 
//			categoryNm = eMap.getKey();
//		}
//		LOG.debug("==== Yaml Info4 = {} ",categoryNm);
//		
//		ApiRegVO vo = new ApiRegVO();
//		vo.setApiSpcNo(apiSpcNo);
//		vo.setApiCtgryNm(categoryNm);
//		vo.setRegr(CommonFunc.safeDbEncrypt(szUserId));
//		vo.setYamlSbst(szDecodeContent);
//		// API 데이터 추가
//		apiArsenalService.syncApiDep(config, vo);
//
//		// SAVE TO FILE OF YAML
//		apiRegService.savYamlFile(filePath, apiSpcNo, szDecodeContent);
//		
//		//LOG.debug("==== SafeDbDecrypt : {}" ,CommonFunc.safeDbDecrypt("0001c5yLnbA77U+HQeFdHTascQ=="));
//		
//		
//		/////////////////////////////////////////////////////////////////////
//		
//		LOG.debug("==== Header Access-token = {} ",szAccessToken);
//		/* YAML String Parser
//		 * 
//		 *///////////////////////////////////////////////////////////////////
//		
//		/*sb.setLength(0); // 초기화
//		sb.append(gitlabArsenalHost)
//		  .append("api/v4/projects/")
//		  .append(szProjectId)
//		  .append("/repository/files/devops%2Fswagger%2F")
//		  .append(szProjectName)
//		  .append("_%2Eyaml");
//		
//		szHostUrl = sb.toString();
//		
//		map.clear();
//		map.put("hostUrl", szHostUrl);
//		map.put("methodType", "2");
//		map.put("apiJsonParams", "{\"branch\":\"master\",\"content\":\""+szDecodeContent+"\",\"commit_message\":\"create a new file\"}");
//		
//		
//		YAMLFactory yamlFactory = new YAMLFactory(new ObjectMapper());
//		YAMLParser aa = yamlFactory.createParser(contentYaml);
//		
//		LOG.debug("==== Yaml Info4 = {} ",this.doApiResult(map));*/
//		
//		//Yaml yaml = new Yaml();
//		//StringWriter writer = new StringWriter();
//		//contentYaml = yaml.dump(config);
//		/////////////////////////////////////////////////////////////////////
//		
//		model.addAttribute("returncode"		  , "1");
//		model.addAttribute("returndescription", "Success");
//		//model.addAttribute("errorcode"		  , "");
//		//model.addAttribute("errordescription" , "");
//		
//		//model.addAttribute("info", this.doApiResult(map));
//		//model.addAttribute("Content", szDecodeContent);
//		//model.addAttribute("projectId",szProjectId);
//
//		return new ModelAndView( "jsonView", model );
//	}
//
//
//	/**
//	* exportApiToGitlab
//	* CYD 2020.06.22
//	*/
//	@ResponseBody
//	@RequestMapping(value="/exportApiToGitlab", method=RequestMethod.POST)
//	public ModelAndView exportApiToGitlab(HttpSession session, HttpServletRequest request, ModelMap model, @RequestBody String inputData) throws Exception {
//		LOG.debug("#######################  ApiArsenalController exportApiToGitlab START ############################");
//		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
//		//$("#idGitlabArsenalHost").val() + "api/v4/projects/" + g_szProjectId + "/repository/files/devops%2Fswagger%2F"
//		//g_projectName + "%2Eyaml?ref=master"
//		StringBuffer sb = new StringBuffer();
//		
//		String szHostUrl;
//		String szProjectId;
//		String szProjectName;
//		String szContent;
//		String szBranch;
//		String szCommitMessage;
//		String szMethodType;
//		String szApiSpcNo;
//		String jsonRequest = "{}";
//		String szUserId;
//		HashMap<String,String> map;
//		
//		JSONParser inputParse = new JSONParser();
//		JSONObject jsonObject = null;
//		try {
//			jsonObject = (JSONObject)inputParse.parse(inputData);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		szProjectName 	= jsonObject.containsKey("projectName"	 ) == true ? jsonObject.get("projectName"	).toString() : "";
//		szProjectId   	= jsonObject.containsKey("projectId"	 ) == true ? jsonObject.get("projectId"		).toString() : "";
//		szBranch     	= jsonObject.containsKey("branch"   	 ) == true ? jsonObject.get("branch"   		).toString() : "";
//		szContent     	= jsonObject.containsKey("content"   	 ) == true ? jsonObject.get("content"   	).toString() : "";
//		szCommitMessage = jsonObject.containsKey("commit_message") == true ? jsonObject.get("commit_message").toString() : "";
//		szMethodType  	= jsonObject.containsKey("methodType" 	 ) == true ? jsonObject.get("methodType" 	).toString() : "1";
//		szApiSpcNo 		= jsonObject.containsKey("apiSpcNo"		 ) == true ? jsonObject.get("apiSpcNo"		).toString() : "";
//		
//		sb.append(gitlabArsenalHost)
//		  .append("api/v4/projects/")
//		  .append(szProjectId)
//		  .append("/repository/files/devops%2Fswagger%2F")
//		  .append(szProjectName)
//		  .append("%2Eyaml");
//		
//		szHostUrl = sb.toString();
//
//		map = new HashMap<String,String>();
//		map.put("hostUrl"	   , szHostUrl);
//		map.put("methodType"   , szMethodType);
//		map.put("apiJsonParams", jsonObject.toJSONString());
//
//		LOG.debug("==== ExportApiToGitlab doApiResult() Start = {} " , map);
//		HashMap<String,String> resultMap = this.doApiResult(map);
//		LOG.debug("==== ExportApiToGitlab Session Start = {} " , session.getAttribute("ssUserVo"));
//		UserJoinVO userJoinVo = (UserJoinVO)session.getAttribute("ssUserVo");
//		szUserId = userJoinVo.getEnCmbrId();
//		
//		LOG.debug("==== ExportApiToGitlab Session ID = {} " , szUserId);
//		try {
//			if(resultMap != null && resultMap.get("returnCode").equalsIgnoreCase("1")) {
//				// Update history
//				ApiRegVO vo = new ApiRegVO();
//				vo.setApiSpcNo(szApiSpcNo);
//				vo.setApiSttusCd("APISYN1010");
//				vo.setDelr("S");
//				vo.setMemo("-");
//				vo.setRegr(szUserId);
//				apiRegDAO.updApiHisInfo(vo);
//			} else {
//				// Update history
//				ApiRegVO vo = new ApiRegVO();
//				vo.setApiSpcNo(szApiSpcNo);
//				vo.setApiSttusCd("APISYN1020");
//				vo.setDelr("S");
//				vo.setMemo(resultMap.get("jsonResponse").toString());
//				vo.setRegr(szUserId);
//				apiRegDAO.updApiHisInfo(vo);
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			LOG.debug("==== Exception = {} " , e.getStackTrace().toString());
//			e.printStackTrace();
//		}
//		
//		
//		LOG.debug("==== ExportApiToGitlab doApiResult() End = {} " , resultMap);
//		model.addAttribute("info", resultMap);
//
//		return new ModelAndView( "jsonView", model );
//	}
//	
//	
//	
//	/**
//	* 
//	* 
//	*/
//	@ResponseBody
//	@RequestMapping(value="/getFileFromGitlabAjax", method=RequestMethod.POST)
//	public ModelAndView getFileFromGitlabAjax(HttpServletRequest request, ModelMap model, @RequestBody String inputData) throws Exception {
//		LOG.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
//		//$("#idGitlabArsenalHost").val() + "api/v4/projects/" + g_szProjectId + "/repository/files/devops%2Fswagger%2F"
//		//g_projectName + "%2Eyaml?ref=master"
//		StringBuffer sb = new StringBuffer();
//		
//		String szHostUrl;
//		String szNamespace;
//		String szProjectId;
//		String szProjectName;
//		String szMethodType;
//		HashMap<String,String> map;
//		
//		JSONParser inputParse = new JSONParser();
//		JSONObject jsonObject = null;
//		try {
//			jsonObject = (JSONObject)inputParse.parse(inputData);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		szProjectName = jsonObject.containsKey("projectName") == true ? jsonObject.get("projectName").toString() : "";
//		szNamespace   = jsonObject.containsKey("namespace"  ) == true ? jsonObject.get("namespace"  ).toString() : "";
//		//szProjectId   = jsonObject.containsKey("projectId"  ) == true ? jsonObject.get("projectId"  ).toString() : "";
//		szMethodType  = jsonObject.containsKey("methodType" ) == true ? jsonObject.get("methodType" ).toString() : "1";
//		
//		/* 프로젝트ID 가져오기
//		 * CYD 2020.06.01
//		 *///////////////////////////////////////////////////
//
//		sb.append(gitlabArsenalHost)
//		  .append("api/v4/projects/")
//		  .append(szNamespace)
//		  .append("%2F")
//		  .append(szProjectName);
//		
//		szHostUrl = sb.toString();
//		
//		map = new HashMap<String,String>();
//		map.put("hostUrl"	   , szHostUrl);
//		map.put("methodType"   , szMethodType);
//		map.put("apiJsonParams", "");
//		
//		HashMap<String,String> projectMap = this.doApiResult(map);
//		LOG.debug("==== projectMap = {} ", projectMap);
//		
//		try {
//			jsonObject = (JSONObject)inputParse.parse(projectMap.get("jsonResponse"));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		szProjectId = jsonObject.containsKey("id") == true ? jsonObject.get("id").toString() : "";
//
//		/////////////////////////////////////////////////////
//		
//		sb.setLength(0); // 초기화
//		sb.append(gitlabArsenalHost)
//		  .append("api/v4/projects/")
//		  .append(szProjectId)
//		  .append("/repository/files/devops%2Fswagger%2F")
//		  .append(szProjectName)
//		  .append("%2Eyaml?ref=master");
//		
//		szHostUrl = sb.toString();
//
//		//UserJoinVO userJVo = (UserJoinVO)session.getAttribute("ssUserVo");
//		
//		//HashMap<String,String> map = new HashMap<String,String>();
//		map.put("hostUrl", szHostUrl);
//		//map.put("methodType", szMethodType);
//		//map.put("apiJsonParams", "");
//
//		LOG.debug("==== doApiResult() = {} " , this.doApiResult(map));
//		model.addAttribute("info"	  , this.doApiResult(map));
//		model.addAttribute("projectId", szProjectId);
//
//		return new ModelAndView( "jsonView", model );
//	}
//	
//	
//	// SHUB Api 호출
//	private HashMap<String,String> doApiResult(HashMap<String,String> map) {
//		String response	 	 = null;
//		String returnCode	 = "0";
//		String hostUrl 		 = map.get("hostUrl").toString();
//		String methodType 	 = map.get("methodType").toString();
//		String apiJosnParams = map.get("apiJsonParams").toString();
//			
//		HashMap<String,String> mapResponse = new HashMap<String,String>();
//			
//		//URL, API가저오기
//		ksmShubApiClient.setApiUrl(hostUrl);
//		ksmShubApiClient.setApiHeader("PRIVATE-TOKEN", gitlabArsenalPrivateToken);
//
//		if(methodType.equalsIgnoreCase("1")) {
//			ksmShubApiClient.setApiMethod(KsmApiClient.METHOD_GET);
//		} else if(methodType.equalsIgnoreCase("2")) {
//			ksmShubApiClient.setApiMethod(KsmApiClient.METHOD_POST);
//		} else if(methodType.equalsIgnoreCase("3")) {
//			ksmShubApiClient.setApiMethod(KsmApiClient.METHOD_PUT);
//		}
//		
//		ksmShubApiClient.setApiParamsJson(apiJosnParams);
//
//		boolean bSuccess = false;
//		bSuccess = ksmShubApiClient.sendRestRequest();
//				
//		if(bSuccess) {
//			if(ksmShubApiClient.getHttpResponse().getCode() == 200 || ksmShubApiClient.getHttpResponse().getCode() == 201) {
//				returnCode = "1";
//				if(methodType.equalsIgnoreCase("1")) {
//					response = ksmShubApiClient.getApiResult().getJsonString();
//
//					if(response.equalsIgnoreCase("{}")) {
//						response = ksmShubApiClient.getHttpResponse().getBody();
//						response = response.replace("[", "");
//						response = response.replace("]", "");
//						if(response.equalsIgnoreCase("")) {
//							returnCode = "0";
//							response = "{\"errorCode\":\"404\"}";
//						}
//					}
//					LOG.debug("==== response() = {} " , response);
//				} else {
//					if( ksmShubApiClient.getApiResult().getJsonString().equals("{}")) {
//						response = apiJosnParams;
//					} else {
//						response = ksmShubApiClient.getApiResult().getJsonString();
//					}
//				}
//			} else {
//				//response = "404 Not Found";
//				response = "{\"errorCode\":\"" + String.valueOf(ksmShubApiClient.getHttpResponse().getCode()) + "\"}";
//			}
//		}else {
//			response = "{\"errorCode\":\"-1\"}";
//		}
//		
//		response = response.replace("{", "{\r\t");
//		response = response.replace("\",", "\",\r\t");
//		response = response.replace("}", "\r}");
//
//		mapResponse.put("returnCode"  , returnCode);
//		mapResponse.put("jsonResponse", response);
//				
//		return mapResponse;
//	}
	
	/* BASE64 Decoding Method
	 * 
	 */
	private String getDecoded(String content) {
		String szDecodeContent = "";
		Decoder decoder = Base64.getDecoder();
		byte[] decodedByte = decoder.decode(content);
		
		try {
			szDecodeContent = new String(decodedByte,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return szDecodeContent;
	}
	
	

}
