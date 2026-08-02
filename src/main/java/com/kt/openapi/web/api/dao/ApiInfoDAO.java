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

import com.kt.openapi.web.adptran.vo.AdptranTestcaseVO;
import com.kt.openapi.web.api.vo.ApiDefaultVO;
import com.kt.openapi.web.api.vo.ApiMainVo;
import com.kt.openapi.web.api.vo.ApiMenuVO;
import com.kt.openapi.web.api.vo.ApiSearchVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.api.dao
* 2. 타입명   : ApiInfoDAO.java
* 3. 작성일   : 2017. 12. 19. 오후 4:31:34
* 4. 작성자   : user
* 5. 설명     : API 규격서 관련 MyBatis Mapper 인터페이스
* </pre>
*/
@Mapper
public interface ApiInfoDAO {

    /**
    * <pre>
    * 1. 메소드명 : selApiDefaultData
    * 2. 작성일   : 2017. 12. 19. 오후 5:09:17
    * 3. 작성자   : user
    * 4. 설명     : api 기본정보 조회 - [마이그레이션] EgovMap -> VO 전환
    * </pre>
    * @param vo
    * @return
    * @throws Exception
    */
    ApiDefaultVO selApiDefaultData(ApiMainVo vo) throws Exception;

    /**
    * <pre>
    * 1. 메소드명 : selApiMenuList
    * 2. 작성일   : 2017. 12. 19. 오후 6:44:40
    * 3. 작성자   : user
    * 4. 설명     : api lnb목록 조회 - [마이그레이션] EgovMap -> VO 전환
    * </pre>
    * @param vo
    * @return
    * @throws Exception
    */
    List<ApiMenuVO> selApiMenuList(ApiMainVo vo) throws Exception;
    
    /** api 검색 리스트 조회 - [마이그레이션] EgovMap -> VO 전환 */
    List<ApiSearchVO> selApiSearchList(ApiMainVo vo) throws Exception;
    
    /** api 검색 리스트 개수 조회 */
    int selApiSearchCnt(ApiMainVo vo) throws Exception;

    /** testcase 정보조회 - [마이그레이션] EgovMap -> VO 전환 */
    List<AdptranTestcaseVO> select_API_TESTCASE(Map<String, Object> params) throws Exception;
    
    /** api 정보 조회 (ApiInfoServiceImpl에서 사용) */
    ApiMainVo selApiInfo(ApiMainVo vo) throws Exception;
}
