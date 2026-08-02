package com.kt.openapi.web.mypage.service;

import com.kt.openapi.web.mypage.vo.MypageVO;
import com.kt.openapi.web.userJoin.vo.UserHistVO;

import java.util.List;
import java.util.Map;

/**
 * 
* <pre>
* 1. 패키지명 : com.kt.openapi.web.mypage.service
* 2. 타입명 : MypageService.java
* 3. 작성일 : 2017. 11. 30. 오후 2:29:39
* 4. 작성자 : ANEUNTAEK
* 5. 설명 : MYPAGE
* </pre>
 */
public interface MypageService {

	/**
	 * 
	* <pre>
	* 1. 메소드명 : getApiRevCnt
	* 2. 작성일 : 2017. 11. 30. 오후 2:31:52
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : API 검토 요청 건수 및 API 개발 요청 건수 
	* </pre>
	* @param param
	* @return
	* @throws Exception
	 */
	int getApiRevCnt(MypageVO param)  throws Exception;
	
	/**
	 * 
	* <pre>
	* 1. 메소드명 : selWriteApi
	* 2. 작성일 : 2017. 11. 30. 오후 2:32:10
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 작성중 API 상세보기
	* </pre>
	* @param param
	* @return
	* @throws Exception
	 */
	Map<String, Object> selWriteApi(MypageVO param)  throws Exception;
	
	/**
	 * 
	* <pre>
	* 1. 메소드명 : selDevFView
	* 2. 작성일 : 2017. 11. 30. 오후 2:32:16
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 나의 개발자 포럼 최신글 1개 
	* </pre>
	* @param param
	* @return
	* @throws Exception
	 */
	Map<String, Object> selDevFView(MypageVO param)  throws Exception;
	
	/**
	 * 
	* <pre>
	* 1. 메소드명 : selDevFCnt
	* 2. 작성일 : 2017. 11. 30. 오후 2:32:23
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 나의 개발자 포럼 댓글 개수
	* </pre>
	* @param param
	* @return
	* @throws Exception
	 */
	int selDevFCnt(MypageVO param)  throws Exception;
	
	/**
	 * 
	* <pre>
	* 1. 메소드명 : selQAView
	* 2. 작성일 : 2017. 11. 30. 오후 2:32:29
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 나의 Q&A 상세글
	* </pre>
	* @param param
	* @return
	* @throws Exception
	 */
	Map<String, Object> selQAView(MypageVO param)  throws Exception;

	/**
	 * 
	* <pre>
	* 1. 메소드명 : selAutList
	* 2. 작성일 : 2017. 11. 30. 오후 2:32:35
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 나의 보유 권한 최대 5개
	* </pre>
	* @param svo
	* @return
	* @throws Exception
	 */
	List<Map<String, Object>> selAutList(MypageVO svo) throws Exception;
	
	/**
	 * 
	* <pre>
	* 1. 메소드명 : selAutReq
	* 2. 작성일 : 2017. 11. 30. 오후 2:32:42
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 권한요청 상태 최대 5개
	* </pre>
	* @param svo
	* @return
	* @throws Exception
	 */
	List<Map<String, Object>> selAutReq(MypageVO svo) throws Exception;
	
	/**
	 * 
	* <pre>
	* 1. 메소드명 : newAutReq
	* 2. 작성일 : 2017. 11. 30. 오후 2:32:48
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 권한 요청 등록
	* </pre>
	* @param param
	* @throws Exception
	 */
	void newAutReq(MypageVO param)throws Exception;

	/**
     * 
    * <pre>
    * 1. 메소드명 : selboxSysNm
    * 2. 작성일 : 2017. 11. 30. 오후 2:32:54
    * 3. 작성자 : ANEUNTAEK
    * 4. 설명 : 시스템 목록
    * </pre>
    * @param param
    * @return
    * @throws Exception
     */
	List<Map<String, Object>> selboxSysNm(MypageVO param) throws Exception;
	
	/**
     * 
    * <pre>
    * 1. 메소드명 : selboxAGroup
    * 2. 작성일 : 2017. 11. 30. 오후 2:33:00
    * 3. 작성자 : ANEUNTAEK
    * 4. 설명 : 권한 그룹 목록
    * </pre>
    * @param param
    * @return
    * @throws Exception
     */
	List<Map<String, Object>> selboxAGroup(MypageVO param) throws Exception;

	/**
     * 
    * <pre>
    * 1. 메소드명 : selBack
    * 2. 작성일 : 2017. 11. 30. 오후 2:33:09
    * 3. 작성자 : ANEUNTAEK
    * 4. 설명 : 반려 사유
    * </pre>
    * @param param
    * @return
    * @throws Exception
     */
	Map<String, Object> selBack(MypageVO param)  throws Exception;
	
	/**
     * 
    * <pre>
    * 1. 메소드명 : autHist
    * 2. 작성일 : 2017. 11. 30. 오후 2:33:14
    * 3. 작성자 : ANEUNTAEK
    * 4. 설명 : 회원 관리 이력
    * </pre> 
    * @param param
    * @throws Exception
     */
	void autHist(UserHistVO param)throws Exception;

	/**
	 * 
	* <pre>
	* 1. 메소드명 : updateMbr
	* 2. 작성일 : 2017. 12. 5. 오후 4:05:10
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 회원 상태 수정
	* </pre>
	* @param param
	* @throws Exception
	 */
	void updateMbr (MypageVO param) throws Exception;
    
	
	/**
	* <pre>
	* 1. 메소드명 : chkInsCnt
	* 2. 작성일 : 2017. 12. 11. 오후 9:25:10
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 권한 요청 등록시 개수 체크
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	int chkInsCnt(MypageVO param)  throws Exception;

	int chkDupCnt(MypageVO mypageVo) throws Exception;
	
}
