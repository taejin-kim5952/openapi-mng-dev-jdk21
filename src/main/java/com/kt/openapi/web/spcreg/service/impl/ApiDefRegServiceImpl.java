package com.kt.openapi.web.spcreg.service.impl;

import com.kt.openapi.web.spcreg.dao.ApiDefRegDAO;
import com.kt.openapi.web.spcreg.service.ApiDefRegService;
import com.kt.openapi.web.spcreg.service.ApiSpcYamlSyncService;
import com.kt.openapi.web.spcreg.vo.ApiDefRegVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.spcreg.service.impl
 * 2. 타입명   : ApiDefRegServiceImpl.java
 * 5. 설명     : "API 등록"(기존 SPC에 API 추가) 화면 전용 서비스 구현체. SPC는 만들지 않고,
 *              화면에서 선택한 기존 apiSpcNo에 CTGRY(재사용 또는 최초 1회 생성)/DEF/PARAM만 INSERT한다.
 * </pre>
 */
@Service("apiDefRegService")
public class ApiDefRegServiceImpl implements ApiDefRegService {

    private static final Logger LOG = LoggerFactory.getLogger(ApiDefRegServiceImpl.class);

    @Autowired
    private ApiDefRegDAO apiDefRegDAO;

    /** DEF/PARAM 저장 후 KOA_TB_API_SPC의 YAML 파생 캐시(2.0/3.0)를 같은 트랜잭션에서 갱신한다. */
    @Autowired
    private ApiSpcYamlSyncService apiSpcYamlSyncService;

    @Override
    public List<Map<String, Object>> selDefListByApiSpcNo(String apiSpcNo) {
        return apiDefRegDAO.selDefListByApiSpcNo(apiSpcNo);
    }

    @Override
    public Map<String, Object> selSpcByNo(String apiSpcNo) {
        if (apiSpcNo == null || apiSpcNo.trim().isEmpty()) {
            return null;
        }
        return apiDefRegDAO.selSpcByNo(apiSpcNo);
    }

    @Override
    public Map<String, Object> selApiDefDetail(String apiNo) {
        if (apiNo == null || apiNo.trim().isEmpty()) {
            return null;
        }
        Map<String, Object> def = apiDefRegDAO.selApiDefDetail(apiNo);
        if (def == null) {
            return null;
        }
        Map<String, Object> result = new HashMap<>(def);
        result.put("paramList", apiDefRegDAO.selParamListByApiNo(apiNo));
        return result;
    }

    @Override
    public List<Map<String, Object>> selCtgryListBySpc(String apiSpcNo) {
        return apiDefRegDAO.selCtgryListBySpc(apiSpcNo);
    }

    /**
     * API그룹 추가. 같은 SPC 안에서 이름이 겹치면 만들지 않는다 - 좌측 트리에 같은 이름이 둘
     * 생기면 어느 쪽에 API를 넣었는지 구분할 수 없다.
     */
    @Override
    @Transactional(rollbackFor = { Exception.class })
    public Map<String, Object> savCtgry(ApiDefRegVO vo) {
        Map<String, Object> dup = new HashMap<>();
        dup.put("apiSpcNo", vo.getApiSpcNo());
        dup.put("ctgryNm", vo.getCtgryNm());
        if (apiDefRegDAO.selCtgryNmDupCnt(dup) > 0) {
            return null;
        }

        apiDefRegDAO.savApiCtgry(vo);

        Map<String, Object> created = new HashMap<>();
        created.put("apiCtgryNo", vo.getApiCtgryNo());
        created.put("ctgryNm", vo.getCtgryNm());
        created.put("ctgryDesc", vo.getCtgryDesc() == null ? "" : vo.getCtgryDesc());
        return created;
    }

    @Override
    public List<Map<String, Object>> selTmpltList() {
        return apiDefRegDAO.selTmpltList();
    }

    @Override
    public List<Map<String, Object>> selApiProviderList() {
        return apiDefRegDAO.selApiProviderList();
    }

    @Override
    public boolean selApiIdChk(String apiId, String apiNo, String apiVerNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("apiId", apiId);
        params.put("apiNo", apiNo);
        params.put("apiVerNo", apiVerNo);
        return apiDefRegDAO.selApiIdChk(params) > 0;
    }

    @Override
    public String selNextApiId() {
        return apiDefRegDAO.selNextApiId();
    }

    @Override
    public List<Map<String, Object>> selBstSysList(String target, String sysId, String sysNm) {
        Map<String, Object> params = new HashMap<>();
        params.put("target", target);
        params.put("sysId", sysId);
        params.put("sysNm", sysNm);
        return apiDefRegDAO.selBstSysList(params);
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public String savApiDefReg(ApiDefRegVO vo) {
        LOG.debug("####################### ApiDefRegServiceImpl savApiDefReg START ############################");
        LOG.debug(" 대상 apiSpcNo(기존 그룹) ========== {} ", vo.getApiSpcNo());

        // 그룹에 카테고리가 이미 있으면 재사용하고(그룹당 카테고리 1개 유지), 없으면(그 그룹의 첫 API라면) 새로 만든다.
        String existingCtgryNo = apiDefRegDAO.selDefaultCtgryBySpc(vo.getApiSpcNo());
        if (existingCtgryNo != null && !existingCtgryNo.trim().isEmpty()) {
            vo.setApiCtgryNo(existingCtgryNo);
            LOG.debug(" 기존 apiCtgryNo 재사용 ========== {} ", vo.getApiCtgryNo());
        } else {
            apiDefRegDAO.savApiCtgry(vo);
            LOG.debug(" 생성된 apiCtgryNo ========== {} ", vo.getApiCtgryNo());
        }

        // Path에 v1.0 같은 버전 세그먼트가 있으면 자동으로 뽑아서 API_VER에 저장(직접 입력 안 받음).
        vo.setApiVer(extractVersionFromPath(vo.getApiPath()));
        boolean isVersionUp = vo.getApiVerNo() != null && !vo.getApiVerNo().trim().isEmpty();

        apiDefRegDAO.savApiDef(vo);
        LOG.debug(" 생성된 apiNo ========== {} ", vo.getApiNo());

        // 버전업으로 만든 게 아니면(=화면이 원본 버전 패밀리 키를 안 보냈으면) 새 API가 자기 자신의
        // 버전 패밀리를 시작한다 - API_VER_NO를 자기 apiNo로 채운다.
        if (!isVersionUp) {
            apiDefRegDAO.updApiVerNoSelf(vo.getApiNo());
        }

        savParamTree(vo.getParamList(), vo.getApiNo(), vo.getRegr());

        // 정본(DEF/PARAM)이 바뀌었으므로 파생 캐시인 YAML 두 컬럼을 같은 트랜잭션에서 다시 만든다.
        apiSpcYamlSyncService.regenerate(vo.getApiSpcNo());

        return vo.getApiSpcNo();
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public String updApiDefReg(ApiDefRegVO vo) {
        LOG.debug("####################### ApiDefRegServiceImpl updApiDefReg START ############################");
        LOG.debug(" 대상 apiNo(기존 API) ========== {} ", vo.getApiNo());

        // 수정 시에도 Path가 바뀌었을 수 있으니 API_VER은 다시 추출한다. API_VER_NO(버전 패밀리)는
        // updApiDef가 아예 건드리지 않는다 - 수정으로는 패밀리가 안 바뀐다.
        vo.setApiVer(extractVersionFromPath(vo.getApiPath()));

        apiDefRegDAO.updApiDef(vo);
        apiDefRegDAO.delParamsByApiNo(vo.getApiNo());
        savParamTree(vo.getParamList(), vo.getApiNo(), vo.getAmdr());

        apiSpcYamlSyncService.regenerate(vo.getApiSpcNo());

        return vo.getApiSpcNo();
    }

    // 기존 등록 마법사 KsmUtil.fmt_data(path, "fmt_version_in_path")와 동일한 패턴 - Path의
    // 두 번째 세그먼트가 v1.0 같은 형태일 때만 그 값을 뽑아낸다(예: /kos/v1.0/ChildBidgRetv -> v1.0).
    private static final Pattern VERSION_IN_PATH = Pattern.compile("^(/[\\w\\-.]+)/(v\\d+\\.\\d+)(/[\\w\\-./]+)$");

    private String extractVersionFromPath(String path) {
        if (path == null) {
            return "";
        }
        Matcher m = VERSION_IN_PATH.matcher(path.trim());
        return m.find() ? m.group(2) : "";
    }

    /**
     * object/array 하위 필드를 포함한 파라미터 트리를 저장한다. 화면(JS)이 각 노드에 부여한
     * tempId/parentTempId로 부모-자식 관계를 표현해서 보내오면, 부모가 먼저 저장되어 실제
     * PARAM_NO를 받은 뒤에야 그 값을 자식의 PRNTS_PARAM_NO로 넘길 수 있으므로 위상순서로 저장한다.
     * paramTypeCd/paramLoc은 화면에서 스코프(입력/Query/출력)에 맞게 이미 채워서 보낸다.
     */
    private void savParamTree(List<ApiDefRegVO.ApiDefParamVO> list, String apiNo, String regr) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Map<String, String> tempIdToRealNo = new HashMap<>();
        Map<String, Integer> siblingSeq = new HashMap<>();
        List<ApiDefRegVO.ApiDefParamVO> pending = new ArrayList<>(list);
        int sortOdrg = 1;
        int guard = 0;
        while (!pending.isEmpty()) {
            if (guard++ > 2000) {
                throw new IllegalStateException("파라미터 트리 구조가 올바르지 않습니다(순환 참조 의심).");
            }
            boolean progressed = false;
            Iterator<ApiDefRegVO.ApiDefParamVO> it = pending.iterator();
            while (it.hasNext()) {
                ApiDefRegVO.ApiDefParamVO p = it.next();
                String parentTempId = p.getParentTempId();
                boolean isRoot = parentTempId == null || parentTempId.trim().isEmpty();
                if (!isRoot && !tempIdToRealNo.containsKey(parentTempId)) {
                    continue; // 부모가 아직 저장 안 됨 - 다음 라운드로
                }
                it.remove();
                progressed = true;
                if (p.getParamNm() == null || p.getParamNm().trim().isEmpty()) {
                    continue;
                }
                String prntsParamNo = isRoot ? "" : tempIdToRealNo.get(parentTempId);
                int siblingKeyBase = isRoot ? 0 : Integer.parseInt(prntsParamNo);
                String siblingKey = String.valueOf(siblingKeyBase);
                int odrg = siblingSeq.merge(siblingKey, 1, Integer::sum);

                Map<String, Object> m = new HashMap<>();
                m.put("apiNo", apiNo);
                m.put("paramTypeCd", p.getParamTypeCd());
                m.put("sortOdrg", sortOdrg++);
                m.put("paramNm", p.getParamNm());
                m.put("dataTypeCd", p.getDataTypeCd());
                m.put("paramLoc", p.getParamLoc());
                m.put("required", p.getRequired());
                m.put("paramDesc", p.getParamDesc());
                m.put("exam", p.getExam());
                m.put("personalData", p.getPersonalData());
                m.put("doNotSend", p.getDoNotSend());
                m.put("fixedValue", p.getFixedValue());
                m.put("hidden", p.getHidden());
                m.put("mappingKey", p.getMappingKey());
                m.put("bigo", p.getBigo());
                m.put("paramSandboxYn", p.getParamSandboxYn());
                m.put("hdpUrlDecode", p.getHdpUrlDecode());
                m.put("hdpUrlEncode", p.getHdpUrlEncode());
                m.put("hdpUploadTarget", p.getHdpUploadTarget());
                m.put("resCd", p.getResCd());
                m.put("resDesc", p.getResDesc());
                m.put("prntsParamNo", prntsParamNo);
                m.put("objNo", isRoot ? "" : prntsParamNo);
                m.put("objOdrg", String.valueOf(odrg));
                m.put("regr", regr);
                apiDefRegDAO.savApiParam(m);

                if (p.getTempId() != null && !p.getTempId().trim().isEmpty()) {
                    tempIdToRealNo.put(p.getTempId(), String.valueOf(m.get("paramNo")));
                }
            }
            if (!progressed) {
                throw new IllegalStateException("파라미터 트리 저장 순서를 결정할 수 없습니다(부모 tempId 불일치).");
            }
        }
    }
}
