package com.kt.openapi.web.spcreg.service.impl;

import com.kt.openapi.web.spcreg.dao.OasYamlDAO;
import com.kt.openapi.web.spcreg.service.ApiSpcYamlSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.spcreg.service.impl
 * 2. 타입명   : ApiSpcYamlSyncServiceImpl.java
 * 5. 설명     : KOA_TB_API_DEF/PARAM(정본)에서 OAS 3.0/2.0 YAML을 재생성해 KOA_TB_API_SPC의
 *              YAML_SBST_V3 / YAML_SBST 두 컬럼에 한 번에 반영한다.
 *
 *              두 컬럼을 항상 같은 호출에서 함께 갱신하는 것이 이 클래스의 존재 이유다. 따로
 *              갱신되는 경로가 하나라도 생기면 2.0/3.0 문서가 어긋나기 시작한다.
 *
 *              배경: 신규 등록 화면(spcreg)은 정규화 테이블에만 쓰고 YAML을 만들지 않아서, YAML_SBST
 *              하나만 보고 화면을 그리는 구버전 등록화면(api/infoRegForm)에서 빈 폼으로 떴다.
 *              docs/04_OAS_GENERATOR_MAPPING.md 참조.
 * </pre>
 */
@Service("apiSpcYamlSyncService")
public class ApiSpcYamlSyncServiceImpl implements ApiSpcYamlSyncService {

    private static final Logger LOG = LoggerFactory.getLogger(ApiSpcYamlSyncServiceImpl.class);

    /** 재생성 대상 - 신규 등록 화면으로 만든 명세만. LEGACY/QUICK은 기존 YAML_SBST를 그대로 둔다. */
    private static final String SRC_SPCREG = "SPCREG";

    @Autowired
    private OasYamlDAO oasYamlDAO;

    @Autowired
    private OasYamlGenerator generator;

    @Override
    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = { Exception.class })
    public boolean regenerate(String apiSpcNo) {
        if (apiSpcNo == null || apiSpcNo.trim().isEmpty()) {
            return false;
        }

        Map<String, Object> spc = oasYamlDAO.selSpcForOas(apiSpcNo);
        if (spc == null) {
            LOG.debug(" YAML 재생성 건너뜀 - 명세 없음 apiSpcNo={} ", apiSpcNo);
            return false;
        }
        String srcCd = String.valueOf(spc.get("spcSrcCd"));
        if (!SRC_SPCREG.equals(srcCd)) {
            // 구버전/빠른등록으로 만든 명세는 자기 화면이 YAML_SBST를 직접 관리한다. 여기서 덮어쓰면
            // 그 화면의 저장 결과를 날려버린다.
            LOG.debug(" YAML 재생성 건너뜀 - 대상 아님 apiSpcNo={}, spcSrcCd={} ", apiSpcNo, srcCd);
            return false;
        }

        List<Map<String, Object>> defList = oasYamlDAO.selDefListForOas(apiSpcNo);
        if (defList == null || defList.isEmpty()) {
            // API가 하나도 없으면 paths가 빈 문서가 나온다. 그런 문서는 쓸모가 없는 데다,
            // "YAML 등록"으로 그룹만 먼저 만든 직후(API는 뒤이어 등록됨) 이 시점에 덮어쓰면
            // 사용자가 붙여넣은 원문을 날려버린다. 첫 API가 등록될 때 어차피 다시 만들어진다.
            LOG.debug(" YAML 재생성 건너뜀 - 등록된 API 없음 apiSpcNo={} ", apiSpcNo);
            return false;
        }

        List<Map<String, Object>> ctgryList = oasYamlDAO.selCtgryListForOas(apiSpcNo);
        List<Map<String, Object>> paramRows = oasYamlDAO.selParamListForOas(apiSpcNo);

        Map<String, Object> oas3 = generator.buildOas3(spc, ctgryList, defList, groupByApiNo(paramRows));
        Map<String, Object> oas2 = generator.downgradeToSwagger2(oas3);

        int updated = oasYamlDAO.updSpcYamlBoth(apiSpcNo, generator.toYaml(oas2), generator.toYaml(oas3));
        LOG.debug(" YAML 재생성 완료 apiSpcNo={}, api={}건, updated={} ", apiSpcNo, defList.size(), updated);
        return updated > 0;
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public int regenerateAllSpcreg() {
        List<String> targets = oasYamlDAO.selSpcNoListForRegen();
        int ok = 0;
        for (String apiSpcNo : targets) {
            try {
                if (regenerate(apiSpcNo)) {
                    ok++;
                }
            } catch (Exception e) {
                // 한 건이 깨져도 나머지는 계속 채운다. 개별 실패는 로그로 남겨 수동 확인한다.
                LOG.error("YAML 일괄 재생성 실패 apiSpcNo={}", apiSpcNo, e);
            }
        }
        LOG.info("YAML 일괄 재생성 종료 - 대상 {}건 중 {}건 성공", targets.size(), ok);
        return ok;
    }

    /** 그룹 전체 파라미터를 apiNo 단위로 나눈다(조회는 1회, 그룹핑은 메모리에서). */
    private Map<String, List<Map<String, Object>>> groupByApiNo(List<Map<String, Object>> rows) {
        Map<String, List<Map<String, Object>>> byApiNo = new LinkedHashMap<>();
        if (rows == null) {
            return byApiNo;
        }
        for (Map<String, Object> r : rows) {
            String apiNo = String.valueOf(r.get("apiNo"));
            byApiNo.computeIfAbsent(apiNo, k -> new ArrayList<>()).add(r);
        }
        return byApiNo;
    }
}
