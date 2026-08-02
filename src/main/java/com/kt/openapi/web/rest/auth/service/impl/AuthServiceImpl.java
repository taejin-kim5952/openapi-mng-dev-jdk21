package com.kt.openapi.web.rest.auth.service.impl;

import com.kt.openapi.web.rest.auth.dao.AuthDAO;
import com.kt.openapi.web.rest.auth.service.AuthService;
import com.kt.openapi.web.rest.auth.vo.SearchVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.qna.service.impl
* 2. 타입명 : QnAServiceImpl.java
* 3. 작성일 : 2017. 11. 30. 오후 2:14:29
* 4. 작성자 : user
* 5. 설명 : QNA SERVERIMPL INFO
* </pre>
*/
@Service("authService")
public class AuthServiceImpl implements AuthService{

	private static final Logger LOG = LoggerFactory.getLogger(AuthServiceImpl.class);
	
	@Autowired
	private AuthDAO authDAO;
	
	/* (non-Javadoc)
	 * @see com.kt.openapi.web.qna.service.QnAService#selQnaList(com.kt.openapi.web.qna.vo.QnASearchVO)
	 */	
	@Override
	public int selAuth(SearchVO param) throws Exception {
		return authDAO.selAuth(param);
	}
}
