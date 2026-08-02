package com.kt.openapi.web.rest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.kt.openapi.web.rest.api.vo.ApiGetSearchVO;
import com.kt.openapi.web.rest.api.vo.ApiPutSearchVO;
import com.kt.openapi.web.rest.api.vo.ApiPutStatRootVO;
import com.kt.openapi.web.rest.auth.vo.SearchVO;
import com.kt.openapi.web.rest.common.vo.StatHeaderVO;

@Service("ErrorValidator")
public class ErrorValidator implements Validator {
	
	private static final Logger LOG = LoggerFactory.getLogger(ErrorValidator.class);
	
	/**
	 * Sets error data.
	 *
	 * @param bindingResult the binding result
	 * @return the object
	 */
	public StatHeaderVO SetErrorData(BindingResult bindingResult)  throws Exception{
		
		StatHeaderVO header = new StatHeaderVO();
		//error count를 가져온다.
		int resultCount = bindingResult.getErrorCount();
		LOG.debug("resultCount  : {}" , resultCount);
		//error Field명과 Message를 맵에 담는다.
		for (int i=0; i<resultCount; i++) {
			String errorCode = bindingResult.getFieldErrors().get(i).getCode();
			String message = bindingResult.getFieldErrors().get(i).getDefaultMessage();
			header.setResultCode(errorCode);
			header.setResultMsg(message);
			//-- [2023:codeeyes][반복문에서 break, continue에 대한 불명확한 사용 금지 issue]
			if (null != errorCode) {
				break;
			}
		}
		return header;
	}

	@Override
	public boolean supports(Class<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void validate(Object object, Errors errors) {
		System.out.println("Start validate @@@@@@@@@@@@@@@@@@@@@@@@@@@@  ::");
		LOG.debug(" errors  : {}" ,  errors);
		LOG.debug(" errors.getObjectName()  : {}" ,  errors.getObjectName());
		LOG.debug(" object : {}" ,  object);
		if (-1 !=errors.getObjectName().indexOf("searchVO")) {
			SearchVO vo = (SearchVO) object;
			if (null == vo.getApiNo() || vo.getApiNo().trim().isEmpty()) {
				errors.rejectValue("apiNo", ResultCode.RESPONSE_DATA_ERR_CODE, ResultCode.RESPONSE_DATA_ERR_Msg);
			}else if (null == vo.getMbrId() || vo.getMbrId().trim().isEmpty()) {
				errors.rejectValue("mbrId", ResultCode.RESPONSE_DATA_ERR_CODE, ResultCode.RESPONSE_DATA_ERR_Msg);
			}
		} else if (-1 !=errors.getObjectName().indexOf("apiGetSearchVO")) {
			ApiGetSearchVO vo = (ApiGetSearchVO) object;
			if (null == vo.getApiNo() || vo.getApiNo().trim().isEmpty()) {
				errors.rejectValue("apiNo", ResultCode.RESPONSE_DATA_ERR_CODE, ResultCode.RESPONSE_DATA_ERR_Msg);
			}else if (null == vo.getMbrId() || vo.getMbrId().trim().isEmpty()) {
				errors.rejectValue("mbrId", ResultCode.RESPONSE_DATA_ERR_CODE, ResultCode.RESPONSE_DATA_ERR_Msg);
			}
		} else if (-1 !=errors.getObjectName().indexOf("apiPutStatRootVO")) {
			ApiPutSearchVO vo = (ApiPutSearchVO) object;
			if (null == vo.getApiNo() || vo.getApiNo().trim().isEmpty()) {
				errors.rejectValue("apiNo", ResultCode.RESPONSE_DATA_ERR_CODE, ResultCode.RESPONSE_DATA_ERR_Msg);
			}else if (null == vo.getMbrId() || vo.getMbrId().trim().isEmpty()) {
				errors.rejectValue("mbrId", ResultCode.RESPONSE_DATA_ERR_CODE, ResultCode.RESPONSE_DATA_ERR_Msg);
			}else if (null == vo.getYaml() || vo.getYaml().trim().isEmpty()) {
				errors.rejectValue("yaml", ResultCode.RESPONSE_DATA_ERR_CODE, ResultCode.RESPONSE_DATA_ERR_Msg);
			}
		}
		
		
		
	}
	
}
