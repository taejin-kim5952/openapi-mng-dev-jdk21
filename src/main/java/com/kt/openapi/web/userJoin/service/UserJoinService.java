package com.kt.openapi.web.userJoin.service;

import com.kt.openapi.web.userJoin.vo.UserHistVO;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;


/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.userJoin.service
* 2. 타입명 : UserJoinService.java
* 3. 작성일 : 2017. 11. 30. 오후 2:54:26
* 4. 작성자 : ANEUNTAEK
* 5. 설명 : 회원 가입
* </pre>
*/
public interface UserJoinService {

	/**
	* <pre>
	* 1. 메소드명 : insertUserJoin
	* 2. 작성일 : 2017. 11. 30. 오후 2:53:10
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 회원 가입 등록
	* </pre>
	* @param param
	* @throws Exception
	*/
	void insertUserJoin(UserJoinVO param)throws Exception;
	
	/**
	* <pre>
	* 1. 메소드명 : selectUserJoin
	* 2. 작성일 : 2017. 11. 30. 오후 2:53:31
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 회원 가입 완료 정보
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	UserJoinVO selectUserJoin(UserJoinVO param) throws Exception;
	

	/**
	* <pre>
	* 1. 메소드명 : insertMgtHist
	* 2. 작성일 : 2017. 11. 30. 오후 2:55:36
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 회원 가입 이력 저장
	* </pre>
	* @param param
	* @throws Exception
	*/
	void insertMgtHist(UserHistVO param)throws Exception;
	
}
