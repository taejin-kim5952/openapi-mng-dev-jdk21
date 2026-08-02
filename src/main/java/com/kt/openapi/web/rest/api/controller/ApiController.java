package com.kt.openapi.web.rest.api.controller;

import com.kt.openapi.web.api.service.ApiMainService;
import com.kt.openapi.web.api.service.ApiRegService;
import com.kt.openapi.web.api.vo.ApiDefVO;
import com.kt.openapi.web.api.vo.ApiMainVo;
import com.kt.openapi.web.api.vo.ApiRegVO;
import com.kt.openapi.web.rest.api.vo.*;
import com.kt.openapi.web.rest.common.vo.StatHeaderVO;
import com.kt.openapi.web.rest.util.ErrorValidator;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;




/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.rest.api.controller
* 2. 타입명 : ApiController.java
* 3. 작성일 : 2017. 12. 5. 오후 7:08:35
* 4. 작성자 : JungHwan Hwang
* 5. 설명 : YAML 에디터에서 데이터 확인 하기 위한 REST
* </pre>
*/
@Controller
@RequestMapping(value="/api.json")
public class ApiController {
	
	private static final Logger LOG = LoggerFactory.getLogger(ApiController.class);
	
	@Value("${pageUnit:10}")
	private int pageUnit;

	@Value("${pageSize:10}")
	private int pageSize;
	
	@Autowired
	private ErrorValidator errorValidator;
	
	@Autowired
	private ApiRegService apiRegService;
	
	@Autowired
	private ApiMainService apiMainService;
	
	/**
	* <pre>
	* 1. 메소드명 : getApi
	* 2. 작성일 : 2017. 12. 5. 오후 8:35:41
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 :
	* </pre>
	* @param svo
	* @param result
	* @return
	* @throws Exception
	* http://localhost:8080/api.json?apiNo=1115&mbrId=0001EDL7paepLUowDTqveogIbg==
	*/
//	@RequestMapping(value="/getApi.json")
	@RequestMapping(method=RequestMethod.GET)
	public HashMap<String,Object> getApi( @Valid ApiGetSearchVO svo, BindingResult result) throws Exception {
		HashMap<String,Object> info = new HashMap<String,Object>();
		
		LOG.debug("#######################   getApi START ############################");
		LOG.debug("result   : {}", result);
		LOG.debug("SearchVO   : {}", svo);
		
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("code","200");
		resultMap.put("message","성공");
		
//		errorValidator.validate(svo, result);
		ApiGetStatRootVO root = new ApiGetStatRootVO();
		LOG.debug("result 결과 값   : {}", result.hasErrors());
		if(result.hasErrors()){
			root.setHeader(errorValidator.SetErrorData(result));
			info.put("code","400");
			info.put("message","요청 실패");
			return info;
	    }
		
//		StatDataVO vo = service.selAuth(svo);//권한 존재 여부 체크
		
		StatHeaderVO header 	= new StatHeaderVO();
		ApiGetStatBodyVO body 	= new ApiGetStatBodyVO();
//		ApiGetStatDataVO data   = new ApiGetStatDataVO();
		
		ApiRegVO apiRegVO = new ApiRegVO();
		apiRegVO.setApiSpcNo(svo.getApiNo());
		
		ApiDefVO apiDef = apiRegService.selApiInfo(apiRegVO);
		
		info.put("code","200");
		info.put("message","정상");
		
		if(apiDef!=null) {
			
			body.setApiNo(apiDef.getApiSpcNo());
			body.setApiSpcNo(apiDef.getApiSpcId());
			body.setVer(apiDef.getVer());
			body.setMbrId(svo.getMbrId());
			body.setApiName(apiDef.getApiNm());
			body.setYaml(apiDef.getYamlSbst());
			body.setSystemId(apiDef.getSysId());
			body.setSystemName(apiDef.getSysIdNm());
			body.setApiStatus(apiDef.getRegSttusCd());
			body.setApiDesc(apiDef.getApiDesc());
			body.setAutId(apiDef.getAutId());
			body.setVerDesc(apiDef.getVerDesc());
			body.setHost(apiDef.getHost());
			body.setBasPath(apiDef.getBasPath());
			body.setRfrnWsdlUrl(apiDef.getRfrnWsdlUrl());
			body.setRfrnTmpltNo(apiDef.getRfrnTmpltNo());
			body.setRfrnApiSpcNo(apiDef.getRfrnApiSpcNo());
			body.setYamlFilePath(apiDef.getYamlFilePath());
			body.setYamlFileNm(apiDef.getYamlFileNm());
			body.setRegSttusCd(apiDef.getRegSttusCd());
			body.setTmpltYn(apiDef.getTmpltYn());
			body.setRegr(apiDef.getRegr());
			body.setAmdDt(String.valueOf(apiDef.getAmdDt()));
			body.setAmdr(apiDef.getAmdr());
			body.setDelYn(apiDef.getDelYn());
			body.setApiClass(apiDef.getApiClass());
			body.setBstgwYn(apiDef.getBstgwYn());
			body.setApiVeriBaseurl(apiDef.getApiVeriBaseurl());
			body.setMinId(apiDef.getMinId());
			body.setMaxId(apiDef.getMaxId());
			body.setProjectNamespace(apiDef.getProjectNamespace());
			body.setProjectName(apiDef.getProjectName());
			//body.setData(data);
			
			header.setResultCode("0000");
			header.setResultMsg("성공");
			
			info.put("apiNo" , apiDef.getApiSpcNo());
			info.put("apiSpcNo" , apiDef.getApiSpcId());
			info.put("ver" , apiDef.getVer());
			info.put("mbrId" , svo.getMbrId());
			info.put("apiName" , apiDef.getApiNm());
			info.put("yaml" , apiDef.getYamlSbst());
			info.put("systemId" , apiDef.getSysId());
			info.put("systemName" , apiDef.getSysIdNm());
			info.put("apiStatus" , apiDef.getRegSttusCd());
			info.put("apiDesc" , apiDef.getApiDesc());
			info.put("autId" , apiDef.getAutId());
			info.put("verDesc" , apiDef.getVerDesc());
			info.put("host" , apiDef.getHost());
			info.put("basPath" , apiDef.getBasPath());
			info.put("rfrnWsdlUrl" , apiDef.getRfrnWsdlUrl());
			info.put("rfrnTmpltNo" , apiDef.getRfrnTmpltNo());
			info.put("rfrnApiSpcNo" , apiDef.getRfrnApiSpcNo());
			info.put("yamlFilePath" , apiDef.getYamlFilePath());
			info.put("yamlFileNm" , apiDef.getYamlFileNm());
			info.put("regSttusCd" , apiDef.getRegSttusCd());
			info.put("tmpltYn" , apiDef.getTmpltYn());
			info.put("regr" , apiDef.getRegr());
			info.put("amdDt" , apiDef.getAmdDt());
			info.put("amdr" , apiDef.getAmdr());
			info.put("delYn" , apiDef.getDelYn());
			info.put("apiClass" , apiDef.getApiClass());
			info.put("bstgwYn" , apiDef.getBstgwYn());
			info.put("apiVeriBaseurl" , apiDef.getApiVeriBaseurl());
			info.put("minId" , apiDef.getMinId());
			info.put("maxId" , apiDef.getMaxId());
			info.put("projectNamespace" , apiDef.getProjectNamespace());
			info.put("projectName" , apiDef.getProjectName());
			
		}else {
			header.setResultCode("0001");
			header.setResultMsg("데이터가 없습니다.");
			
			info.put("code","200");
			info.put("message","요청 실패");
		}
		
//		root.setHeader(header);
//		root.setBody(body);
		
//		StringWriter  data = JaxbApiUtil.marshallering(root);
//		LOG.debug("data : {}", data);
//		
//		
		return info;
	}
	
	
	/**
	* <pre>
	* 1. 메소드명 : putApi
	* 2. 작성일 : 2017. 12. 5. 오후 7:59:22
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : API 저장 (YAML)
	* </pre>
	* @param svo
	* @param result
	* @return
	* @throws Exception
	*/
	//@RequestMapping(value="/putApi.json" , method=RequestMethod.GET)
	@RequestMapping(method=RequestMethod.PUT)
	public   HashMap<String,Object>  putApi( @Valid ApiPutSearchVO svo, BindingResult result) throws Exception {
		HashMap<String,Object> info = new HashMap<String,Object>();
		LOG.debug("#######################   putApi START ############################");
		LOG.debug("result   : {}", result);
		LOG.debug("SearchVO   : {}", svo);
		
		errorValidator.validate(svo, result);
		
		ApiPutStatRootVO root = new ApiPutStatRootVO();
		LOG.debug("result 결과 값   : {}", result.hasErrors());
		if(result.hasErrors()){
			root.setHeader(errorValidator.SetErrorData(result));
			info.put("code","400");
			info.put("message","요청 실패");
			
			return info;
	    }
		
		StatHeaderVO header 	= new StatHeaderVO();
		ApiPutStatBodyVO body 	= new ApiPutStatBodyVO();
		ApiPutStatDataVO data   = new ApiPutStatDataVO();
		
		header.setResultCode("0000");
		header.setResultMsg("성공");
		
		ApiRegVO apiRegVO = new ApiRegVO();
		apiRegVO.setApiSpcNo(svo.getApiNo());
		apiRegVO.setYamlStr(svo.getYaml());
		apiRegVO.setYamlSbst(svo.getYaml());
//		apiRegVO.setYamlFileNm(yamlFileNm);
		
		apiRegService.savApiRegBasic(apiRegVO);
		
		apiRegService.savYamlToFile(apiRegVO);
		
		body.setData(data);
		
		root.setHeader(header);
		root.setBody(body);
		
		try {
			apiRegService.savApiRegBasic(apiRegVO);
			
			apiRegService.savYamlToFile(apiRegVO);
		}catch(Exception e) {
			info.put("code","500");
			info.put("message","내부 오류");
		}
		
//		StringWriter  data = JaxbApiUtil.marshallering(root);
//		LOG.debug("data : {}", data);
//		
//		
		return info;
	}
	
	/**
	* <pre>
	* 1. 메소드명 : putApi
	* 2. 작성일 : 2017. 12. 5. 오후 7:59:22
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : API 등록 요청 ( API 저장 포함 )
	* </pre>
	* @param svo
	* @param result
	* @return
	* @throws Exception
	*/
//	@RequestMapping(value="/postApi.json" , method=RequestMethod.GET)
	@RequestMapping(method=RequestMethod.POST)
	public   HashMap<String,Object>  postApi( @Valid ApiPutSearchVO svo, BindingResult result) throws Exception {
		
		HashMap<String,Object> info = new HashMap<String,Object>();
		
		LOG.debug("#######################   postApi START ############################");
		LOG.debug("result   : {}", result);
		LOG.debug("SearchVO   : {}", svo);
		
		errorValidator.validate(svo, result);
		
		ApiPutStatRootVO root = new ApiPutStatRootVO();
		LOG.debug("result 결과 값   : {}", result.hasErrors());
		if(result.hasErrors()){
			root.setHeader(errorValidator.SetErrorData(result));
			info.put("code","400");
			info.put("message","요청 실패");
			return info;
	    }
		
//		StatDataVO vo = service.selAuth(svo);//권한 존재 여부 체크
		
		StatHeaderVO header 	= new StatHeaderVO();
		ApiPutStatBodyVO body 	= new ApiPutStatBodyVO();
		ApiPutStatDataVO data   = new ApiPutStatDataVO();
		
		header.setResultCode("200");
		header.setResultMsg("성공");
		
		ApiRegVO apiRegVO = new ApiRegVO();
		apiRegVO.setApiSpcNo(svo.getApiNo());
		
		ApiDefVO apiDef = apiRegService.selApiInfo(apiRegVO);
		
		data.setApiNo(apiDef.getApiSpcNo());
		data.setApiSpcNo(apiDef.getApiSpcId());
		data.setVer(apiDef.getVer());
		data.setMbrId(svo.getMbrId());
		data.setApiName(apiDef.getApiNm());
		data.setYaml(apiDef.getYamlSbst());
		data.setSystemId(apiDef.getSysId());
		data.setSystemName(apiDef.getSysIdNm());
		data.setApiStatus(apiDef.getRegSttusCd());
		data.setApiDesc(apiDef.getApiDesc());
		data.setAutId(apiDef.getAutId());
		data.setVerDesc(apiDef.getVerDesc());
		data.setHost(apiDef.getHost());
		data.setBasPath(apiDef.getBasPath());
		data.setRfrnWsdlUrl(apiDef.getRfrnWsdlUrl());
		data.setRfrnTmpltNo(apiDef.getRfrnTmpltNo());
		data.setRfrnApiSpcNo(apiDef.getRfrnApiSpcNo());
		data.setYamlFilePath(apiDef.getYamlFilePath());
		data.setYamlFileNm(apiDef.getYamlFileNm());
		data.setRegSttusCd(apiDef.getRegSttusCd());
		data.setTmpltYn(apiDef.getTmpltYn());
		data.setRegr(apiDef.getRegr());
		data.setAmdDt(String.valueOf(apiDef.getAmdDt()));
		data.setAmdr(apiDef.getAmdr());
		data.setDelYn(apiDef.getDelYn());
		data.setApiClass(apiDef.getApiClass());
		data.setBstgwYn(apiDef.getBstgwYn());
		data.setApiVeriBaseurl(apiDef.getApiVeriBaseurl());
		data.setMinId(apiDef.getMinId());
		data.setMaxId(apiDef.getMaxId());
		data.setProjectNamespace(apiDef.getProjectNamespace());
		data.setProjectName(apiDef.getProjectName());
		
		body.setData(data);
		
		root.setHeader(header);
		root.setBody(body);
		
		info.put("code","0000");
		info.put("message","성공");
		info.put("apiNo" , apiDef.getApiSpcNo());
		info.put("apiSpcNo" , apiDef.getApiSpcId());
		info.put("ver" , apiDef.getVer());
		info.put("mbrId" , svo.getMbrId());
		info.put("apiName" , apiDef.getApiNm());
		info.put("yaml" , apiDef.getYamlSbst());
		info.put("systemId" , apiDef.getSysId());
		info.put("systemName" , apiDef.getSysIdNm());
		info.put("apiStatus" , apiDef.getRegSttusCd());
		info.put("apiDesc" , apiDef.getApiDesc());
		info.put("autId" , apiDef.getAutId());
		info.put("verDesc" , apiDef.getVerDesc());
		info.put("host" , apiDef.getHost());
		info.put("basPath" , apiDef.getBasPath());
		info.put("rfrnWsdlUrl" , apiDef.getRfrnWsdlUrl());
		info.put("rfrnTmpltNo" , apiDef.getRfrnTmpltNo());
		info.put("rfrnApiSpcNo" , apiDef.getRfrnApiSpcNo());
		info.put("yamlFilePath" , apiDef.getYamlFilePath());
		info.put("yamlFileNm" , apiDef.getYamlFileNm());
		info.put("regSttusCd" , apiDef.getRegSttusCd());
		info.put("tmpltYn" , apiDef.getTmpltYn());
		info.put("regr" , apiDef.getRegr());
		info.put("amdDt" , apiDef.getAmdDt());
		info.put("amdr" , apiDef.getAmdr());
		info.put("delYn" , apiDef.getDelYn());
		info.put("apiClass" , apiDef.getApiClass());
		info.put("bstgwYn" , apiDef.getBstgwYn());
		info.put("apiVeriBaseurl" , apiDef.getApiVeriBaseurl());
		info.put("minId" , apiDef.getMinId());
		info.put("maxId" , apiDef.getMaxId());
		info.put("projectNamespace" , apiDef.getProjectNamespace());
		info.put("projectName" , apiDef.getProjectName());
		
		try {
			apiRegService.savApiRegBasic(apiRegVO);
			
			apiRegService.savYamlToFile(apiRegVO);
		}catch(Exception e) {
			info.put("code","500");
			info.put("message","내부 오류");
		}
//		StringWriter  data = JaxbApiUtil.marshallering(root);
//		LOG.debug("data : {}", data);
//		
		info.put("code","0000");
		info.put("message","성공");
		
//		
		return info;
		
	}
	
	
	/**
	* <pre>
	* 1. 메소드명 : cancelApi
	* 2. 작성일 : 2017. 12. 5. 오후 8:20:06
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : API 삭제
	* </pre>
	* @param svo
	* @param result
	* @return
	* @throws Exception
	* http://localhost:8080/rest/api/cancelApi.json?api_no=45&mbr_id=1
	*/
//	@RequestMapping(value="/cancelApi.json" , method=RequestMethod.GET)
	@RequestMapping(method=RequestMethod.DELETE)
	public   ApiGetStatRootVO  cancelApi( @Valid ApiPutSearchVO svo, BindingResult result) throws Exception {
		LOG.debug("#######################   cancelApi START ############################");
		LOG.debug("result   : {}", result);
		LOG.debug("SearchVO   : {}", svo);
		
		errorValidator.validate(svo, result);
		
		ApiGetStatRootVO root = new ApiGetStatRootVO();
		LOG.debug("result 결과 값   : {}", result.hasErrors());
		if(result.hasErrors()){
			root.setHeader(errorValidator.SetErrorData(result));
			return root;
	    }
		
		StatHeaderVO header 	= new StatHeaderVO();
		ApiGetStatBodyVO body 	= new ApiGetStatBodyVO();
		ApiGetStatDataVO data   = new ApiGetStatDataVO();
		
		ApiMainVo apiVO = new ApiMainVo();
		apiVO.setApiSpcNo(svo.getApiNo());
		apiVO.setRegr(svo.getMbrId());
		
		try {
			int resultInt = (int) apiMainService.delDevApi(apiVO);
			
			if(resultInt < 1) {
				header.setResultCode("0001");
				header.setResultMsg("삭제될 데이터가 없습니다.");
			}else {
				header.setResultCode("0000");
				header.setResultMsg("성공");
			}
		}catch(Exception e) {
			header.setResultCode("0005");
			header.setResultMsg("실패");
		}
		
		root.setHeader(header);
		
//		StringWriter  data = JaxbApiUtil.marshallering(root);
//		LOG.debug("data : {}", data);
//		
//		
		return root;
	}
	
}
