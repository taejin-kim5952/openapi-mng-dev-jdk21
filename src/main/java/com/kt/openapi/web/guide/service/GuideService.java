package com.kt.openapi.web.guide.service;

import java.util.ArrayList;

import com.kt.openapi.web.guide.vo.GuideShubVO;


public interface GuideService {

	
	/**
	 * <pre>
	 * 1. 메소드명 : selGuideShubList
	 * 2. 작성일   : 2017. 12. 12. 오후 7:31:33
	 * 3. 작성자   : JeonGeun Kang
	 * 4. 설명     : shub 가이드 페이지에서 shub list 조회
	 * </pre>
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public ArrayList<GuideShubVO> selGuideShubList() throws Exception ;
}
