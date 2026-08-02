package com.kt.openapi.web.guide.service.impl;

import com.kt.openapi.web.guide.dao.GuideDAO;
import com.kt.openapi.web.guide.service.GuideService;
import com.kt.openapi.web.guide.vo.GuideShubVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service("guideService")
public class GuideServiceImpl implements GuideService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GuideServiceImpl.class);

	// TODO ibatis 사용
	@Autowired
	private GuideDAO guideDAO;
	
	/**
	* <pre>
	* 1. 메소드명 : selGuideShubList
	* 2. 작성일 : 2017. 12. 12. 오후 5:12:31
	* 3. 작성자 : Jeon Geun Kang
	* 4. 설명   : shub 가이드 페이지에서 shub list 조회
	* </pre>
	* @param vo
	* @return
	* @throws Exception
	*/
	public ArrayList<GuideShubVO> selGuideShubList() throws Exception {
		return (ArrayList<GuideShubVO>)guideDAO.selGuideShubList();
	}
}
