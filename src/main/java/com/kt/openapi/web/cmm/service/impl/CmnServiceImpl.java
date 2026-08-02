package com.kt.openapi.web.cmm.service.impl;

import com.kt.openapi.web.cmm.dao.CmnDAO;
import com.kt.openapi.web.cmm.service.CmnService;
import com.kt.openapi.web.cmm.vo.CmnCdVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.cmm.service.impl
* 2. 타입명 : CmnServiceImpl.java
* 3. 작성일 : 2017. 11. 28. 오후 5:20:19
* 4. 작성자 : JungHwan Hwang
* 5. 설명 : 공통코드 관련
* </pre>
*/
@Service("CmnService")
public class CmnServiceImpl implements CmnService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CmnServiceImpl.class);

	// TODO ibatis 사용
	@Autowired
	private CmnDAO cmnDAO;
	
	/* (non-Javadoc)
	 * @see com.kt.openapi.web.api.service.ApiRegService#selImportApiList(com.kt.openapi.web.api.vo.ApiMainVo)
	 */
	public ArrayList<CmnCdVO> selComnList(String groupCd) throws Exception{
		return (ArrayList<CmnCdVO>)cmnDAO.selComnList(groupCd);
	}
	
}
