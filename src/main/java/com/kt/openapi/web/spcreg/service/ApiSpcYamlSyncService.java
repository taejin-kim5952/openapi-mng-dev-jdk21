package com.kt.openapi.web.spcreg.service;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.spcreg.service
 * 2. 타입명   : ApiSpcYamlSyncService.java
 * 5. 설명     : KOA_TB_API_SPC의 YAML 파생 캐시(YAML_SBST_V3 / YAML_SBST) 갱신 서비스.
 * </pre>
 */
public interface ApiSpcYamlSyncService {

    /**
     * 한 명세(apiSpcNo)의 OAS 3.0/2.0 YAML을 정규화 테이블에서 재생성해 두 컬럼에 반영한다.
     *
     * 호출자의 트랜잭션 안에서만 동작한다(Propagation.MANDATORY) - 저장 로직이 롤백되면 YAML도
     * 함께 롤백되어야 정본과 캐시가 어긋나지 않는다.
     *
     * @return 실제로 갱신했으면 true. 재생성 대상이 아니면(SPC_SRC_CD != 'SPCREG') false.
     */
    boolean regenerate(String apiSpcNo);

    /**
     * SPCREG 명세 전체를 일괄 재생성한다(도입 시점의 백필, 매핑 규칙 변경 후 재생성용).
     * 개별 명세 실패는 건너뛰고 계속 진행한다.
     *
     * @return 성공 건수
     */
    int regenerateAllSpcreg();
}
