package com.kt.openapi.web.spcreg.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.spcreg.dao
 * 2. 타입명   : OasYamlDAO.java
 * 5. 설명     : KOA_TB_API_DEF/PARAM(정본)에서 OAS YAML을 생성하기 위한 조회 + 생성 결과 반영 Mapper.
 *              매핑 규칙 전문은 docs/04_OAS_GENERATOR_MAPPING.md 참조.
 * </pre>
 */
@Mapper
public interface OasYamlDAO {

    /** 문서 루트(info/servers)용 SPC 1건. spcSrcCd 포함 - 재생성 대상 판정에 쓴다. */
    Map<String, Object> selSpcForOas(String apiSpcNo);

    /** tags[] 용 카테고리 목록 */
    List<Map<String, Object>> selCtgryListForOas(String apiSpcNo);

    /** paths[] 용 Operation 목록. methodNm은 MTHTYP1000 코드가 아니라 소문자 HTTP 동사. */
    List<Map<String, Object>> selDefListForOas(String apiSpcNo);

    /** 그룹 전체 파라미터(모든 API 분)를 한 번에 조회 - 호출부에서 apiNo로 그룹핑한다. */
    List<Map<String, Object>> selParamListForOas(String apiSpcNo);

    /** 생성된 2.0/3.0 YAML을 두 컬럼에 동시 반영 */
    int updSpcYamlBoth(@Param("apiSpcNo") String apiSpcNo,
                       @Param("yamlV2") String yamlV2,
                       @Param("yamlV3") String yamlV3);

    /** 일괄 재생성 배치 대상(SPCREG 명세 전체) */
    List<String> selSpcNoListForRegen();
}
