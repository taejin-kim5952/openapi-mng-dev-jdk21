package com.kt.openapi.web.faq.service.impl;

import com.kt.openapi.web.faq.dao.FaqDAO;
import com.kt.openapi.web.faq.service.FaqService;
import com.kt.openapi.web.faq.vo.FaqVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
* <pre>
* 1. 패키지명 : com.kt.openapi.web.faq.service.impl
* 2. 타입명 : FaqServiceImpl.java
* 3. 작성일 : 2017. 11. 30. 오후 1:46:44
* 4. 작성자 : ANEUNTAEK
* 5. 설명 : FAQ
* </pre>
 */
@Service("faqService")
public class FaqServiceImpl implements FaqService {

	@Autowired
	private FaqDAO faqDAO;
	
	private static final Logger logger = LoggerFactory.getLogger(FaqServiceImpl.class);

	/**
	 * 
	* <pre>
	* 1. 메소드명 : selFaqList
	* 2. 작성일 : 2017. 11. 30. 오후 1:45:24
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : FAQ 목록 조회
	* </pre>
	* @param param
	* @return
	* @throws Exception
	 */
	@Override
	public List<FaqVO> selFaqList(FaqVO param) throws Exception {
		// TODO Auto-generated method stub
		return faqDAO.selFaqList(param);
	}

	/**
	 * 
	* <pre>
	* 1. 메소드명 : selToplist
	* 2. 작성일 : 2017. 11. 30. 오후 1:45:32
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : FAQ TOP5 목록 조회
	* </pre>
	* @param param
	* @return
	* @throws Exception
	 */
	@Override
	public List<FaqVO> selToplist(FaqVO param) throws Exception {
		// TODO Auto-generated method stub
		return faqDAO.selToplist(param);
	}

	/**
	 * 
	* <pre>
	* 1. 메소드명 : selfaqCnt
	* 2. 작성일 : 2017. 11. 30. 오후 1:45:37
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : FAQ 목록 개수
	* </pre>
	* @param param
	* @return
	* @throws Exception
	 */
	@Override
	public int selfaqCnt(FaqVO param) throws Exception {
		// TODO Auto-generated method stub
		return faqDAO.selfaqCnt(param);
	}

	/**
	 * 
	* <pre>
	* 1. 메소드명 : selTopCnt
	* 2. 작성일 : 2017. 11. 30. 오후 1:45:43
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : FAQ TOP5 목록 개수
	* </pre>
	* @param param
	* @return
	* @throws Exception
	 */
	@Override
	public int selTopCnt(FaqVO param) throws Exception {
		// TODO Auto-generated method stub
		return faqDAO.selTopCnt(param);
	}

	/**
	* <pre>
	* 1. 메소드명 : upRCnt
	* 2. 작성일 : 2018. 1. 4. 오후 8:51:38
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 조회수 증가
	* </pre>
	* @param param
	* @throws Exception
	*/
	@Override
	public void upRCnt(FaqVO param) throws Exception {
		// TODO Auto-generated method stub
		faqDAO.upRCnt(param);
	}

}
