package com.kt.openapi.web.api.service;

import com.kt.openapi.web.api.vo.ApiHistoryVO;
import com.kt.openapi.web.api.vo.ApiNamespaceVO;
import com.kt.openapi.web.api.vo.ApiRegVO;
import com.kt.openapi.web.api.vo.ApiSpcVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [마이그레이션] EgovMap 제거 및 VO 전환
 */
public interface ApiArsenalService {
	/**
	* Gitlab서버에 등록된 프로젝트의 Yaml파일 조회
	*/
	void getYamlInfoFromGitlab(String fileName) throws Exception;
	
	/**
	* API 동기화
	*/
	public void syncApiDep(HashMap<String,Object> map, ApiRegVO vo) throws Exception;
	

	/**
	 * 네임스페이에 등록된 최신 프로젝트의 수정날짜와 등록된 API 갯수 조회
	 */
	public List<ApiNamespaceVO> selApiCountAndInfoByProjectNs(Map<String, Object> map) throws Exception;
	
	/**
	 * Namespace에 등록된 프로젝트 코드 조회
	 */
	public ApiSpcVO selApiSpcInfoByProjectNsWithNm(Map<String, Object> map) throws Exception;
	
	/**
	 * API 동기화 히스토리 조회
	 */
	public List<ApiHistoryVO> selApiSpcHistory(Map<String, Object> map) throws Exception;
}
