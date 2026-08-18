package com.kt.openapi.web.tmplt.service.impl;

import com.kt.openapi.web.tmplt.dao.ApiTmpltDAO;
import com.kt.openapi.web.tmplt.service.ApiTmpltService;
import com.kt.openapi.web.tmplt.vo.ApiTmpltVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.tmplt.service.impl
 * 2. 타입명   : ApiTmpltServiceImpl.java
 * 5. 설명     : API 등록 템플릿 관리 서비스 구현체.
 * </pre>
 */
@Service("apiTmpltService")
public class ApiTmpltServiceImpl implements ApiTmpltService {

    @Autowired
    private ApiTmpltDAO apiTmpltDAO;

    @Override
    public List<ApiTmpltVO> selTmpltMngList() {
        return apiTmpltDAO.selTmpltMngList();
    }

    @Override
    public ApiTmpltVO selTmpltDetail(String tmpltNo) {
        return apiTmpltDAO.selTmpltDetail(tmpltNo);
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public String savTmplt(ApiTmpltVO vo) {
        if (vo.getTmpltNo() != null && !vo.getTmpltNo().trim().isEmpty()) {
            apiTmpltDAO.updTmplt(vo);
            return vo.getTmpltNo();
        }
        apiTmpltDAO.savTmplt(vo);
        return vo.getTmpltNo();
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public void delTmplt(String tmpltNo) {
        apiTmpltDAO.delTmplt(tmpltNo);
    }
}
