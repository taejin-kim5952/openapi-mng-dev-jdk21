package com.kt.openapi.web.api.service.impl;

import com.kt.openapi.web.api.dao.ApiSensitiveInfoDAO;
import com.kt.openapi.web.api.service.ApiSensitiveInfoService;
import com.kt.openapi.web.api.vo.ApiListPopupVO;
import com.kt.openapi.web.api.vo.ApiSensitiveParamVO;
import com.kt.openapi.web.api.vo.SensitiveInfoSurveyVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * API 민감정보 서비스 구현체
 */
@Service("apiSensitiveInfoService")
public class ApiSensitiveInfoServiceImpl implements ApiSensitiveInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(ApiSensitiveInfoServiceImpl.class);

    @Autowired
    private ApiSensitiveInfoDAO sensitiveInfoDAO;
    
    @Override
    public List<ApiSensitiveParamVO> getParamList(String sysnm) {
        List<ApiSensitiveParamVO> paramList = sensitiveInfoDAO.getParamList();
        
        // 시스템명 필터링
        if (sysnm != null && !sysnm.trim().isEmpty()) {
            paramList.removeIf(param -> !sysnm.equals(param.getSystemNm()));
        }
        
        /*
        // 민감정보 등급 자동 매칭 (DB 에 값이 없으면만 실행)
        for (ApiSensitiveParamVO param : paramList) {
            // currentGrade 가 NULL 이거나 비어있으면 자동 매칭 실행
            if (param.getCurrentGrade() == null || param.getCurrentGrade().trim().isEmpty()) {
                String matchedGrade = matchSensitiveGrade(param.getParamNm(), param.getParamDesc());
                param.setCurrentGrade(matchedGrade);
            }
            // currentGrade 가 있으면 (DB 에서 조회된 값) 그대로 사용
        }*/
        
        return paramList;
    }
    
    @Override
    public void registerSensitiveInfo(SensitiveInfoSurveyVO vo) throws Exception {
        sensitiveInfoDAO.insertSensitiveInfoSurvey(vo);
    }
    
    @Override
    public List<ApiListPopupVO> getApiListForPopup(Map<String, Object> map) throws Exception {
        return sensitiveInfoDAO.getApiListForPopup(map);
    }
    
    @Override
    public int getApiListForPopupCount(Map<String, Object> map) throws Exception {
        return sensitiveInfoDAO.getApiListForPopupCount(map);
    }
    
    @Override
    public void updateGrade(String apiNo, String paramNm, String sensitivityLevel, String updatedBy) throws Exception {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("apiId", apiNo);
        paramMap.put("paramNm", paramNm);
        paramMap.put("sensitivityLevel", sensitivityLevel);
        paramMap.put("updatedBy", updatedBy);
        sensitiveInfoDAO.updateGrade(paramMap);
    }
    
    @Override
    public int batchUpdateGrades(List<Map<String, Object>> changes) throws Exception {
        if (changes == null || changes.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (Map<String, Object> change : changes) {
            try {
                // 무조건 INSERT (UPDATE 없이 새로운 레코드만 생성)
                sensitiveInfoDAO.insertGradeIfNotExists(change);
                count++;
            } catch (Exception e) {
                LOG.error("등급 INSERT 실패: apiId={}, paramNm={}",
                    change.get("apiId"), change.get("paramNm"), e);
                throw e;
            }
        }

        return count;
    }
    
    /**
     * 민감정보 등급 매칭
     * @param paramNm 파라미터명
     * @param paramDesc 파라미터 설명
     * @return 등급 (none, 1 등급, 2 등급)
     */
    private String matchSensitiveGrade(String paramNm, String paramDesc) {
        if (paramNm == null && paramDesc == null) {
            return "none";
        }
        
        String searchStr = (paramNm + " " + paramDesc).toLowerCase();
        
        // 1 등급: 단말정보 (IMSI, IMEI, ICCID, EID, Ki, Opc 등)
        String[] grade1Patterns = {
            "imsi", "imei", "iccid", "eid", "ki", "opc", "단말기식별", "기기식별"
        };
        
        for (String pattern : grade1Patterns) {
            if (searchStr.contains(pattern)) {
                return "1 등급";
            }
        }
        
        // 2 등급: 개인식별정보 (주민번호, 신용카드, 계좌번호, 이름, 주소, 전화번호 등)
        String[] grade2Patterns = {
            "주민", "신용카드", "계좌", "은행", "name", "이름", "address", "주소", 
            "phone", "전화", "email", "이메일", "birthday", "생년", "ssn", "id", "아이디"
        };
        
        for (String pattern : grade2Patterns) {
            if (searchStr.contains(pattern)) {
                return "2 등급";
            }
        }
        
        return "none";
    }
}
