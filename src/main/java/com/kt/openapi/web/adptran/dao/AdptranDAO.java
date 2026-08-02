package com.kt.openapi.web.adptran.dao;

import com.kt.openapi.web.adptran.vo.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdptranDAO {

    /** sample(검색) - [마이그레이션] EgovMap -> Map 전환 (VO 미생성 대상) */
    List<Map<String, Object>> select_sample(Map<String, Object> params);

    /** API 상세 조회 (API_SPC 포함) - [마이그레이션] EgovMap -> VO 전환 */
    AdptranApiVO select_API_DEF_with_API_SPC(Map<String, Object> params);

    /** API PARAM 목록 조회 - [마이그레이션] EgovMap -> VO 전환 */
    List<AdptranParamVO> select_API_PARAM_list(Map<String, Object> params);

    /** API PARAM 계층형 목록 조회 (검증용) - [마이그레이션] EgovMap -> VO 전환 */
    List<AdptranParamVO> select_API_PARAM_TEST_list(Map<String, Object> params);

    /** API 테스트케이스 조회 - [마이그레이션] EgovMap -> VO 전환 */
    List<AdptranTestcaseVO> select_API_TESTCASE(Map<String, Object> params);

    /** API 테스트케이스 등록 */
    int insert_API_TESTCASE(Map<String, Object> params);

    /** API 테스트케이스 수정 */
    int update_API_TESTCASE(Map<String, Object> params);

    /** API 테스트케이스 삭제 */
    int delete_API_TESTCASE(Map<String, Object> params);

    /** API 테스트케이스 목록 삭제 */
    int delete_API_TESTCASE_list(Map<String, Object> params);

    /** 검증 결과 상세 조회 - [마이그레이션] EgovMap -> VO 전환 */
    AdptranVeriConditionVO select_API_VERI_CONDITION(Map<String, Object> params);

    /** 다음 API ID 조회 */
    String select_NEXT_API_ID(String prefix);

    /** 다음 API ID 관련 정보 조회 - [마이그레이션] EgovMap -> VO 전환 */
    AdptranNextApiIdInfoVO select_NEXT_API_ID_INFO(Map<String, Object> params);

    /** 배포 처리 상태 조회 - [마이그레이션] EgovMap -> VO 전환 */
    List<AdptranDeployProcVO> select_DEPLOY_PROC(Map<String, Object> params);
}
