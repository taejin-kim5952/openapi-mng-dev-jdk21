package com.kt.openapi.web.devsupport.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kt.openapi.web.devsupport.vo.DevSupportFileVo;
import com.kt.openapi.web.devsupport.vo.DevSupportManagerVo;
import com.kt.openapi.web.devsupport.vo.DevSupportSaveVo;
import com.kt.openapi.web.devsupport.vo.DevSupportVo;

public interface DevSupportService {

	/**
	* <pre>
	* 1. 메소드명 : selDevSupportList
	* 2. 작성일 : 2017. 12. 02.
	* 3. 작성자 : user
	* 4. 설명 : 기술지원 리스트 
	* </pre>
	* @param svo
	* @return
	* @throws Exception
	*/
	List<DevSupportVo> selDevSupportList(DevSupportVo dvo) throws Exception;
	
	/**
	* <pre>
	* 1. 메소드명 : saveForum
	* 2. 작성일 : 2017. 11. 16. 오후 4:02:26
	* 3. 작성자 : user
	* 4. 설명 : 기술지원 글 등록
	* </pre>
	* @param bbsSaveVo
	* @return
	* @throws Exception
	*/
	String saveForum(DevSupportSaveVo vo, MultipartFile uploadFile)  throws Exception;
	
	int updDevSupport(DevSupportSaveVo vo, MultipartFile uploadFile)   throws Exception;
	
	/**
	* <pre>
	* 1. 메소드명 : selDevSupportListCnt
	* 2. 작성일 : 2017. 12. 02. 오후 4:02:26
	* 3. 작성자 : user
	* 4. 설명 : 기술지원 전체 갯수
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	int selDevSupportListCnt(DevSupportVo param) throws Exception;
	
	
	/**
	* <pre>
	* 1. 메소드명 : selDevSupportView
	* 2. 작성일 : 2017. 12. 02. 오후 4:02:26
	* 3. 작성자 : user
	* 4. 설명 : 기술지원 VIEW
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	DevSupportVo selDevSupportView(DevSupportVo param)  throws Exception;
	

	/**
	* <pre>
	* 1. 메소드명 : selDevSupportFileList
	* 2. 작성일 : 2017. 12. 02. 오후 4:02:26
	* 3. 작성자 : user
	* 4. 설명 :  기술지원 첨부파일
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	List<DevSupportFileVo>  selDevSupportFileList(DevSupportVo param)  throws Exception;
	
	/**
	* <pre>
	* 1. 메소드명 : selDevSupportMyServiceList
	* 2. 작성일 : 2017. 12. 02. 오후 4:02:26
	* 3. 작성자 : user
	* 4. 설명 :  기존 등록 서비스 조회
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	List<DevSupportVo>  selDevSupportMyServiceList(DevSupportVo param)  throws Exception;
	
	
	/**
	* <pre>
	* 1. 메소드명 : selDevManagerList
	* 2. 작성일 : 2017. 12. 02. 오후 4:02:26
	* 3. 작성자 : user
	* 4. 설명 :  담당자 조회
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	List<DevSupportManagerVo> selDevSupportManagerList (DevSupportManagerVo param)  throws Exception;
	
	/**
	* <pre>
	* 1. 메소드명 : selDevSupportManagerListCnt
	* 2. 작성일 : 2017. 12. 02. 오후 4:02:26
	* 3. 작성자 : user
	* 4. 설명 : 담당자정보 전체 갯수
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	int selDevSupportManagerCnt(DevSupportManagerVo param) throws Exception;
	
	
	/**
	* <pre>
	* 1. 메소드명 : selDevSupportSdk
	* 2. 작성일 : 2018. 01. 08. 오후 4:02:26
	* 3. 작성자 : user
	* 4. 설명 :  SDK조회
	* </pre>
	* @param param
	* @return
	* @throws Exception
	*/
	List<DevSupportFileVo>  selDevSupportSdk()  throws Exception;
	
}
