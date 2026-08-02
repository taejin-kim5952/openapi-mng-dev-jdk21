package com.kt.openapi.web.rest.auth.service;

import com.kt.openapi.web.rest.auth.vo.SearchVO;

/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.qna.service
* 2. 타입명 : QnAService.java
* 3. 작성일 : 2017. 11. 30. 오후 2:14:58
* 4. 작성자 : user
* 5. 설명 : QNA SERVICE INTERFACE
* </pre>
*/
public interface AuthService {


	/**
	* <pre>
	* 1. 메소드명 : selAuth
	* 2. 작성일 : 2017. 12. 6. 오후 2:35:47
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 권한 조회
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	int  selAuth(SearchVO param)  throws Exception;

}
