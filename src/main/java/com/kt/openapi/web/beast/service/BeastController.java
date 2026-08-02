package com.kt.openapi.web.beast.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.kt.openapi.web.adptran.util.KsmUtil;
import com.kt.openapi.web.adptran.vo.BstApiDeployVO;
import com.kt.openapi.web.beast.apigw.constant.BstgwConstant;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import com.kt.openapi.web.util.CommonFunc;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 * [마이그레이션] EgovMap 제거 및 VO 전환
 */
@Controller
@RequestMapping(value="/beast")
public class BeastController {
	private static final Logger LOGGER = LoggerFactory.getLogger(BeastController.class);

	@Autowired
	private BeastService beastService;

	//-- for beast apigwmng {
	@RequestMapping(value = "/apigwmng/{pathVal}/tb", method = RequestMethod.GET)
	public ModelAndView beastApigwmngTb(HttpServletRequest request, ModelAndView mv, @PathVariable(value="pathVal") String pathVal) throws Exception {
		LOGGER.debug("\n\n### {}.{}() [request.getServletPath(): {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), request.getServletPath());

		ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = servletRequestAttribute.getRequest().getSession(true);
		UserJoinVO ssUserVo = (UserJoinVO)session.getAttribute("ssUserVo");
		
		String ssMbrId = KsmUtil.fnSafeStr(ssUserVo.getMbrId());
		
		if("APILink1".equals(ssMbrId)) {
			mv.addObject("excel", "download");
		}
    //-- [tag:SR-20230706]
    //-- [i][접근권한check]
    boolean bIsBstgwManager = CommonFunc.isSpecificUser("bstgw.manager");
    boolean bIsBstgwViewer = CommonFunc.isSpecificUser("bstgw.viewer");
    if (false == (bIsBstgwManager || bIsBstgwViewer)) {
      mv.setViewName("redirect:/");
      return mv;
    }

		//-- [i][target: PRD, TB구분]
		mv.addObject("attr_target", BstgwConstant.PROFILE.TB);

		mv.setViewName("beast/apigwmng/" + pathVal);
		return mv;
	}
	
	@RequestMapping(value = "/apigwmng/{pathVal}", method = RequestMethod.GET)
	public ModelAndView beastApigwmng(HttpServletRequest request, ModelAndView mv, @PathVariable(value="pathVal") String pathVal, String target) throws Exception {
		LOGGER.debug("\n\n### {}.{}() [request.getServletPath(): {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), request.getServletPath());
		
		ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = servletRequestAttribute.getRequest().getSession(true);
		UserJoinVO ssUserVo = (UserJoinVO)session.getAttribute("ssUserVo");
		
		String ssMbrId = KsmUtil.fnSafeStr(ssUserVo.getMbrId());
		
		if("APILink1".equals(ssMbrId)) {
			mv.addObject("excel", "download");
		}
    //-- [tag:SR-20230706]
    //-- [i][접근권한check]
		boolean bIsBstgwManager = CommonFunc.isSpecificUser("bstgw.manager");
		boolean bIsBstgwViewer = CommonFunc.isSpecificUser("bstgw.viewer");
		if (false == (bIsBstgwManager || bIsBstgwViewer)) {
      mv.setViewName("redirect:/");
			return mv;
		}

		//-- [i][target: PRD, TB구분]
		mv.addObject("attr_target", BstgwConstant.PROFILE.PRD);

		mv.setViewName("beast/apigwmng/" + pathVal);
		return mv;
	}

	@RequestMapping(value = "/apigwmng/{pathVal}/{target}", method = RequestMethod.GET)
	public ModelAndView beastApigwmngTarget(HttpServletRequest request, ModelAndView mv, @PathVariable(value="pathVal") String pathVal, @PathVariable(value="target") String target) throws Exception {
		LOGGER.debug("\n\n### {}.{}() [request.getServletPath(): {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), request.getServletPath());

		ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = servletRequestAttribute.getRequest().getSession(true);
		UserJoinVO ssUserVo = (UserJoinVO)session.getAttribute("ssUserVo");

		String ssMbrId = KsmUtil.fnSafeStr(ssUserVo.getMbrId());

		if("APILink1".equals(ssMbrId)) {
			mv.addObject("excel", "download");
		}
		boolean bIsBstgwManager = CommonFunc.isSpecificUser("bstgw.manager");
		boolean bIsBstgwViewer = CommonFunc.isSpecificUser("bstgw.viewer");
		if (false == (bIsBstgwManager || bIsBstgwViewer)) {
			mv.setViewName("redirect:/");
			return mv;
		}

		//-- [i][target: PRD, TB, PRD_AZURE, TB_AZURE 구분]
		mv.addObject("attr_target", target);

		mv.setViewName("beast/apigwmng/" + pathVal);
		return mv;
	}
	//-- for beast apigwmng }

	//-- for beast front-service {
	@RequestMapping(value = "/deploy/mvDeployList.do")
	public ModelAndView mvDeployList(HttpServletRequest request, ModelAndView mv) {
		LOGGER.debug("\n\n### {}.{}() [request.getServletPath(): {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), request.getServletPath());

		mv.setViewName("beast/deploy/deployList");
		return mv;

	}
	
	@RequestMapping(value = "/deploy/mvDeployView.do")
	public ModelAndView mvDeployView(HttpServletRequest request, ModelAndView mv) {
		LOGGER.debug("\n\n### {}.{}() [request.getServletPath(): {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), request.getServletPath());

	    int req_apiNo = KsmUtil.parseInt(request.getParameter("apiNo"), -1);

		BstApiDeployVO vo_out = null;
		try {
			vo_out = beastService.selDeployView(req_apiNo);
		} catch (Exception e) {
			LOGGER.error("\n\n### {}.{}() [selDeployView()][Message: {}][Exception: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
		}
		mv.addObject("item_api", vo_out);

		mv.setViewName("beast/deploy/deployView");
		return mv;

	}

	@RequestMapping(value = "/deploy/mvVerifyExecute.do")
	public ModelAndView mvVerifyExecute(HttpServletRequest request, ModelAndView mv) {
		LOGGER.debug("\n\n### {}.{}() [request.getServletPath(): {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), request.getServletPath());

		int req_apiNo = KsmUtil.parseInt(request.getParameter("apiNo"), -1);

		mv.addObject("apiNo", req_apiNo);

		mv.setViewName("beast/deploy/verifyExecute");
		return mv;

	}
	//-- for beast front-service }

	@RequestMapping(value = "/api/{pathVal}/ajax_query.do")
	public ModelAndView ajaxQuery(HttpServletRequest request, @PathVariable(value="pathVal") String pathVal) {
		LOGGER.debug("\n\n### {}.{}() [request.getServletPath(): {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), request.getServletPath());

		ModelMap model = beastService.beastApiAjaxQuery(request, pathVal);
		return new ModelAndView("jsonView", model);
	}

	@RequestMapping(value= "/api/{pathVal}/ajax_proc.do" )
	public ModelAndView ajaxProc(HttpServletRequest request, @PathVariable(value="pathVal") String pathVal, @RequestBody String requestBody) throws JsonMappingException, JsonProcessingException {
		LOGGER.debug("\n\n### {}.{}() [request.getServletPath(): {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), request.getServletPath());

		ModelMap model = beastService.beastApiAjaxProc(request, pathVal, requestBody);
		return new ModelAndView("jsonView", model);
	}

	//-- [i][below_for_dev]
	//-- [i][dev beast api service simulate]
	@RequestMapping(value="/apilink/v1/{apiTarget}/{apiName}")
	@ResponseBody
	public String apilinkV1(HttpServletRequest request, HttpServletResponse response, ModelMap model, @PathVariable(value="apiTarget") String apiTarget, @PathVariable(value="apiName") String apiName, @RequestBody(required=false) String requestBody) {
		LOGGER.debug("\n\n### {}.{}() [request.getServletPath(): {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), request.getServletPath());

		/*
		//-- [i][for reflect request payload]
		JSONObject jso_response = JSONObject.fromObject(requestBody);
		jso_response = (((null == jso_response) || jso_response.isNullObject()) ? (new JSONObject()) : jso_response);
		*/
		String method = request.getMethod();
		
		JSONObject jso_response = new JSONObject();
		JSONObject jso_common = new JSONObject();
		JSONObject jso_data = new JSONObject();

		int common_code = 404;
		String common_message = "Data Not Found";

		String xAgwTxId = KsmUtil.fnSafeStr(request.getHeader("X-AGW-TX-ID"));
		//-- [i][sparrow collection // replaceAll()]
		xAgwTxId = xAgwTxId.replaceAll("\r", "").replaceAll("\n", "");
		response.setHeader("X-AGW-TX-ID", xAgwTxId);

		if ("sys".equals(apiTarget)) {
			if ("getSysDplyList".equals(apiName) && "GET".equals(method)) {
				common_code = 200;	common_message = "정상처리되었습니다";
				try {
					jso_data = JSONObject.fromObject(this.fnGetApiResDataEg(apiTarget, apiName, method));
				} catch (JSONException e) {
					LOGGER.error("\n\n### {}.{}() [Message: {}][JSONException: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
				}
			}
			else if ("getSysDplyById".equals(apiName) && "GET".equals(method)) {
				common_code = 200;	common_message = "정상처리되었습니다";
				jso_data = JSONObject.fromObject(this.fnGetApiResDataEg(apiTarget, apiName, method));
			}
			else if ("sysDply".equals(apiName) && "POST".equals(method)) {
				common_code = 200;	common_message = "정상처리되었습니다";
				jso_data = JSONObject.fromObject(this.fnGetApiResDataEg(apiTarget, apiName, method));
			}
		}
		else if ("api".equals(apiTarget)) {
			if ("getApiDplyList".equals(apiName) && "GET".equals(method)) {
				common_code = 200;	common_message = "정상처리되었습니다";
				try {
					jso_data = JSONObject.fromObject(this.fnGetApiResDataEg(apiTarget, apiName, method));
				} catch (JSONException e) {
					LOGGER.error("\n\n### {}.{}() [Message: {}][JSONException: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
				}
			}
			else if ("getApiDplyById".equals(apiName) && "GET".equals(method)) {
				common_code = 200;	common_message = "정상처리되었습니다";
				jso_data = JSONObject.fromObject(this.fnGetApiResDataEg(apiTarget, apiName, method));
			}
			else if ("apiDply".equals(apiName) && "POST".equals(method)) {
				common_code = 200;	common_message = "정상처리되었습니다";
				jso_data = JSONObject.fromObject(this.fnGetApiResDataEg(apiTarget, apiName, method));
			}
		}
		else if ("svc".equals(apiTarget)) {
			if ("getSvcDplyList".equals(apiName) && "GET".equals(method)) {
				common_code = 200;	common_message = "정상처리되었습니다";
				try {
					jso_data = JSONObject.fromObject(this.fnGetApiResDataEg(apiTarget, apiName, method));
				} catch (JSONException e) {
					LOGGER.error("\n\n### {}.{}() [Message: {}][JSONException: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
				}
			}
			else if ("getSvcDplyById".equals(apiName) && "GET".equals(method)) {
				common_code = 200;	common_message = "정상처리되었습니다";
				jso_data = JSONObject.fromObject(this.fnGetApiResDataEg(apiTarget, apiName, method));
			}
			else if ("svcDply".equals(apiName) && "POST".equals(method)) {
				common_code = 200;	common_message = "정상처리되었습니다";
				jso_data = JSONObject.fromObject(this.fnGetApiResDataEg(apiTarget, apiName, method));
			}
		}
		else if ("data".equals(apiTarget)) {
			if ("getApiLinkDataList".equals(apiName) && "GET".equals(method)) {
				common_code = 200;	common_message = "정상처리되었습니다";
				try {
					jso_data = JSONObject.fromObject(this.fnGetApiResDataEg(apiTarget, apiName, method));
				} catch (JSONException e) {
					LOGGER.error("\n\n### {}.{}() [Message: {}][JSONException: {}] ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage(), e);
				}
			}
			else if ("getApidataLinkByTypeId".equals(apiName) && "GET".equals(method)) {
				common_code = 200;	common_message = "정상처리되었습니다";
				jso_data = JSONObject.fromObject(this.fnGetApiResDataEg(apiTarget, apiName, method));
			}
			else if (("createApidataLink".equals(apiName) && "POST".equals(method))
				|| ("ApidataLink".equals(apiName) && "PUT".equals(method)) 
				|| ("ApidataLink".equals(apiName) && "DELETE".equals(method))) {
				common_code = 200;	common_message = "정상처리되었습니다";
				jso_data = JSONObject.fromObject(this.fnGetApiResDataEg(apiTarget, apiName, method));
			}
		}
		else {
			jso_response.put("apiTarget", apiTarget);
			jso_response.put("apiName", apiName);
		}
		
		jso_common.put("code", common_code);
		jso_common.put("message", common_message);
		jso_response.put("common", jso_common);
		jso_response.put("data", jso_data);

		return jso_response.toString();
	}
	
	private String fnGetApiResDataEg(String apiTarget, String apiName, String Method) {
		String s_data = "";
		if (("sys".equals(apiTarget) && "sysDply".equals(apiName)) 
			|| ("api".equals(apiTarget) && "apiDply".equals(apiName))
			|| ("svc".equals(apiTarget) && "svcDply".equals(apiName))
			|| ("data".equals(apiTarget) && "createApiLinkData".equals(apiName))
			|| ("data".equals(apiTarget) && "apiLinkData".equals(apiName))
		) {
			s_data += "{                                          ";
			s_data += "  'value': 'SUCCESS',                      ";
			s_data += "}                                          ";
		}
		else if ("sys".equals(apiTarget) && "getSysDplyList".equals(apiName)) {
			s_data += "{                                          ";
			s_data += "  'value': [                               ";
			s_data += "    {                                      ";
			s_data += "      'dplyDt': '2022-09-07T16:10:21.616', ";
			s_data += "      'dplyType': 'DPLY',                  ";
			s_data += "      'sysId': 'TESTSYSTEM_MTLS',          ";
			s_data += "      'sysNm': 'TESTSYSTEM_MTLS',          ";
			s_data += "      'sysCd': '',                         ";
			s_data += "      'apiLinkCd': '',                     ";
			s_data += "      'edpt': {                            ";
			s_data += "        'prot': 'MTLS',                    ";
			s_data += "        'atrib': {                         ";
			s_data += "          'url': [                         ";
			s_data += "            'hhttps://3.34.189.146'        ";
			s_data += "          ],                               ";
			s_data += "          'certi': '-----BEGIN CERTIFICATE-----\\n-----END CERTIFICATE KEY-----', ";
			s_data += "          'certiKey': '-----BEGIN PRIVATE KEY-----\\n-----END PRIVATE KEY-----',  ";
			s_data += "          'ecod': null,                    ";
			s_data += "          'addr': null,                    ";
			s_data += "          'minPool': null,                 ";
			s_data += "          'maxPool': null                  ";
			s_data += "        }                                  ";
			s_data += "      }                                    ";
			s_data += "    },                                     ";
			s_data += "    {                                      ";
			s_data += "      'dplyDt': '2022-09-07T16:07:24.895', ";
			s_data += "      'dplyType': 'DPLY',                  ";
			s_data += "      'sysId': 'TESTSYSTEM_SOCKET',        ";
			s_data += "      'sysNm': 'TESTSYSTEM_SOCKET',        ";
			s_data += "      'sysCd': '',                         ";
			s_data += "      'apiLinkCd': '',                     ";
			s_data += "      'edpt': {                            ";
			s_data += "        'prot': 'SOCKET',                  ";
			s_data += "        'atrib': {                         ";
			s_data += "          'url': null,                     ";
			s_data += "          'certi': null,                   ";
			s_data += "          'certiKey': null,                ";
			s_data += "          'ecod': 'UTF-8',                 ";
			s_data += "          'addr': [                        ";
			s_data += "            '127.0.0.1:9005',              ";
			s_data += "            '127.0.0.1:9006'               ";
			s_data += "          ],                               ";
			s_data += "          'minPool': 1,                    ";
			s_data += "          'maxPool': 10                    ";
			s_data += "        }                                  ";
			s_data += "      }                                    ";
			s_data += "    }                                      ";
			s_data += "  ]                                        ";
			s_data += "}                                          ";
		}
		else if ("sys".equals(apiTarget) && "getSysDplyById".equals(apiName)) {
			s_data += "{                                        ";
			s_data += "  'value': {                             ";
			s_data += "    'dplyDt': '2022-09-07T16:07:24.895', ";
			s_data += "    'dplyType': 'DPLY',                  ";
			s_data += "    'sysId': 'TESTSYSTEM_SOCKET',        ";
			s_data += "    'sysNm': 'TESTSYSTEM_SOCKET',        ";
			s_data += "    'sysCd': '',                         ";
			s_data += "    'apiLinkCd': '',                     ";
			s_data += "    'edpt': {                            ";
			s_data += "      'prot': 'SOCKET',                  ";
			s_data += "      'atrib': {                         ";
			s_data += "        'url': null,                     ";
			s_data += "        'certi': null,                   ";
			s_data += "        'certiKey': null,                ";
			s_data += "        'ecod': 'UTF-8',                 ";
			s_data += "        'addr': [                        ";
			s_data += "          '127.0.0.1:9005',              ";
			s_data += "          '127.0.0.1:9006'               ";
			s_data += "        ],                               ";
			s_data += "        'minPool': 1,                    ";
			s_data += "        'maxPool': 10                    ";
			s_data += "      }                                  ";
			s_data += "    }                                    ";
			s_data += "  }                                      ";
			s_data += "}                                        ";
		}
		else if ("api".equals(apiTarget) && "getApiDplyList".equals(apiName)) {
			s_data += "{                                          ";
			s_data += "  'value': [                               ";
			s_data += "    {                                      ";
			s_data += "      'dplyDt': '2022-09-07T16:31:55.027', ";
			s_data += "      'dplyType': 'DEL',                   ";
			s_data += "      'sysId': 'HRSYSTEM',                 ";
			s_data += "      'apiId': 'testApi',                  ";
			s_data += "      'ifNo': 'HRSYSTEM-0100',             ";
			s_data += "      'ver': 'v1',                         ";
			s_data += "      'meth': [                            ";
			s_data += "        'GET',                             ";
			s_data += "        'POST'                             ";
			s_data += "      ],                                   ";
			s_data += "      'in': '/v1/authHandlerTest',         ";
			s_data += "      'out': '/v1/authHandlerTest',        ";
			s_data += "      'reqHndlr': [                        ";
			s_data += "        'REQ.AUTH',                        ";
			s_data += "        'REQ.API-AUT',                     ";
			s_data += "        'REQ.SLA',                         ";
			s_data += "        'REQ.IP-ACES-AUTH'                 ";
			s_data += "      ],                                   ";
			s_data += "      'resHndlr': [],                      ";
			s_data += "      'errHndlr': '',                      ";
			s_data += "      'timeOut': 10000,                    ";
			s_data += "      'prnts': true,                       ";
			s_data += "      'prntsApiId': [                      ";
			s_data += "        'getCustInfo',                     ";
			s_data += "        'getMapInfo',                      ";
			s_data += "        'postInfo'                         ";
			s_data += "      ],                                   ";
			s_data += "      'hndlrOptn': null,                   ";
			s_data += "      'mask': [],                          ";
			s_data += "      'atrib': {                           ";
			s_data += "        'inFmt': 'KHUB',                   ";
			s_data += "        'outFmt': 'SDP',                   ";
			s_data += "        'inComnParam': '',                 ";
			s_data += "        'outComnParam': ''                 ";
			s_data += "      }                                    ";
			s_data += "    },                                     ";
			s_data += "    {                                      ";
			s_data += "      'dplyDt': '2022-09-07T16:31:08.597', ";
			s_data += "      'dplyType': 'DPLY',                  ";
			s_data += "      'sysId': 'UTSOCKETPROT3',            ";
			s_data += "      'apiId': 'socketProtTest3',          ";
			s_data += "      'ifNo': 'UTSYSTEM-0103',             ";
			s_data += "      'ver': 'v1',                         ";
			s_data += "      'meth': [                            ";
			s_data += "        'POST'                             ";
			s_data += "      ],                                   ";
			s_data += "      'in': '/socketProtTest3',            ";
			s_data += "      'out': '/socketProtTest3',           ";
			s_data += "      'reqHndlr': [],                      ";
			s_data += "      'resHndlr': [],                      ";
			s_data += "      'errHndlr': '',                      ";
			s_data += "      'timeOut': 10000,                    ";
			s_data += "      'prnts': false,                      ";
			s_data += "      'prntsApiId': [],                    ";
			s_data += "      'hndlrOptn': null,                   ";
			s_data += "      'mask': [],                          ";
			s_data += "      'atrib': {                           ";
			s_data += "        'inFmt': '',                       ";
			s_data += "        'outFmt': '',                      ";
			s_data += "        'inComnParam': '',                 ";
			s_data += "        'outComnParam': ''                 ";
			s_data += "      }                                    ";
			s_data += "    }                                      ";
			s_data += "  ]                                        ";
			s_data += "}                                          ";
		}
		else if ("api".equals(apiTarget) && "getApiDplyById".equals(apiName)) {
			s_data += "{                                        ";
			s_data += "  'value': {                             ";
			s_data += "    'dplyDt': '2022-09-07T16:31:55.027', ";
			s_data += "    'dplyType': 'DEL',                   ";
			s_data += "    'sysId': 'HRSYSTEM',                 ";
			s_data += "    'apiId': 'testApi',                  ";
			s_data += "    'ifNo': 'HRSYSTEM-0100',             ";
			s_data += "    'ver': 'v1',                         ";
			s_data += "    'meth': [                            ";
			s_data += "      'GET',                             ";
			s_data += "      'POST'                             ";
			s_data += "    ],                                   ";
			s_data += "    'in': '/v1/authHandlerTest',         ";
			s_data += "    'out': '/v1/authHandlerTest',        ";
			s_data += "    'reqHndlr': [                        ";
			s_data += "      'REQ.AUTH',                        ";
			s_data += "      'REQ.API-AUT',                     ";
			s_data += "      'REQ.SLA',                         ";
			s_data += "      'REQ.IP-ACES-AUTH'                 ";
			s_data += "    ],                                   ";
			s_data += "    'resHndlr': [],                      ";
			s_data += "    'errHndlr': '',                      ";
			s_data += "    'timeOut': 10000,                    ";
			s_data += "    'prnts': true,                       ";
			s_data += "    'prntsApiId': [                      ";
			s_data += "      'getCustInfo',                     ";
			s_data += "      'getMapInfo',                      ";
			s_data += "      'postInfo'                         ";
			s_data += "    ],                                   ";
			s_data += "    'hndlrOptn': null,                   ";
			s_data += "    'mask': [],                          ";
			s_data += "    'atrib': {                           ";
			s_data += "      'inFmt': 'KHUB',                   ";
			s_data += "      'outFmt': 'SDP',                   ";
			s_data += "      'inComnParam': '',                 ";
			s_data += "      'outComnParam': ''                 ";
			s_data += "    }                                    ";
			s_data += "  }                                      ";
			s_data += "}                                        ";
		}
		else if ("svc".equals(apiTarget) && "getSvcDplyList".equals(apiName)) {
			s_data += "{                                                       ";
			s_data += "  'value': [                                            ";
			s_data += "    {                                                   ";
			s_data += "      'dplyDt': '2022-09-07T16:39:00.867',              ";
			s_data += "      'dplyType': 'DEL',                                ";
			s_data += "      'svcId': 'KTDS0907',                              ";
			s_data += "      'svcNm': 'KTDS0907Nm',                            ";
			s_data += "      'userNm': 'KTDS',                                 ";
			s_data += "      'sla': {                                          ";
			s_data += "        'sec': 3,                                       ";
			s_data += "        'min': 5,                                       ";
			s_data += "        'hr': null,                                     ";
			s_data += "        'day': null,                                    ";
			s_data += "        'mon': null                                     ";
			s_data += "      },                                                ";
			s_data += "      'svcStDt': '2022-07-12T00:00:00',                 ";
			s_data += "      'svcEndDt': '2099-07-31T23:59:59',                ";
			s_data += "      'apiAut': [                                       ";
			s_data += "        'HRSYSTEM.KHUB',                                ";
			s_data += "        'SB.MESG_MTSMSOPE',                             ";
			s_data += "        'HRSYSTEM.MESG',                                ";
			s_data += "        'SB.MessageMTMMSReportNoNetCharge',             ";
			s_data += "        'HRSYSTEM.Auth11',                              ";
			s_data += "        'DBC.Auth_DcbPost',                             ";
			s_data += "        'HRSYSTEM.SubscriptionInfoRetrievalManagerSSL', ";
			s_data += "        'SHUB.getSpecificSubsAndUserInfo',              ";
			s_data += "        'HRSYSTEM.getEapAKA',                           ";
			s_data += "        'SB.getCarrierSpaceUpdates',                    ";
			s_data += "        'SB.checkCustomerIDByIpForECNV_RestPost',       ";
			s_data += "        'HRSYSTEM.getApiNmPre_v1_RestPost',             ";
			s_data += "        'HRSYSTEM.authHandlerTest'                      ";
			s_data += "      ],                                                ";
			s_data += "      'ipAcesAut': {                                    ";
			s_data += "        'alwdIp': [],                                   ";
			s_data += "        'blckIp': []                                    ";
			s_data += "      },                                                ";
			s_data += "      'atrib': {                                        ";
			s_data += "        'cpId': '',                                     ";
			s_data += "        'serviceId': ''                                 ";
			s_data += "      }                                                 ";
			s_data += "    },                                                  ";
			s_data += "    {                                                   ";
			s_data += "      'dplyDt': '2022-09-07T10:53:32.712',              ";
			s_data += "      'dplyType': 'DPLY',                               ";
			s_data += "      'svcId': 'SYNCSERVICE',                           ";
			s_data += "      'svcNm': 'SYNCSERVICE',                           ";
			s_data += "      'userNm': 'syncUser',                             ";
			s_data += "      'pw': '',                                         ";
			s_data += "      'sla': {                                          ";
			s_data += "        'sec': null,                                    ";
			s_data += "        'min': 3,                                       ";
			s_data += "        'hr': null,                                     ";
			s_data += "        'day': null,                                    ";
			s_data += "        'mon': null                                     ";
			s_data += "      },                                                ";
			s_data += "      'svcStDt': '2022-09-01T00:00:00',                 ";
			s_data += "      'svcEndDt': '2099-12-31T23:59:59',                ";
			s_data += "      'apiAut': [                                       ";
			s_data += "        'SYNCSYSTEM.dataSyncTest'                       ";
			s_data += "      ],                                                ";
			s_data += "      'ipAcesAut': {                                    ";
			s_data += "        'alwdIp': [],                                   ";
			s_data += "        'blckIp': []                                    ";
			s_data += "      },                                                ";
			s_data += "      'atrib': {                                        ";
			s_data += "        'cpId': '',                                     ";
			s_data += "        'serviceId': ''                                 ";
			s_data += "      }                                                 ";
			s_data += "    },                                                  ";
			s_data += "  ]                                                     ";
			s_data += "}                                                       ";
		}
		else if ("svc".equals(apiTarget) && "getSvcDplyById".equals(apiName)) {
			s_data += "{                                                       ";
			s_data += "  'value': {                                            ";
			s_data += "    'dplyDt': '2022-08-26T15:24:02.473',                ";
			s_data += "    'dplyType': 'DPLY',                                 ";
			s_data += "    'svcId': 'HRSERVICE',                               ";
			s_data += "    'svcNm': 'HRSERVICENM',                             ";
			s_data += "    'userNm': '정혜림',                                 ";
			s_data += "    'sla': {                                            ";
			s_data += "      'sec': 3,                                         ";
			s_data += "      'min': 5,                                         ";
			s_data += "      'hr': null,                                       ";
			s_data += "      'day': null,                                      ";
			s_data += "      'mon': null                                       ";
			s_data += "    },                                                  ";
			s_data += "    'svcStDt': '2022-07-12T00:00:00',                   ";
			s_data += "    'svcEndDt': '2099-07-31T23:59:59',                  ";
			s_data += "    'apiAut': [                                         ";
			s_data += "      'HRSYSTEM.KHUB',                                  ";
			s_data += "      'SB.MESG_MTSMSOPE',                               ";
			s_data += "      'HRSYSTEM.MESG',                                  ";
			s_data += "      'SB.MessageMTMMSReportNoNetCharge',               ";
			s_data += "      'HRSYSTEM.Auth11',                                ";
			s_data += "      'DBC.Auth_DcbPost',                               ";
			s_data += "      'HRSYSTEM.SubscriptionInfoRetrievalManagerSSL',   ";
			s_data += "      'SHUB.getSpecificSubsAndUserInfo',                ";
			s_data += "      'HRSYSTEM.getEapAKA',                             ";
			s_data += "      'SB.getCarrierSpaceUpdates',                      ";
			s_data += "      'SB.checkCustomerIDByIpForECNV_RestPost',         ";
			s_data += "      'HRSYSTEM.getApiNmPre_v1_RestPost',               ";
			s_data += "      'HRSYSTEM.authHandlerTest'                        ";
			s_data += "    ],                                                  ";
			s_data += "    'ipAcesAut': {                                      ";
			s_data += "      'alwdIp': [],                                     ";
			s_data += "      'blckIp': []                                      ";
			s_data += "    },                                                  ";
			s_data += "    'atrib': {                                          ";
			s_data += "      'cpId': '',                                       ";
			s_data += "      'serviceId': ''                                   ";
			s_data += "    }                                                   ";
			s_data += "  }                                                     ";
			s_data += "}                                                       ";
		}
		else if ("data".equals(apiTarget) && "getApiLinkDataList".equals(apiName)) {
			s_data += "{                                         ";
			s_data += "  'value': [                              ";
			s_data += "    {                                     ";
			s_data += "      'type': 'PARAM',                    ";
			s_data += "      'key': 'paramSyncTest',             ";
			s_data += "      'value': '{    \"paramSyncKey1\": \"/param,/sync,/value/1\", \\n    \"paramSyncKey2\": \"/param,/sync,/value/2\" \\n}', ";
			s_data += "      'dplyDt': '2022-09-07T00:40:16'     ";
			s_data += "    },                                    ";
			s_data += "    {                                     ";
			s_data += "      'type': 'PARAM',                    ";
			s_data += "      'key': 'paramUpdateTest',           ";
			s_data += "      'value': '{\"updateParamKey\":{\"updateParamKey1\":\"create/param/value/1\",\"updateParamKey2\":\"create/param/value/2\"}}', ";
			s_data += "      'dplyDt': '2022-09-06T17:48:46.313' ";
			s_data += "    }                                     ";
			s_data += "  ]                                       ";
			s_data += "}                                         ";
		}
		else if ("data".equals(apiTarget) && "getApiLinkDataByType".equals(apiName)) {
			s_data += "{                                         ";
			s_data += "  'value': {                              ";
			s_data += "    'type': 'DOMAIN',                     ";
			s_data += "    'key': 'BCMGRID1901311043cT5haYCb',   ";
			s_data += "    'value': '{\"commonSmartContract\": \"bcapitest.ktbcp.kt.co.kr\"}', ";
			s_data += "    'dplyDt': '2022-09-07T16:00:00'       "; 
			s_data += "  }                                       ";
			s_data += "}                                         ";
		}
		
		return s_data;
	}
}
