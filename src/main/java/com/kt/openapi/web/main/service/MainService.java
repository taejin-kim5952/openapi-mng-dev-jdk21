package com.kt.openapi.web.main.service;

import java.util.List;

import com.kt.openapi.web.main.vo.MainBBSVO;
import com.kt.openapi.web.main.vo.MainVO;

/**
 * 
* <pre>
* 1. 패키지명 : com.kt.openapi.web.main.service
* 2. 타입명 : MainService.java
* 3. 작성일 : 2017. 12. 1. 오후 5:58:49
* 4. 작성자 : ANEUNTAEK
* 5. 설명 :
* </pre>
 */
public interface MainService {
	
	
	/**
	* <pre>
	* 1. 메소드명 : selRecNotice
	* 2. 작성일 : 2017. 12. 1. 오후 5:59:07
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 공지사항 최신글 3개
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	List<MainBBSVO> selRecNotice(MainVO param) throws Exception;

	/**
	* <pre>
	* 1. 메소드명 : selRecDevF
	* 2. 작성일 : 2017. 12. 1. 오후 5:59:11
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 개발자 포럼 최신글 3개
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	List<MainBBSVO> selRecDevF(MainVO param) throws Exception;

}
