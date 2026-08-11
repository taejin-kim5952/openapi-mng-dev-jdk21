package com.kt.openapi.web.api.dao;

import com.kt.openapi.web.api.vo.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.api.dao
 * 2. 타입명   : ApiRegDAO.java
 * 3. 작성일   : 2017. 11. 10. 오후 2:54:48
 * 4. 작성자   : JungHwan Hwang
 * 5. 설명     : API 등록 관련 MyBatis Mapper 인터페이스
 * </pre>
 */
@Mapper
public interface ApiRegDAO {

    /** API명세 정보 등록 (생성된 API_SPC_NO는 selectKey로 vo.apiSpcNo에 채워짐) */
    int savApiRegInfo(ApiRegVO vo);

    /** Arsenal API 네임스페이스 정보 등록 */
    void savArsenalApiNS(Map<String, Object> params);

    /** Arsenal API 네임스페이스 정보 수정 */
    void updArsenalApiNS(Map<String, Object> params);

    /** API명세 정보 수정 */
    void updApiRegInfo(ApiRegVO vo);

    /** YAML 경로 조회 - [마이그레이션] EgovMap -> VO 전환 */
    ApiYamlVO selApiFileInfo(ApiRegVO vo);

    /** API 명 중복 체크 */
    int salApiNmDupCheck(ApiRegVO vo);

    /** api operationId 추가 */
    int salApiIdCheck(ApiRegVO vo);

    /** 카테고리명 중복 체크 */
    int selApiCateNmDupCheck(ApiRegVO vo);

    /** api 이름 중복 체크 */
    int selApiNmDupCheck(ApiRegVO vo);

    /** api 이름 중복 체크기본정보 */
    int selApiInfoNmDupCheck(ApiRegVO vo);

    /** 카테고리 안의 동일안 Path 가 존재하는지 체크 */
    int salApijDupPathCheck(ApiRegVO vo);

    /** API path 명 중복 체크 */
    int salApiPathDupCheck(ApiRegVO vo);

    /** API path의 api 명 중복 체크 */
    int salApiPathNmDupCheck(ApiRegVO vo);

    /** API 불러오기 / 템플릿 불러오기 목록 조회 - [마이그레이션] EgovMap -> VO 전환 */
    List<ApiImportVO> selImportApiList(ApiRegVO vo);

    /** API 불러오기 / 템플릿 불러오기 목록 조회 의 전체 수 */
    int selImportApiTotalList(ApiRegVO vo);

    /** 카테고리 등록 (생성된 API_CTGRY_NO는 selectKey로 vo.apiCtgryNo에 채워짐) */
    int savApiCateInfo(ApiRegVO vo);

    /** 카테고리 수정 */
    void updApiCateInfo(ApiRegVO vo);

    /** 카테고리 순서 수정 */
    void updApiCateSortOrder(ApiRegVO vo);

    /** 카테고리 삭제 */
    int delApiCateInfo(ApiRegVO vo);

    /** API 상세 조회 - [마이그레이션] EgovMap -> VO 전환 */
    ApiDefVO selApiInfo(ApiRegVO vo);

    /** YAML 정보만 수정 */
    int updApiYamlInfo(ApiRegVO vo);

    /** 카테고리 정보 조회 - [마이그레이션] EgovMap -> VO 전환 */
    ApiCategoryVO selCateInfo(ApiRegVO vo);

    /** 카테고리 목록 조회 ( 패스에 연결되지 않은 ) - [마이그레이션] EgovMap -> VO 전환 */
    List<ApiCategoryVO> selCateList(ApiRegVO vo);

    /** 패스 목록 조회 ( 카테고리에 연결되지 않은 ) - [마이그레이션] EgovMap -> VO 전환 */
    List<ApiDefVO> selPathList(ApiRegVO vo);

    /** 패스 목록 조회 ( 카테고리에 연결되지 않은 ) - [마이그레이션] EgovMap -> VO 전환 */
    List<ApiDefVO> selCatePathList(ApiRegVO vo);

    /** API 정의 등록 (생성된 API_NO는 selectKey로 vo.apiNo에 채워짐) */
    int savApiPathInfo(ApiRegVO vo);

    /** API 정의 수정 */
    int updApiPathInfo(ApiRegVO vo);

    /** API 영향도 정보 등록 */
    int savApiImpactInfo(ApiRegVO vo);

    /** API 영향도 정보 수정 */
    int updApiImpactInfo(ApiRegVO vo);

    /** MTHTYP1000 의 목록 조회 - [마이그레이션] EgovMap -> Map 전환 */
    List<Map<String, Object>> selMethodDupList(ApiRegVO vo);

    /** PATH 삭제 */
    int delApiPath(ApiRegVO vo);

    /** apiSpcNo 건의 DEF 테이블 삭제 */
    int delApiSpcPath(ApiRegVO vo);

    /** apiSpcNo 건의 PARAM 테이블 삭제 */
    int delApiSpcPathParam(ApiRegVO vo);

    /** 한건의 PATH에 대한 파라미터 데이터 삭제 */
    int delApiPathParam(ApiRegVO vo);

    /** 동일 패스의 전체 삭제 */
    int delApiAllPath(ApiRegVO vo);

    /** 동일 패스의 전체 삭제시의 파리미터 삭제 */
    int delApiAllPathParam(ApiRegVO vo);

    /** 동일 패스의 전체 삭제시의 파라미터 컨텐츠 타입 삭제 */
    int delApiAllPathParamType(ApiRegVO vo);

    /** IMPACT 삭제 */
    int delApiImpact(ApiRegVO vo);

    /** apiSpcNo 건의 IMPACT 테이블 삭제 */
    int delApiSpcImpact(ApiRegVO vo);

    /** 동일 패스의 전체 IMPACT 삭제 */
    int delApiAllImpact(ApiRegVO vo);

    /** 파라미터 등록 (생성된 PARAM_NO는 selectKey로 vo.paramNo에 채워짐) */
    int savApiParamInfo(ApiRegVO vo);

    /** 저장할 API_NO 번호 조회 */
    String selApiPathApiNo(ApiRegVO vo);

    /** 카테고리 번호 조회 */
    String selApiCategoryNo(ApiRegVO vo);

    /** API 관련 이력 관리 */
    int updApiHisInfo(ApiRegVO vo);

    /** API명세 수정 : REST 요청 */
    int updApiRegRestBasic(ApiRegVO vo);

    /** API명세 수정(작업중으로 변경) : REST 요청 */
    int updApiRegRestBasicToWork(ApiRegVO vo);

    /** 파라미터의 컨텐츠 타입 저장 */
    int insApiParamContType(ApiRegVO vo);

    /** API에서 사용중인 사용자가 생성한 DATATYPE 목록을 조회 - [마이그레이션] EgovMap -> Map 전환 */
    List<Map<String, Object>> selApiDataTypeUseList(ApiRegVO vo);

    /** 다음 API ID 조회 */
    String selNextApiId(String prefix);

    /** 다음 API ID 정보 조회 - [마이그레이션] EgovMap -> Map 전환 */
    Map<String, Object> selNextApiIdInfo(Map<String, Object> map);

    /** 배포 프로세스 조회 - [마이그레이션] EgovMap -> Map 전환 */
    List<Map<String, Object>> selDeployProc(Map<String, Object> map);

    /** 네임스페이스별 API 수 및 정보 조회 - [마이그레이션] EgovMap -> VO 전환 */
    List<ApiNamespaceVO> selApiCountAndInfoByProjectNs(Map<String, Object> map);

    /** 네임스페이스별 SPC 정보 조회 - [마이그레이션] EgovMap -> VO 전환 */
    ApiSpcVO selApiSpcInfoByProjectNsWithNm(Map<String, Object> map);

    /** API 명세 이력 조회 - [마이그레이션] EgovMap -> VO 전환 */
    List<ApiHistoryVO> selApiSpcHistory(Map<String, Object> map);

    /** API 명세 수정 권한 체크 */
    int selApiSpcAuthCheck(ApiRegVO vo);

    /** 관리자 권한 체크 */
    int selMbrAuthCheck(ApiRegVO vo);

    /** KOA_TB_API_DEF 조회 - [마이그레이션] EgovMap -> VO 전환 */
    ApiDefVO selApiDef(ApiRegVO vo);

    /** API 패스 목록 조회 - [마이그레이션] EgovMap -> VO 전환 */
    List<ApiDefVO> selApiDefList(ApiRegVO vo);

    /** EDIT_FLAG 수정 */
    int updApiDefEditFlag(ApiRegVO vo);

    /** DPLY_REQ_FLAG 수정 */
    int updApiDefDplyReqFlag(ApiRegVO vo);

    /** TB_DPLY_STATUS 수정 */
    int updApiDefTbDplyStatus(ApiRegVO vo);

    /** DPLY_VERI_STATUS 수정 */
    int updApiDefDplyVeriStatus(ApiRegVO vo);

    /** PRD_DPLY_REQ_FLAG 수정 */
    int updApiDefPrdDplyReqFlag(ApiRegVO vo);

    /** PRD_DPLY_STATUS 수정 */
    int updApiDefPrdDplyStatus(ApiRegVO vo);

    /** 권한 그룹 중복 체크 */
    String selGrpAuthCheck(Map<String, Object> map);

    /** 권한 그룹 추가 (생성된 AUT_ID는 selectKey로 map의 "authId" 키에 채워짐) */
    int saveAutGrp(Map<String, Object> map);

    /** API Provider 목록 검색 - [마이그레이션] EgovMap -> Map 전환 */
    List<Map<String, Object>> selApiProviderList();

    /** API명/번호 체크 - [마이그레이션] EgovMap -> Map 전환 */
    Map<String, Object> selectApiNmNoCheck(ApiRegVO vo);

    /** API 번호 존재 확인 */
    int selectApiNoCount(String apiNo);

    /** API ID 중복 체크 */
    int selectApiIdChk(String apiId);

    // -- [마이그레이션] KsmCmnDAO.selectQuery() → ApiRegDAO로 이전 (Map 기반)
    /** API 번호 중복 체크 (Map 기반) */
    Map<String, Object> select_APINO_CHECK(Map<String, Object> params);
}

