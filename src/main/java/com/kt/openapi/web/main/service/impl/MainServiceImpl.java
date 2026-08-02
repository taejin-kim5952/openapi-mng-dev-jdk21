package com.kt.openapi.web.main.service.impl;

import com.kt.openapi.web.main.dao.MainDAO;
import com.kt.openapi.web.main.service.MainService;
import com.kt.openapi.web.main.vo.MainBBSVO;
import com.kt.openapi.web.main.vo.MainVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.main.service.impl
* 2. 타입명 : MainServiceImpl.java
* 3. 작성일 : 2017. 12. 1. 오후 6:00:40
* 4. 작성자 : deveAdmin
* 5. 설명 :
* </pre>
*/
@Service("mainService")
public class MainServiceImpl implements MainService{

	private static final Logger log = LoggerFactory.getLogger(MainServiceImpl.class);
	
	@Autowired
	private MainDAO mainDAO;
	
	/**
	 * 
	* <pre>
	* 1. 메소드명 : selRecNotice
	* 2. 작성일 : 2017. 12. 1. 오후 5:54:39
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 공지사항 최신글 3개
	* </pre>
	* @param param
	* @return
	* @throws Exception
	 */
	@Override
	public List<MainBBSVO> selRecNotice(MainVO param) throws Exception {
		return mainDAO.selRecNotice(param);
	}

	/**
	 * 
	* <pre>
	* 1. 메소드명 : selRecDevF
	* 2. 작성일 : 2017. 12. 1. 오후 5:54:44
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 개발자 포럼 최신글 3개
	* </pre>
	* @param param
	* @return
	* @throws Exception
	 */
	@Override
	public List<MainBBSVO> selRecDevF(MainVO param) throws Exception {
		return mainDAO.selRecDevF(param);
	}

}
