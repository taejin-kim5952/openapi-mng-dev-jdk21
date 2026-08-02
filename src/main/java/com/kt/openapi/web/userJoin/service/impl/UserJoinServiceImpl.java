package com.kt.openapi.web.userJoin.service.impl;

import com.kt.openapi.web.userJoin.dao.UserJoinDAO;
import com.kt.openapi.web.userJoin.service.UserJoinService;
import com.kt.openapi.web.userJoin.vo.UserHistVO;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.userJoin.service.impl
* 2. 타입명 : UserJoinServiceImpl.java
* 3. 작성일 : 2017. 11. 30. 오후 2:56:02
* 4. 작성자 : ANEUNTAEK
* 5. 설명 : 회원 가입
* </pre>
*/
@Service("userJoinService")
public class UserJoinServiceImpl implements UserJoinService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserJoinServiceImpl.class);

	@Autowired
	private UserJoinDAO userJoinDAO;
	
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
	@Override
	@Transactional(rollbackFor={Exception.class})
	public void insertUserJoin(UserJoinVO param) throws Exception {
		
		userJoinDAO.insertUserJoin(param);
	}

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
	@Override
	public UserJoinVO selectUserJoin(UserJoinVO param) throws Exception {
		return userJoinDAO.selectUserJoin(param) ;
	}

	/**
	 * <pre>
	 * 1. 메소드명 : insertMgtHist
	 * 2. 작성일 : 2017. 11. 30. 오후 2:53:50
	 * 3. 작성자 : ANEUNTAEK
	 * 4. 설명 : 회원 가입 이력 저장
	 * </pre>
	 * @param param
	 * @throws Exception
	 */
	@Override
	@Transactional(rollbackFor={Exception.class})
	public void insertMgtHist(UserHistVO param) throws Exception {
		// TODO Auto-generated method stub
		userJoinDAO.insertMgtHist(param);
	}

}
