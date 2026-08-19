package com.kt.openapi.web.spcreg.dao;

import com.kt.openapi.web.spcreg.vo.ApiDefRegVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.spcreg.dao
 * 2. 타입명   : ApiDefRegDAO.java
 * 5. 설명     : "API 등록"(기존 SPC에 API 추가) 화면 전용 MyBatis Mapper. SPC는 만들지 않고,
 *              KOA_TB_API_CTGRY(재사용/최초생성)/KOA_TB_API_DEF/KOA_TB_API_PARAM에 대한
 *              자체 INSERT/조회만 담당한다.
 * </pre>
 */
@Mapper
public interface ApiDefRegDAO {

    /** 이 그룹(apiSpcNo)에 이미 등록된 API 목록 (좌측 트리 - 클릭하면 수정 로드) */
    List<Map<String, Object>> selDefListByApiSpcNo(String apiSpcNo);

    /** apiSpcNo 1건의 그룹 정보(sysId/host/basPath 등) - 화면 진입 시 유효성 검증 + 컨텍스트 조회용 */
    Map<String, Object> selSpcByNo(String apiSpcNo);

    /** API(DEF) 1건의 전체 상세(기본 정보+고급 설정) - 기존 API를 폼에 불러올 때 */
    Map<String, Object> selApiDefDetail(String apiNo);

    /** API(DEF) 1건의 파라미터 목록(입력/Query/출력) - 기존 API를 폼에 불러올 때 */
    List<Map<String, Object>> selParamListByApiNo(String apiNo);

    /** 템플릿 선택 팝업용 목록 (KOA_TB_API_QUICK_TMPLT, 사용중인 것만). quickApiReg가 쓰는 것과
        같은 테이블이지만, 화면 독립 원칙에 따라 이 화면 전용으로 별도 조회한다. */
    /** 이 SPC의 API그룹(카테고리) 목록 - 등록 화면 선택 드롭다운용 */
    List<Map<String, Object>> selCtgryListBySpc(String apiSpcNo);

    /** 같은 SPC 안에서 API그룹 이름이 겹치는 개수 */
    int selCtgryNmDupCnt(Map<String, Object> param);

    List<Map<String, Object>> selTmpltList();

    /** Provider(단위서비스코드) 선택 팝업용 목록 (KOA_TB_API_PROVIDER, 사용중인 것만). 기존 등록
        마법사(ApiReg)의 selApiProviderList와 같은 테이블이지만, 화면 독립 원칙에 따라 별도 조회한다. */
    List<Map<String, Object>> selApiProviderList();

    /** 선택한 apiSpcNo에 이미 있는 첫 카테고리 조회 (없으면 null - 그 그룹의 첫 API라는 뜻) */
    String selDefaultCtgryBySpc(String apiSpcNo);

    /** API ID 중복 개수 조회 (Map: apiId, apiNo(수정 시 자기 자신 제외용, 없으면 null)) */
    int selApiIdChk(Map<String, Object> params);

    /** 다음 API ID 제안값 조회 ("OIF_" + 5자리 순번) */
    String selNextApiId();

    /** BEAST G/W 시스템 검색 (Map: target(TB_KTC/TB_AZURE/PRD_KTC/PRD_AZURE), sysId, sysNm) */
    List<Map<String, Object>> selBstSysList(Map<String, Object> params);

    /** KOA_TB_API_CTGRY 기본 카테고리 등록 (생성된 PK는 selectKey로 vo.apiCtgryNo에 채워짐) */
    int savApiCtgry(ApiDefRegVO vo);

    /** KOA_TB_API_DEF 등록 (생성된 PK는 selectKey로 vo.apiNo에 채워짐) */
    int savApiDef(ApiDefRegVO vo);

    /** 신규 등록된 DEF의 API_VER_NO를 자기 자신의 apiNo로 채운다("새 버전 패밀리 시작") -
        버전업으로 만든 DEF는 이미 원본 apiVerNo를 갖고 있으므로 호출하지 않는다 */
    int updApiVerNoSelf(String apiNo);

    /** KOA_TB_API_DEF 수정 (기존 API를 폼에서 불러와 다시 저장할 때) */
    int updApiDef(ApiDefRegVO vo);

    /** 이 API의 입력/출력 파라미터 삭제 (수정 시 재구성 전 기존 값 정리) */
    int delParamsByApiNo(String apiNo);

    /** KOA_TB_API_PARAM 등록 (파라미터 1건, Map: apiNo/paramNm/dataTypeCd/required/paramDesc/sortOdrg/regr) */
    int savApiParam(Map<String, Object> params);
}
