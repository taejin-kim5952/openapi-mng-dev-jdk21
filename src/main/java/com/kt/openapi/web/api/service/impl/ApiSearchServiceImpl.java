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

import com.kt.openapi.web.api.dao.ApiSearchDAO;
import com.kt.openapi.web.api.service.ApiSearchService;
import com.kt.openapi.web.api.vo.ApiCategoryVO;
import com.kt.openapi.web.api.vo.ApiMainSearchVo;
import com.kt.openapi.web.api.vo.ApiSearchVO;
import com.kt.openapi.web.api.vo.ApiSystemVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("apiSearchService")
public class ApiSearchServiceImpl implements ApiSearchService {

	private static final Logger log = LoggerFactory.getLogger(ApiSearchServiceImpl.class);

	@Autowired
	private ApiSearchDAO apiSearchDAO;

	@Override
	public List<ApiSearchVO> selMainList(ApiMainSearchVo vo) throws Exception {
		return apiSearchDAO.selMainList(vo);
	}

	@Override
	public List<ApiCategoryVO> selMainCateList(ApiMainSearchVo vo) throws Exception {
		return apiSearchDAO.selMainCateList(vo);
	}

	@Override
	public List<ApiSystemVO> selMainSysList(ApiMainSearchVo vo) throws Exception {
		return apiSearchDAO.selMainSysList(vo);
	}

	@Override
	public int selMainListTotalCnt(ApiMainSearchVo vo) throws Exception {
		return apiSearchDAO.selMainListTotalCnt(vo);
	}

	@Override
	public List<ApiSystemVO> selSystemList() throws Exception {
		return apiSearchDAO.selSystemList();
	}
}
