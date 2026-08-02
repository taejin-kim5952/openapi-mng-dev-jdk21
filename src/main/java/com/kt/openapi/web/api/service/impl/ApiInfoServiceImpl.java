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
package com.kt.openapi.web.api.service.impl;

import com.kt.openapi.web.adptran.vo.AdptranTestcaseVO;
import com.kt.openapi.web.api.dao.ApiInfoDAO;
import com.kt.openapi.web.api.service.ApiInfoService;
import com.kt.openapi.web.api.vo.ApiDefaultVO;
import com.kt.openapi.web.api.vo.ApiMainVo;
import com.kt.openapi.web.api.vo.ApiMenuVO;
import com.kt.openapi.web.api.vo.ApiSearchVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * [마이그레이션] EgovMap 제거 및 VO 전환
 */
@Service("apiInfoService")
public class ApiInfoServiceImpl implements ApiInfoService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ApiInfoServiceImpl.class);
	
	@Autowired
	private ApiInfoDAO apiInfoDAO;

	@Override
	public ApiDefaultVO selApiDefaultData(ApiMainVo vo) throws Exception {
		return apiInfoDAO.selApiDefaultData(vo);
	}

	@Override
	public List<ApiMenuVO> selApiMenuList(ApiMainVo vo) throws Exception {
		return apiInfoDAO.selApiMenuList(vo);
	}
	
	@Override
	public List<ApiSearchVO> selApiSearchList(ApiMainVo vo) throws Exception {
		return apiInfoDAO.selApiSearchList(vo);
	}

	@Override
	public int selApiSearchListCnt(ApiMainVo vo) throws Exception {
		return apiInfoDAO.selApiSearchCnt(vo);
	}
	
	@Override
	public List<AdptranTestcaseVO> select_API_TESTCASE(Map<String, Object> map_in) throws Exception {
		return apiInfoDAO.select_API_TESTCASE(map_in);
	}
}
