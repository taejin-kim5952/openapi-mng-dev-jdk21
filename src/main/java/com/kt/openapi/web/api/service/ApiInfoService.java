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
package com.kt.openapi.web.api.service;

import com.kt.openapi.web.adptran.vo.AdptranTestcaseVO;
import com.kt.openapi.web.api.vo.ApiDefaultVO;
import com.kt.openapi.web.api.vo.ApiMainVo;
import com.kt.openapi.web.api.vo.ApiMenuVO;
import com.kt.openapi.web.api.vo.ApiSearchVO;

import java.util.List;
import java.util.Map;

/**
 * [마이그레이션] EgovMap 제거 및 VO 전환
 */
public interface ApiInfoService {

	/** api 기본정보 조회 - [마이그레이션] EgovMap -> VO 전환 */
	ApiDefaultVO selApiDefaultData(ApiMainVo vo) throws Exception;

	/** api lnb 조회 - [마이그레이션] EgovMap -> VO 전환 */
	List<ApiMenuVO> selApiMenuList(ApiMainVo vo) throws Exception;

	List<ApiSearchVO> selApiSearchList(ApiMainVo vo) throws Exception;

	int selApiSearchListCnt(ApiMainVo vo) throws Exception;

	/** testcase 정보조회 - [마이그레이션] EgovMap -> VO 전환 */
	List<AdptranTestcaseVO> select_API_TESTCASE(Map<String, Object> map_in) throws Exception;
}
