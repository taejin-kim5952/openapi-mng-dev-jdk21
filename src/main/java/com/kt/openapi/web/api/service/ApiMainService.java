/**
 *  OPEN API version 1.0
 *
 *  Copyright ⓒ 2017 kt corp. All rights reserved.
 *
 *  This is a proprietary software of kt corp, and you may not use this file except in
 *  compliance with license agreement with kt corp. Any redistribution or use of this
 *  software, with or without modification shall be strictly prohibited without prior written
 *  approval of kt corp, and the copyright notice above does not evidence any actual or
 *  intended publication of such software.
 * 
 */
package com.kt.openapi.web.api.service;

import com.kt.openapi.web.api.vo.*;

import java.util.List;
import java.util.Map;

public interface ApiMainService {

	/**
	* <pre>
	* 1. 메소드명 : selMainRequestCnt
	* 2. 작성일 : 2017. 11. 20. 오후 1:31:33
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 검토요청/개발요청 갯수 조회
	* </pre>
	* @param vo
	* @return
	* @throws Exception
	*/
	int selMainRequestCnt(ApiMainVo vo) throws Exception;
	
	
	/**
	* <pre>
	* 1. 메소드명 : selMainList
	* 2. 작성일 : 2017. 11. 20. 오후 1:36:03
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 작성중/등록완료/검토/요청 등의 목록 조회
	* </pre>
	* @param vo
	* @return
	* @throws Exception
	*/
	Map<String, Object> selMainList(ApiMainVo vo) throws Exception;
	
	/**
	* <pre>
	* 1. 메소드명 : selMainListCnt
	* 2. 작성일 : 2017. 11. 20. 오후 7:38:25
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 작성중/등록완료/검토/요청 등의 목록 전체 갯수 
	* </pre>
	* @param vo
	* @return
	* @throws Exception
	*/
	int selMainListTotalCnt(ApiMainVo vo) throws Exception;
	
	
	/**
	 * <pre>
	 * 1. 메소드명 : savDevReqReg
	 * 2. 작성일 : 2017. 11. 21. 오전 11:04:30
	 * 3. 작성자 : JungHwan Hwang
	 * 4. 설명 : 개발요청글 등록
	 * </pre>
	 *
	 * @param vo
	 * @return String
	 * @throws Exception
	 */
	String savDevReqReg(ApiMainVo vo) throws Exception;
	
	

	/**
	* <pre>
	* 1. 메소드명 : selRegistView
	* 2. 작성일 : 2017. 11. 21. 오후 7:19:21
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 개발요청/ 검토요청 상세조회
	* </pre>
	* @param vo
	* @return
	* @throws Exception
	*/
	public ApiReviewVO selRegistView(ApiMainVo vo) throws Exception;
	
	/**
	* <pre>
	* 1. 메소드명 : selReqReplyView
	* 2. 작성일 : 2017. 11. 21. 오후 7:19:24
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 개발요청/ 검토요청 상세조회 의 답글 목록
	* </pre>
	* @param vo
	* @return
	* @throws Exception
	*/
	public List<ApiReviewOpinVO> selReqReplyView(ApiMainVo vo) throws Exception;

	
	/**
	* <pre>
	* 1. 메소드명 : savDevReplyReg
	* 2. 작성일 : 2017. 11. 21. 오후 10:02:13
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 개발요청/ 검토요청 상세조회 의 답글 저장
	* </pre>
	* @param vo
	* @return
	* @throws Exception
	*/
	public int savDevReplyReg(ApiMainVo vo) throws Exception;
	
	/**
	* <pre>
	* 1. 메소드명 : selSysList
	* 2. 작성일 : 2017. 11. 21. 오후 1:57:07
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 전체 시스템 목록 조회
	* </pre>
	* @param vo
	* @return
	* @throws Exception
	*/
	public List<ApiSystemVO> selSysList(ApiMainVo vo) throws Exception;


	/**
	* <pre>
	* 1. 메소드명 : savApiVer
	* 2. 작성일 : 2017. 11. 22. 오후 3:46:21
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : API 버전 새로 생성
	* </pre>
	* @param vo
	* @return
	* @throws Exception
	*/
	public Map<String, Object> savApiVer(ApiMainVo vo) throws Exception;
	
	/**
	* <pre>
	* 1. 메소드명 : savReqFile
	* 2. 작성일 : 2017. 11. 21. 오후 2:46:34
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 파일 등록
	* </pre>
	* @param vo
	* @return
	* @throws Exception
	*/
	public int savReqFile(ApiMainVo vo) throws Exception;
	
	/**
	* <pre>
	* 1. 메소드명 : selFileList
	* 2. 작성일 : 2017. 11. 21. 오후 2:41:54
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 파일 조회
	* </pre>
	* @param vo
	* @return
	* @throws Exception
	*/
	public List<ApiReviewAtcVO> selFileList(ApiMainVo vo) throws Exception;
	
	
	/**
	* <pre>
	* 1. 메소드명 : delDevApi
	* 2. 작성일 : 2017. 11. 23. 오후 8:10:31
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 작업중인 API 삭제
	* </pre>
	* @param vo
	* @return
	* @throws Exception
	*/
	public int delDevApi(ApiMainVo vo) throws Exception;
	
	

	/**
	* <pre>
	* 1. 메소드명 : selReqInfo
	* 2. 작성일 : 2017. 12. 5. 오전 9:48:22
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 :
	* </pre>
	* @param vo
	* @return
	* @throws Exception
	*/
	public ApiSpcVO selReqInfo(ApiMainVo vo) throws Exception;
	
	
	/**
	* <pre>
	* 1. 메소드명 : insertApiVerUpDep
	* 2. 작성일 : 2017. 12. 5. 오후 3:52:32
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : API 버전 업
	* </pre>
	* @param orgVo
	* @throws Exception
	*/
	public void insertApiVerUpDep(ApiMainVo orgVo) throws Exception;
	
	
	
	/**
	* <pre>
	* 1. 메소드명 : salApiVerDupCheck
	* 2. 작성일 : 2017. 12. 21. 오후 2:31:51
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : API 버전업시, 동일한 버전 있는 지 체크
	* </pre>
	* @param orgVo
	* @return
	* @throws Exception
	*/
	public int salApiVerDupCheck(ApiMainVo orgVo) throws Exception;

	/**
	* <pre>
	* 1. 메소드명 : savApiPrivate
	* 2. 작성일 : 2019. 10. 14
	* 3. 작성자 : [tag:adpt][drm][add]
	* 4. 설명 : API Private전환
	* </pre>
	* @param vo
	* @return
	* @throws Exception
	*/
	public Map<String, Object> savApiPrivate(ApiMainVo vo) throws Exception;
	
	/**
	* <pre>
	* 1. 메소드명 : selYamlInfo
	* 2. 작성일 : 2020.05. 07
	* 3. 작성자 : CYD
	* 4. 설명 : YAML 데이터 가져오기
	* </pre>
	* @param vo
	* @return
	* @throws Exception
	*/
	public ApiSpcVO selYamlInfo(ApiMainVo vo) throws Exception;
}

