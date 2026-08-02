package com.kt.openapi.web.api.service;

import com.kt.openapi.web.api.vo.ApiListPopupVO;
import com.kt.openapi.web.api.vo.ApiSensitiveParamVO;
import com.kt.openapi.web.api.vo.SensitiveInfoSurveyVO;

import java.util.List;
import java.util.Map;

/**
 * API 민감정보 서비스 인터페이스
 */
public interface ApiSensitiveInfoService {
    
    /**
     * 민감정보 파라미터 목록 조회
     * @param sysnm 시스템명 (필터링용, null 이면 전체)
     * @return 파라미터 목록
     */
    List<ApiSensitiveParamVO> getParamList(String sysnm);
    
    /**
     * API 민감정보 등록
     * @param vo 민감정보 조사 VO
     * @throws Exception
     */
    void registerSensitiveInfo(SensitiveInfoSurveyVO vo) throws Exception;
    
    /**
     * API 목록 조회 (팝업용)
     * @param map 검색 조건
     * @return API 목록
     */
    List<ApiListPopupVO> getApiListForPopup(Map<String, Object> map) throws Exception;
    
    /**
     * API 목록 총 개수 조회
     * @param map 검색 조건
     * @return 총 개수
     */
    int getApiListForPopupCount(Map<String, Object> map) throws Exception;
    
    /**
     * 등급 업데이트 (INSERT 또는 UPDATE)
     * @param apiNo API No
     * @param paramNm 파라미터명
     * @param sensitivityLevel 민감도 등급 (1 또는 2)
     * @param updatedBy 업데이트 사용자 ID
     * @throws Exception
     */
    void updateGrade(String apiNo, String paramNm, String sensitivityLevel, String updatedBy) throws Exception;
    
    /**
     * 등급 일괄 업데이트 (배치 INSERT)
     * @param changes 변경 목록
     * @return 성공 개수
     * @throws Exception
     */
    int batchUpdateGrades(List<Map<String, Object>> changes) throws Exception;
}
