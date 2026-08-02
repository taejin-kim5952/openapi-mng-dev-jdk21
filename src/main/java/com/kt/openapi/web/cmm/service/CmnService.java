package com.kt.openapi.web.cmm.service;

import java.util.ArrayList;

import com.kt.openapi.web.cmm.vo.CmnCdVO;


/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.cmm.service
* 2. 타입명 : CmnService.java
* 3. 작성일 : 2017. 11. 28. 오후 5:20:07
* 4. 작성자 : JungHwan Hwang
* 5. 설명 : 공통코드 목록
* </pre>
*/
public interface CmnService {
	
	/**
	* <pre>
	* 1. 메소드명 : selComnList
	* 2. 작성일 : 2017. 11. 28. 오후 5:20:01
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 공통코드 조회
	* </pre>
	* @param groupCd
	* @return
	* @throws Exception
	*/
	public ArrayList<CmnCdVO> selComnList(String groupCd) throws Exception;	
	
}
