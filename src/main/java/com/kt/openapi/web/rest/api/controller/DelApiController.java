package com.kt.openapi.web.rest.api.controller;

import com.kt.openapi.web.api.service.ApiMainService;
import com.kt.openapi.web.api.service.ApiRegService;
import com.kt.openapi.web.api.vo.ApiRegVO;
import com.kt.openapi.web.rest.api.vo.ApiGetStatBodyVO;
import com.kt.openapi.web.rest.api.vo.ApiGetStatDataVO;
import com.kt.openapi.web.rest.api.vo.ApiGetStatRootVO;
import com.kt.openapi.web.rest.api.vo.ApiPutSearchVO;
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
@RequestMapping(value="/delapi.json")
public class DelApiController {
	
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
	@RequestMapping(method=RequestMethod.GET)
	public   HashMap<String,Object>  cancelApi( @Valid ApiPutSearchVO svo, BindingResult result) throws Exception {
		LOG.debug("#######################   cancelApi START ############################");
		
		HashMap<String,Object> info = new HashMap<String,Object>();
		
		LOG.debug("result   : {}", result);
		LOG.debug("SearchVO   : {}", svo);
		
		errorValidator.validate(svo, result);
		
		ApiGetStatRootVO root = new ApiGetStatRootVO();
		LOG.debug("result 결과 값   : {}", result.hasErrors());
		if(result.hasErrors()){
			root.setHeader(errorValidator.SetErrorData(result));
			return info;
	    }
		
		StatHeaderVO header 	= new StatHeaderVO();
		ApiGetStatBodyVO body 	= new ApiGetStatBodyVO();
		ApiGetStatDataVO data   = new ApiGetStatDataVO();
		
		ApiRegVO apiVO = new ApiRegVO();
		apiVO.setApiSpcNo(svo.getApiNo());
		apiVO.setRegr(svo.getMbrId());
		
		try {
			int resultInt = (int) apiRegService.updApiRegRestBasicToWork(apiVO);
			
			if(resultInt < 1) {
				info.put("code","400");
				//info.put("message","삭제될 데이터가 없습니다.");
				
			}else {
				info.put("code","200");
				//info.put("message","삭제될 데이터가 없습니다.");
			}
		}catch(Exception e) {
			info.put("code","0005");
			info.put("message","실패");
		}
		
		root.setHeader(header);
		
//		StringWriter  data = JaxbApiUtil.marshallering(root);
//		LOG.debug("data : {}", data);
//		
//		
		return info;
	}
	
}
