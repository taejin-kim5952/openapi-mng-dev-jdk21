package com.kt.openapi.web.api.service.impl;

import com.kt.openapi.web.api.dao.ApiQuickRegDAO;
import com.kt.openapi.web.api.service.ApiQuickRegService;
import com.kt.openapi.web.api.vo.ApiQuickRegVO;
import com.kt.openapi.web.api.vo.ApiQuickTmpltVO;
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
 * 2. 타입명   : ApiQuickRegServiceImpl.java
 * 5. 설명     : "빠른 API 등록" 화면 전용 서비스 구현체. 기존 ApiRegServiceImpl의 YAML 파싱 경로를
 *              타지 않고, 폼 필드를 바로 KOA_TB_API_SPC/CTGRY/DEF/PARAM에 INSERT한다.
 * </pre>
 */
@Service("apiQuickRegService")
public class ApiQuickRegServiceImpl implements ApiQuickRegService {

    private static final Logger LOG = LoggerFactory.getLogger(ApiQuickRegServiceImpl.class);

    @Autowired
    private ApiQuickRegDAO apiQuickRegDAO;

    @Override
    public List<Map<String, Object>> selSysApiTree(String sysId) {
        return apiQuickRegDAO.selSysApiTree(sysId);
    }

    @Override
    public List<ApiQuickTmpltVO> selTmpltList() {
        return apiQuickRegDAO.selTmpltList();
    }

    @Override
    public List<ApiQuickTmpltVO> selTmpltMngList() {
        return apiQuickRegDAO.selTmpltMngList();
    }

    @Override
    public ApiQuickTmpltVO selTmpltDetail(String tmpltNo) {
        return apiQuickRegDAO.selTmpltDetail(tmpltNo);
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public String savTmplt(ApiQuickTmpltVO vo) {
        if (vo.getTmpltNo() != null && !vo.getTmpltNo().trim().isEmpty()) {
            apiQuickRegDAO.updTmplt(vo);
            return vo.getTmpltNo();
        }
        apiQuickRegDAO.savTmplt(vo);
        return vo.getTmpltNo();
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public void delTmplt(String tmpltNo) {
        apiQuickRegDAO.delTmplt(tmpltNo);
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public String savApiQuickReg(ApiQuickRegVO vo) {
        LOG.debug("####################### ApiQuickRegServiceImpl savApiQuickReg START ############################");

        apiQuickRegDAO.savApiSpc(vo);
        LOG.debug(" 생성된 apiSpcNo ========== {} ", vo.getApiSpcNo());

        apiQuickRegDAO.savApiCtgry(vo);
        LOG.debug(" 생성된 apiCtgryNo ========== {} ", vo.getApiCtgryNo());

        apiQuickRegDAO.savApiDef(vo);
        LOG.debug(" 생성된 apiNo ========== {} ", vo.getApiNo());

        savParamTree(vo.getParamList(), vo.getApiNo(), vo.getRegr());

        return vo.getApiSpcNo();
    }

    /**
     * object/array 하위 필드를 포함한 파라미터 트리를 저장한다. 화면(JS)이 각 노드에 부여한
     * tempId/parentTempId로 부모-자식 관계를 표현해서 보내오면, 부모가 먼저 저장되어 실제
     * PARAM_NO를 받은 뒤에야 그 값을 자식의 PRNTS_PARAM_NO로 넘길 수 있으므로 위상순서로 저장한다.
     * paramTypeCd/paramLoc은 화면에서 스코프(입력/Query/출력)에 맞게 이미 채워서 보낸다.
     */
    private void savParamTree(List<ApiQuickRegVO.ApiQuickParamVO> list, String apiNo, String regr) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Map<String, String> tempIdToRealNo = new HashMap<>();
        Map<String, Integer> siblingSeq = new HashMap<>();
        List<ApiQuickRegVO.ApiQuickParamVO> pending = new ArrayList<>(list);
        int sortOdrg = 1;
        int guard = 0;
        while (!pending.isEmpty()) {
            if (guard++ > 2000) {
                throw new IllegalStateException("파라미터 트리 구조가 올바르지 않습니다(순환 참조 의심).");
            }
            boolean progressed = false;
            Iterator<ApiQuickRegVO.ApiQuickParamVO> it = pending.iterator();
            while (it.hasNext()) {
                ApiQuickRegVO.ApiQuickParamVO p = it.next();
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
                m.put("prntsParamNo", prntsParamNo);
                m.put("objNo", isRoot ? "" : prntsParamNo);
                m.put("objOdrg", String.valueOf(odrg));
                m.put("regr", regr);
                apiQuickRegDAO.savApiParam(m);

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
