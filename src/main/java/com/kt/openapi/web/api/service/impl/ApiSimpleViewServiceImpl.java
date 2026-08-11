package com.kt.openapi.web.api.service.impl;

import com.kt.openapi.web.api.dao.ApiSimpleViewDAO;
import com.kt.openapi.web.api.service.ApiSimpleViewService;
import com.kt.openapi.web.api.vo.ApiSimpleDefVO;
import com.kt.openapi.web.api.vo.ApiSimpleSpcVO;
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

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.api.service.impl
 * 2. 타입명   : ApiSimpleViewServiceImpl.java
 * 5. 설명     : "간단 상세" 화면 전용 서비스 구현체.
 * </pre>
 */
@Service("apiSimpleViewService")
public class ApiSimpleViewServiceImpl implements ApiSimpleViewService {

    private static final Logger LOG = LoggerFactory.getLogger(ApiSimpleViewServiceImpl.class);

    @Autowired
    private ApiSimpleViewDAO apiSimpleViewDAO;

    @Override
    public ApiSimpleSpcVO selSpcEssential(String apiSpcNo) {
        return apiSimpleViewDAO.selSpcEssential(apiSpcNo);
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public void savSpcEssential(ApiSimpleSpcVO vo) {
        apiSimpleViewDAO.updSpcEssential(vo);
    }

    @Override
    public List<ApiSimpleDefVO> selDefList(String apiSpcNo) {
        return apiSimpleViewDAO.selDefList(apiSpcNo);
    }

    @Override
    public ApiSimpleDefVO selDefDetail(String apiNo) {
        return apiSimpleViewDAO.selDefDetail(apiNo);
    }

    @Override
    public List<Map<String, Object>> selParamList(String apiNo) {
        return apiSimpleViewDAO.selParamList(apiNo);
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public void savDefDetail(ApiSimpleDefVO vo) {
        apiSimpleViewDAO.updDefDetail(vo);
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public void savDefParams(String apiNo, List<Map<String, Object>> paramList, String regr) {
        LOG.debug("####################### ApiSimpleViewServiceImpl savDefParams START ############################");
        apiSimpleViewDAO.delParamsByApiNo(apiNo);
        savParamTree(paramList, apiNo, regr);
    }

    /**
     * object/array 하위 필드를 포함한 파라미터 트리를 저장한다. 화면(JS)이 각 노드에 부여한
     * tempId/parentTempId로 부모-자식 관계를 표현해서 보내오면, 부모가 먼저 저장되어 실제
     * PARAM_NO를 받은 뒤에야 그 값을 자식의 PRNTS_PARAM_NO로 넘길 수 있으므로 위상순서로 저장한다.
     * paramTypeCd/paramLoc은 화면에서 스코프(입력/Query/출력)에 맞게 이미 채워서 보낸다.
     */
    private void savParamTree(List<Map<String, Object>> paramList, String apiNo, String regr) {
        if (paramList == null || paramList.isEmpty()) {
            return;
        }
        Map<String, String> tempIdToRealNo = new HashMap<>();
        Map<String, Integer> siblingSeq = new HashMap<>();
        List<Map<String, Object>> pending = new ArrayList<>(paramList);
        int sortOdrg = 1;
        int guard = 0;
        while (!pending.isEmpty()) {
            if (guard++ > 2000) {
                throw new IllegalStateException("파라미터 트리 구조가 올바르지 않습니다(순환 참조 의심).");
            }
            boolean progressed = false;
            Iterator<Map<String, Object>> it = pending.iterator();
            while (it.hasNext()) {
                Map<String, Object> p = it.next();
                String parentTempId = toStr(p.get("parentTempId"));
                boolean isRoot = parentTempId.isEmpty();
                if (!isRoot && !tempIdToRealNo.containsKey(parentTempId)) {
                    continue; // 부모가 아직 저장 안 됨 - 다음 라운드로
                }
                it.remove();
                progressed = true;
                String nm = toStr(p.get("paramNm"));
                if (nm.isEmpty()) {
                    continue;
                }
                String prntsParamNo = isRoot ? "" : tempIdToRealNo.get(parentTempId);
                int siblingKeyBase = isRoot ? 0 : Integer.parseInt(prntsParamNo);
                String siblingKey = String.valueOf(siblingKeyBase);
                int odrg = siblingSeq.merge(siblingKey, 1, Integer::sum);

                String paramTypeCd = toStr(p.get("paramTypeCd"));
                p.put("apiNo", apiNo);
                p.put("paramTypeCd", paramTypeCd.isEmpty() ? "PRMTYP1010" : paramTypeCd);
                p.put("sortOdrg", sortOdrg++);
                p.put("regr", regr);
                if (toStr(p.get("paramLoc")).isEmpty()) {
                    p.put("paramLoc", "body");
                }
                p.put("prntsParamNo", prntsParamNo);
                p.put("objNo", isRoot ? "" : prntsParamNo);
                p.put("objOdrg", String.valueOf(odrg));
                apiSimpleViewDAO.savParam(p);

                String tempId = toStr(p.get("tempId"));
                if (!tempId.isEmpty()) {
                    tempIdToRealNo.put(tempId, toStr(p.get("paramNo")));
                }
            }
            if (!progressed) {
                throw new IllegalStateException("파라미터 트리 저장 순서를 결정할 수 없습니다(부모 tempId 불일치).");
            }
        }
    }

    private static String toStr(Object o) {
        return o == null ? "" : o.toString().trim();
    }
}
