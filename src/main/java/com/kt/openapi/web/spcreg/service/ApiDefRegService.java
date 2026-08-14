package com.kt.openapi.web.spcreg.service;

import com.kt.openapi.web.spcreg.vo.ApiDefRegVO;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.spcreg.service
 * 2. 타입명   : ApiDefRegService.java
 * 5. 설명     : "API 등록"(기존 SPC에 API 추가) 화면 전용 서비스.
 * </pre>
 */
public interface ApiDefRegService {

    List<Map<String, Object>> selDefListByApiSpcNo(String apiSpcNo);

    Map<String, Object> selSpcByNo(String apiSpcNo);

    /** API(DEF) 1건의 상세+파라미터를 함께 조회 (좌측 트리 클릭 시 폼에 불러오기용) */
    Map<String, Object> selApiDefDetail(String apiNo);

    /** 템플릿 선택 팝업용 목록 */
    List<Map<String, Object>> selTmpltList();

    /** Provider(단위서비스코드) 선택 팝업용 목록 */
    List<Map<String, Object>> selApiProviderList();

    /** API ID가 이미 쓰이고 있는지(중복이면 true). apiNo가 있으면(수정 모드) 자기 자신은 제외,
        apiVerNo가 있으면(버전업 진행 중) 같은 버전 패밀리도 제외 */
    boolean selApiIdChk(String apiId, String apiNo, String apiVerNo);

    /** 다음 API ID 제안값 */
    String selNextApiId();

    /** BEAST G/W 시스템 검색 */
    List<Map<String, Object>> selBstSysList(String target, String sysId, String sysNm);

    /** 카테고리(재사용 또는 최초 1회 생성)+DEF+파라미터까지 한 트랜잭션으로 등록하고, apiSpcNo를 반환 */
    String savApiDefReg(ApiDefRegVO vo);

    /** 기존 API(DEF)를 수정하고(파라미터는 삭제 후 재구성) apiSpcNo를 반환 */
    String updApiDefReg(ApiDefRegVO vo);
}
