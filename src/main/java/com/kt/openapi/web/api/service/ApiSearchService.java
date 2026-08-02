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

import java.util.List;

import com.kt.openapi.web.api.vo.ApiCategoryVO;
import com.kt.openapi.web.api.vo.ApiMainSearchVo;
import com.kt.openapi.web.api.vo.ApiSearchVO;
import com.kt.openapi.web.api.vo.ApiSystemVO;

public interface ApiSearchService {

	
	/**
	* <pre>
	* 1. 메소드명 : selMainList
	* 2. 작성일 : 2017. 12. 8. 오후 1:23:04
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : OPEN API 목록 조회
	* </pre>
	* @param vo
	* @return
	* @throws Exception
	*/
	List<ApiSearchVO> selMainList(ApiMainSearchVo vo) throws Exception;
	
	
	/**
	* <pre>
	* 1. 메소드명 : selMainSysList
	* 2. 작성일 : 2017. 12. 8. 오후 2:14:13
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 시스템 탭 목록
	* </pre>
	* @param vo
	* @return
	* @throws Exception
	*/
	List<ApiSystemVO> selMainSysList(ApiMainSearchVo vo) throws Exception;
	
	
	/**
	* <pre>
	* 1. 메소드명 : selMainCateList
	* 2. 작성일 : 2017. 12. 8. 오후 1:56:45
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 :
	* </pre>
	* @param vo
	* @return
	* @throws Exception
	*/
	List<ApiCategoryVO> selMainCateList(ApiMainSearchVo vo) throws Exception;
	
	
	/**
	* <pre>
	* 1. 메소드명 : selMainListTotalCnt
	* 2. 작성일 : 2017. 12. 8. 오후 1:23:06
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : OPEN API 목록 전체 조회
	* </pre>
	* @param vo
	* @return
	* @throws Exception
	*/
	int selMainListTotalCnt(ApiMainSearchVo vo) throws Exception;
	
	/**
	 * 시스템 목록 조회
	 */

	List<ApiSystemVO> selSystemList() throws Exception;
	

}
