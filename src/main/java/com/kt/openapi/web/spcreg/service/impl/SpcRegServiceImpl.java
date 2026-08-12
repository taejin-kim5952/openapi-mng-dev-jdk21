package com.kt.openapi.web.spcreg.service.impl;

import com.kt.openapi.web.spcreg.dao.SpcRegDAO;
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
        return vo.getApiSpcNo();
    }
}
