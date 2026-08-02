package com.kt.openapi.web.rest.api.controller;

import com.kt.openapi.web.api.service.ApiMainService;
import com.kt.openapi.web.api.service.ApiRegService;
import com.kt.openapi.web.api.vo.ApiRegVO;
import com.kt.openapi.web.rest.api.vo.ApiPutSearchVO;
import com.kt.openapi.web.rest.api.vo.ApiPutStatRootVO;
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



@Controller
@RequestMapping(value="/apipost.json")
public class ApiPostController {
	
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
	@RequestMapping(method=RequestMethod.GET)
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
		
		info.put("code","200");
		info.put("message","성공");
		
		try {
			
			ApiRegVO apiRegVO = new ApiRegVO();
			
			apiRegVO.setApiSpcNo(svo.getApiNo());
			apiRegVO.setRegr(svo.getMbrId());
			apiRegVO.setYamlStr(svo.getYaml());
			apiRegVO.setYamlSbst(svo.getYaml());
			apiRegVO.setRegSttusCd("APIREG1020");
			
//			EgovMap map = (EgovMap)apiRegService.selApiInfo(apiRegVO);
//			apiRegVO.setYamlStr((map.get("yamlSbst")).toString());
//			apiRegVO.setYamlSbst((map.get("yamlSbst")).toString());
			
			apiRegService.savApiRegRest(apiRegVO);
			
		}catch(Exception e) {
			info.put("code","500");
			info.put("message","내부 오류");
		}

		return info;
		
	}
	
	
}
