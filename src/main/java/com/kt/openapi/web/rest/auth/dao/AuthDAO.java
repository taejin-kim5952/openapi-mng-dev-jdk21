package com.kt.openapi.web.rest.auth.dao;

import com.kt.openapi.web.rest.auth.vo.SearchVO;
import org.apache.ibatis.annotations.Mapper;

/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.rest.auth.dao
* 2. 타입명   : AuthDAO.java
* 3. 작성일   : 2017. 12. 6. 오후 2:36:55
* 4. 작성자   : JungHwan Hwang
* 5. 설명     : 권한 관련 MyBatis Mapper 인터페이스
* </pre>
*/
@Mapper
public interface AuthDAO {

    /**
    * <pre>
    * 1. 메소드명 : selAuth
    * 2. 작성일   : 2017. 12. 6. 오후 2:36:55
    * 3. 작성자   : JungHwan Hwang
    * 4. 설명     : 권한 목록 조회
    * </pre>
    * @param vo
    * @return
    * @throws Exception
    */
    int selAuth(SearchVO vo) throws Exception;
}
