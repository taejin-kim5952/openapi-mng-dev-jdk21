package com.kt.openapi.web.adptran.dao;

import com.kt.openapi.web.adptran.vo.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BeastDAO {

    /** BEAST-시스템-목록 - [마이그레이션] EgovMap -> VO 전환 */
    List<BstSyncAdmSysDplyVO> select_BST_SYNC_ADM_SYS_DPLY_list(Map<String, Object> params);

    /** BEAST-시스템-목록 count */
    int select_BST_SYNC_ADM_SYS_DPLY_count(Map<String, Object> params);

    /** BEAST-시스템-merge */
    int merge_BST_SYNC_ADM_SYS_DPLY(Map<String, Object> params);

    /** BEAST-API-목록 - [마이그레이션] EgovMap -> VO 전환 */
    List<BstSyncAdmApiDplyVO> select_BST_SYNC_ADM_API_DPLY_list(Map<String, Object> params);

    /** BEAST-API-목록 count */
    int select_BST_SYNC_ADM_API_DPLY_count(Map<String, Object> params);

    /** BEAST-API-merge */
    int merge_BST_SYNC_ADM_API_DPLY(Map<String, Object> params);

    /** BEAST-SVC-목록 - [마이그레이션] EgovMap -> VO 전환 */
    List<BstSyncAdmSvcDplyVO> select_BST_SYNC_ADM_SVC_DPLY_list(Map<String, Object> params);

    /** BEAST-SVC-목록 count */
    int select_BST_SYNC_ADM_SVC_DPLY_count(Map<String, Object> params);

    /** BEAST-SVC-merge */
    int merge_BST_SYNC_ADM_SVC_DPLY(Map<String, Object> params);

    /** BEAST I/F Execute 이력-insert */
    int insert_BST_IF_EXEC_HIST(Map<String, Object> params);

    /** BEAST-I/F LOG-R - [마이그레이션] EgovMap -> VO 전환 */
    List<BstIfExecHistVO> sel_BST_IF_EXEC_HIST(Map<String, Object> params);

    /** APILink서비스신청-기본정보 - [마이그레이션] EgovMap -> VO 전환 */
    TDevApplyInfoVO sel_T_DEV_APPLY_INFO(Map<String, Object> params);

    /** APILink서비스신청-API정보 - [마이그레이션] EgovMap -> VO 전환 */
    List<BstDevApplyApiVO> sel_T_DEV_APPLY_API_list(Map<String, Object> params);

    /** APILink서비스신청-IP정보 - [마이그레이션] EgovMap -> VO 전환 */
    List<BstIpListVO> sel_getIp_list(Map<String, Object> params);

    /** API배포(BEAST)-목록 - [마이그레이션] EgovMap -> VO 전환 */
    List<BstApiDeployVO> selApiDeployBeastList(Map<String, Object> params);

    /** API배포(BEAST)-목록 count */
    int selApiDeployBeastCount(Map<String, Object> params);

    /** 배포API-R - [마이그레이션] EgovMap -> VO 전환 */
    BstApiDeployVO selDeployView(Map<String, Object> params);

    /** API상태별갯수-목록 - [마이그레이션] EgovMap -> VO 전환 */
    List<BstApiStatusCountVO> selBeastApiCountGroupByStatus(Map<String, Object> params);

    /** SB_CHECK - [마이그레이션] EgovMap -> VO 전환 */
    TDevApplyInfoVO sel_SB_CHECK(Map<String, Object> params);

    /** TB_APPINSTID 조회 - [마이그레이션] EgovMap -> VO 전환 */
    TDevApplyInfoVO sel_TB_APPINSTID(Map<String, Object> params);

    /** BEAST-시스템-상세 - [마이그레이션] EgovMap -> VO 전환 */
    BstSyncAdmSysDplyVO sel_BST_SYNC_ADM_SYS_DPLY(Map<String, Object> params);

    /** 트래픽 SPC 목록 - [마이그레이션] EgovMap -> VO 전환 */
    List<BstApiTrafficVO> select_apitraffic_spclist(Map<String, Object> params);

    /** 트래픽 DEF 목록 - [마이그레이션] EgovMap -> VO 전환 */
    List<BstApiTrafficVO> select_apitraffic_deflist(Map<String, Object> params);

    /** 트래픽 API 통계 - [마이그레이션] EgovMap -> VO 전환 */
    List<BstApiTrafficVO> select_apitraffic_api(Map<String, Object> params);

    /** 트래픽 DEF API 통계 - [마이그레이션] EgovMap -> VO 전환 */
    List<BstApiTrafficVO> select_apitraffic_defapi(Map<String, Object> params);

    /** 트래픽 DEF API 통계 v1 - [마이그레이션] EgovMap -> VO 전환 */
    List<BstApiTrafficVO> select_apitraffic_defapi_v1(Map<String, Object> params);

    /** BEAST-시스템-삭제 */
    int delete_BST_SYNC_ADM_SYS_DPLY(Map<String, Object> params);

    /** BEAST-API-삭제 */
    int delete_BST_SYNC_ADM_API_DPLY(Map<String, Object> params);

    /** BEAST-SVC-삭제 */
    int delete_BST_SYNC_ADM_SVC_DPLY(Map<String, Object> params);

    /** BEAST-API_LINK_DATA-목록 */
    List<BstSyncAdmApiLinkDataVO> select_BST_SYNC_ADM_API_LINK_DATA_list(Map<String, Object> params);

    /** BEAST-API_LINK_DATA-목록 count */
    int select_BST_SYNC_ADM_API_LINK_DATA_count(Map<String, Object> params);

    /** BEAST-API_LINK_DATA-merge */
    int merge_BST_SYNC_ADM_API_LINK_DATA(Map<String, Object> params);

    /** BEAST-API_LINK_DATA-삭제 */
    int delete_BST_SYNC_ADM_API_LINK_DATA(Map<String, Object> params);

    /** PORTAL SVC 삭제(TB) */
    int delete_PORTAL_SVC_TB(Map<String, Object> params);

    /** PORTAL SVC 삭제(상용) */
    int delete_PORTAL_SVC_SB(Map<String, Object> params);

    // -- [마이그레이션] KsmCmnDAO.selectQueryList() → BeastDAO로 이전 (Map 기반)
    /** API배포(BEAST)-목록 (Map 기반) */
    List<Map<String, Object>> selApiDeployBeastList_map(Map<String, Object> params);

    /** API상태별갯수-목록 (Map 기반) */
    List<Map<String, Object>> selBeastApiCountGroupByStatus_map(Map<String, Object> params);

    /** 트래픽 SPC 목록 (Map 기반) */
    List<Map<String, Object>> select_apitraffic_spclist_map(Map<String, Object> params);

    /** 트래픽 DEF 목록 (Map 기반) */
    List<Map<String, Object>> select_apitraffic_deflist_map(Map<String, Object> params);

    /** 트래픽 API 통계 (Map 기반) */
    List<Map<String, Object>> select_apitraffic_api_map(Map<String, Object> params);

    /** 트래픽 DEF API 통계 (Map 기반) */
    List<Map<String, Object>> select_apitraffic_defapi_map(Map<String, Object> params);

    /** 트래픽 DEF API 통계 v1 (Map 기반) */
    List<Map<String, Object>> select_apitraffic_defapi_v1_map(Map<String, Object> params);
}
