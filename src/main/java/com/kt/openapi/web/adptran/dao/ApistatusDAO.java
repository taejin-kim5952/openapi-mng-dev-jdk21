package com.kt.openapi.web.adptran.dao;

import com.kt.openapi.web.adptran.vo.ApiStatusGroupVO;
import com.kt.openapi.web.adptran.vo.ApiStatusVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ApistatusDAO {

    /** API상태그룹목록 */
    List<ApiStatusGroupVO> select_API_STATUS_SPC_GROUP_LIST(Map<String, Object> params);

    /** 그룹별API상태요약정보목록 */
    List<ApiStatusGroupVO> select_GROUP_SUMMARY_LIST(Map<String, Object> params);

    /** 그룹API상태요약정보 */
    ApiStatusGroupVO select_GROUP_SUMMARY_INFO(Map<String, Object> params);

    /** 그룹API상태목록-대표정보 */
    List<ApiStatusVO> select_GROUP_API_STATUS_LIST(Map<String, Object> params);

    /** 그룹API상태목록(pagination)-검색정보 */
    List<ApiStatusVO> select_API_STATUS_INFO_LIST(Map<String, Object> params);

    /** 그룹API상태목록(pagination)-검색정보 count */
    int count_select_API_STATUS_INFO_LIST(Map<String, Object> params);

    /** 그룹API상태목록-검색정보-상태별 count */
    List<ApiStatusVO> select_API_STATUS_INFO_LIST_STATUS_CODE_COUNT(Map<String, Object> params);

    /** 그룹API상태목록(pagination)-검색정보-일별요약 */
    List<ApiStatusVO> select_API_STATUS_CHECK_HIST_DAILY_LIST(Map<String, Object> params);

    /** 그룹API상태목록(pagination)-검색정보-일별요약 count */
    int count_select_API_STATUS_CHECK_HIST_DAILY_LIST(Map<String, Object> params);

    /** 그룹API상태이력정보목록 - [마이그레이션] EgovMap -> VO 전환 */
    List<ApiStatusVO> select_API_STATUS_CHECK_HIST_LIST(Map<String, Object> params);

    /** 그룹API상태이력정보 상세 */
    ApiStatusVO select_API_STATUS_CHECK_HIST(Map<String, Object> params);

    /** 시스템-서비스 목록 */
    List<ApiStatusVO> select_API_SYSTEM_SPC_LIST(Map<String, Object> params);

    /** API상태그룹사용자설정정보 삭제 */
    int delete_API_STATUS_SPC_GROUP_USER_LINK(Map<String, Object> params);

    /** API상태그룹사용자설정정보 입력 */
    int insert_API_STATUS_SPC_GROUP_USER_LINK(Map<String, Object> params);

    /** API그룹 목록 */
    List<ApiStatusGroupVO> select_dum_API_STATUS_SPC_GROUP_LIST(Map<String, Object> params);
}
