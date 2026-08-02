package com.kt.openapi.web.login.service;

import com.kt.openapi.web.auth.vo.AuthVO;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;

import java.util.List;

/**
 * 
* <pre>
* 1. 패키지명 : com.kt.openapi.web.login.service
* 2. 타입명 : LoginService.java
* 3. 작성일 : 2017. 11. 30. 오후 2:06:40
* 4. 작성자 : ANEUNTAEK
* 5. 설명 : LOGIN
* </pre>
 */
public interface LoginService {
	
	/**
	 * 
	* <pre>
	* 1. 메소드명 : getUserIdChk
	* 2. 작성일 : 2017. 11. 30. 오후 2:07:27
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : PSSO ID와 OPENAPI ID 일치 여부 확인
	* </pre>
	* @param param
	* @return
	* @throws Exception
	 */
	List<UserJoinVO> getUserIdChk(UserJoinVO param) throws Exception;
	
	
	/**
	 * 
	* <pre>
	* 1. 메소드명 : updateLDate
	* 2. 작성일 : 2017. 11. 30. 오후 2:07:32
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 최종 로그인 일시 수정
	* </pre>
	* @param param
	* @throws Exception
	 */
	void updateLDate (UserJoinVO param) throws Exception;

	/**
	 * 
	* <pre>
	* 1. 메소드명 : selUserInfo
	* 2. 작성일 : 2017. 11. 30. 오후 2:07:35
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 회원 가입 완료 정보
	* </pre>
	* @param userJoinVo
	* @return
	* @throws Exception
	 */
	UserJoinVO selUserInfo(UserJoinVO userJoinVo) throws Exception;
	
    /**
     * 
    * <pre>
    * 1. 메소드명 : selAuthList
    * 2. 작성일 : 2017. 11. 30. 오후 2:07:41
    * 3. 작성자 : ANEUNTAEK
    * 4. 설명 : 권한 정보
    * </pre>
    * @param authVo
    * @return
    * @throws Exception
     */
	List<AuthVO> selAuthList(AuthVO authVo) throws Exception;
	
}
