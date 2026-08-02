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
package com.kt.openapi.web.guide.dao;

import com.kt.openapi.web.guide.vo.GuideShubVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.guide.dao
* 2. 타입명   : GuideDAO.java
* 3. 작성일   : 2017. 12. 12. 오후 5:12:31
* 4. 작성자   : Jeon Geun Kang
* 5. 설명     : Guide 관련 MyBatis Mapper 인터페이스
* </pre>
*/
@Mapper
public interface GuideDAO {

    /**
    * <pre>
    * 1. 메소드명 : selGuideShubList
    * 2. 작성일   : 2017. 12. 12. 오후 5:12:31
    * 3. 작성자   : Jeon Geun Kang
    * 4. 설명     : shub 가이드 페이지에서 shub list 조회
    * </pre>
    * @return
    * @throws Exception
    */
    List<GuideShubVO> selGuideShubList() throws Exception;
    
}
