package com.kt.openapi.web.faq.service;


import java.util.List;
import com.kt.openapi.web.faq.vo.FaqVO;

/**
 * 
* <pre>
* 1. 패키지명 : com.kt.openapi.web.faq.service
* 2. 타입명 : FaqService.java
* 3. 작성일 : 2017. 11. 30. 오후 1:45:17
* 4. 작성자 : ANEUNTAEK
* 5. 설명 : FAQ
* </pre>
 */
public interface FaqService {

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
	List<FaqVO> selFaqList(FaqVO param) throws Exception;

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
	List<FaqVO> selToplist(FaqVO param) throws Exception;

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
	int selfaqCnt(FaqVO param)  throws Exception;

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
	int selTopCnt(FaqVO param)  throws Exception;
	
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
	void upRCnt(FaqVO param) throws Exception;
	
}
