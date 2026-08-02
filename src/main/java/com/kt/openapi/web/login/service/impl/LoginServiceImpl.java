package com.kt.openapi.web.login.service.impl;

import com.kt.openapi.web.auth.vo.AuthVO;
import com.kt.openapi.web.login.dao.LoginDAO;
import com.kt.openapi.web.login.service.LoginService;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 
* <pre>
* 1. 패키지명 : com.kt.openapi.web.login.service.impl
* 2. 타입명 : LoginServiceImpl.java
* 3. 작성일 : 2017. 11. 30. 오후 2:08:53
* 4. 작성자 : ANEUNTAEK
* 5. 설명 : LOGIN
* </pre>
 */
@Service("loginService")
public class LoginServiceImpl implements LoginService{
	
	private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);
	
	@Autowired
	private LoginDAO loginDAO;


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
	@Override
	public List<UserJoinVO> getUserIdChk(UserJoinVO param) throws Exception {
		return loginDAO.getUserIdChk(param);
	}
	
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
	@Override
	@Transactional(rollbackFor={Exception.class})
	public void updateLDate(UserJoinVO param) throws Exception {
		loginDAO.updateLDate(param);
	}

	/**
	 * 
	* <pre>
	* 1. 메소드명 : selUserInfoList
	* 2. 작성일 : 2017. 11. 30. 오후 2:07:35
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 회원 가입 완료 정보
	* </pre>
	* @param userJoinVo
	* @return
	* @throws Exception
	 */
	@Override
	public UserJoinVO selUserInfo(UserJoinVO userJoinVo) throws Exception {
		return loginDAO.selUserInfo(userJoinVo);
	}

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
	@Override
	public List<AuthVO> selAuthList(AuthVO authVo) throws Exception {
		return loginDAO.selAuthList(authVo);
	}
	
}
