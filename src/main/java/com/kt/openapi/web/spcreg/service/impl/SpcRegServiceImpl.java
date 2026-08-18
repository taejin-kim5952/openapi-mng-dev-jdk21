package com.kt.openapi.web.spcreg.service.impl;

import com.kt.openapi.web.spcreg.dao.SpcRegDAO;
import com.kt.openapi.web.spcreg.service.ApiSpcYamlSyncService;
import com.kt.openapi.web.spcreg.service.SpcRegService;
import com.kt.openapi.web.spcreg.vo.SpcRegVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.spcreg.service.impl
 * 2. 타입명   : SpcRegServiceImpl.java
 * 5. 설명     : "SPC 등록" 화면 전용 서비스 구현체. KOA_TB_API_SPC 1건만 만든다 — API(Method+Path)
 *              등록은 quickApiReg가 이 화면이 만든 apiSpcNo를 받아 별도로 처리한다.
 * </pre>
 */
@Service("spcRegService")
public class SpcRegServiceImpl implements SpcRegService {

    private static final Logger LOG = LoggerFactory.getLogger(SpcRegServiceImpl.class);

    @Autowired
    private SpcRegDAO spcRegDAO;

    @Autowired
    private ApiSpcYamlSyncService apiSpcYamlSyncService;

    @Override
    public List<Map<String, Object>> selSysSpcTree(String sysId) {
        return spcRegDAO.selSysSpcTree(sysId);
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public String savSpcReg(SpcRegVO vo) {
        LOG.debug("####################### SpcRegServiceImpl savSpcReg START ############################");
        spcRegDAO.savApiSpc(vo);
        LOG.debug(" 생성된 apiSpcNo ========== {} ", vo.getApiSpcNo());
        // 이 시점엔 아직 API(DEF)가 없어 재생성해도 paths가 빈 문서가 된다. "YAML 등록"으로 붙여넣은
        // 원문(YAML_SBST)을 지우게 되므로 여기서는 호출하지 않는다 - 뒤이어 등록되는 첫 API의
        // savApiDefReg()가 정본 기준으로 두 컬럼을 만들어 준다.
        return vo.getApiSpcNo();
    }

    @Override
    public Map<String, Object> selSpcDetail(String apiSpcNo) {
        return spcRegDAO.selSpcDetail(apiSpcNo);
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public String updSpcReg(SpcRegVO vo) {
        LOG.debug("####################### SpcRegServiceImpl updSpcReg START ############################");
        spcRegDAO.updApiSpc(vo);
        // 그룹명/host/basPath/버전이 바뀌면 YAML의 info/servers도 따라가야 한다.
        apiSpcYamlSyncService.regenerate(vo.getApiSpcNo());
        return vo.getApiSpcNo();
    }
}
