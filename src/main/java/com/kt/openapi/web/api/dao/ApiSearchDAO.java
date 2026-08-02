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
package com.kt.openapi.web.api.dao;

import com.kt.openapi.web.api.vo.ApiCategoryVO;
import com.kt.openapi.web.api.vo.ApiMainSearchVo;
import com.kt.openapi.web.api.vo.ApiSearchVO;
import com.kt.openapi.web.api.vo.ApiSystemVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.api.dao
* 2. 타입명   : ApiSearchDAO.java
* 3. 작성일   : 2017. 12. 8. 오후 1:27:41
* 4. 작성자   : JungHwan Hwang
* 5. 설명     : API 검색 관련 MyBatis Mapper 인터페이스
* </pre>
*/
@Mapper
public interface ApiSearchDAO {

    /** api 목록 조회 */
    List<ApiSearchVO> selMainList(ApiMainSearchVo vo) throws Exception;

    /** 카테고리 목록 */
    List<ApiCategoryVO> selMainCateList(ApiMainSearchVo vo) throws Exception;

    /** 메인 시스템 목록 */
    List<ApiSystemVO> selMainSysList(ApiMainSearchVo vo) throws Exception;

    /** api 목록 조회 갯수 */
    int selMainListTotalCnt(ApiMainSearchVo vo) throws Exception;

    /** 시스템 목록 조회 */
    List<ApiSystemVO> selSystemList() throws Exception;
}
