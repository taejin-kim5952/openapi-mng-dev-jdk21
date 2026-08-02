package com.kt.openapi.web.api.dao;

import com.kt.openapi.web.api.vo.ApiListPopupVO;
import com.kt.openapi.web.api.vo.ApiSensitiveParamVO;
import com.kt.openapi.web.api.vo.SensitiveInfoStatisticsVO;
import com.kt.openapi.web.api.vo.SensitiveInfoSurveyVO;
import com.kt.openapi.web.api.vo.SystemStatisticsVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * API 민감정보 DAO
 */
@Mapper
public interface ApiSensitiveInfoDAO {

    /**
     * 민감정보 파라미터 목록 조회 (전체)
     * @return 파라미터 목록
     */
    List<ApiSensitiveParamVO> getParamList();

    /**
     * 민감정보 파라미터 목록 조회 (시스템명 필터링)
     * @param sysnm 시스템명
     * @return 파라미터 목록
     */
    List<ApiSensitiveParamVO> getParamListBySysnm(String sysnm);

    /**
     * 시스템명 목록 조회
     * @return 시스템명 목록
     */
    List<String> getSystemNameList();

    /**
     * 통계 정보 조회
     * @return 통계 VO (totalParams, grade1Count, grade2Count)
     */
    SensitiveInfoStatisticsVO getStatistics();

    /**
     * 시스템별 통계 조회
     * @return 시스템별 통계 목록
     */
    List<SystemStatisticsVO> getSystemStatistics();

    /**
     * API 민감정보 등록 (INSERT)
     * @param vo 민감정보 조사 VO
     */
    void insertSensitiveInfoSurvey(SensitiveInfoSurveyVO vo);

    /**
     * API 목록 조회 (페이지네이션용)
     * @param map 검색 조건 (offset, pageSize, searchTerm)
     * @return API 목록
     */
    List<ApiListPopupVO> getApiListForPopup(Map<String, Object> map);

    /**
     * API 목록 총 개수 조회
     * @param map 검색 조건
     * @return 총 개수
     */
    int getApiListForPopupCount(Map<String, Object> map);

    /**
     * 등급 업데이트 (UPDATE)
     * @param paramMap 변경 정보 (apiNo, paramNm, sensitivityLevel, updatedBy)
     * @return 업데이트된 행 수
     */
    int updateGrade(Map<String, Object> paramMap);

    /**
     * 등급 INSERT (새로운 데이터)
     * @param paramMap 등급 정보
     */
    void insertGradeIfNotExists(Map<String, Object> paramMap);


}
