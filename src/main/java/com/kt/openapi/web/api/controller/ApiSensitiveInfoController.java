package com.kt.openapi.web.api.controller;

import com.kt.openapi.web.api.service.ApiSensitiveInfoService;
import com.kt.openapi.web.api.vo.ApiListPopupVO;
import com.kt.openapi.web.api.vo.ApiSensitiveParamVO;
import com.kt.openapi.web.api.vo.SensitiveInfoSurveyVO;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * API 민감정보 전수조사 컨트롤러
 */

@Controller
@RequestMapping("/api/sensitiveInfo")
public class ApiSensitiveInfoController {

	private static final Logger LOG = LoggerFactory.getLogger(ApiSensitiveInfoController.class);

	@Autowired
	private ApiSensitiveInfoService sensitiveInfoService;

    /**
     * API 민감정보 전수조사 리포트 페이지
     * @param sysnm 시스템명 (URL 파라미터)
     * @param model Model
     * @return JSP 페이지
     */
    @GetMapping("/report.do")
    public String sensitiveInfoReport(
            @RequestParam(value = "sysnm", required = false) String sysnm,
            HttpServletRequest request,
            HttpSession session,
            Model model) {

        // [JSP -> Thymeleaf 마이그레이션] 원본 JSP의 <c:choose>/<c:redirect> 로그인 체크를 컨트롤러 단으로 이관
        String loginRedirect = redirectToLoginIfNotAuthenticated(request, session, "/api/sensitiveInfo/report.do");
        if (loginRedirect != null) {
            return loginRedirect;
        }

        // 파라미터 목록 조회
        List<ApiSensitiveParamVO> paramList = sensitiveInfoService.getParamList(sysnm);
        
        // 통계 계산
        int totalParams = paramList.size();
        int grade1Count = 0;
        int grade2Count = 0;
        
        for (ApiSensitiveParamVO param : paramList) {
            if ("1 등급".equals(param.getCurrentGrade())) {
                grade1Count++;
            } else if ("2 등급".equals(param.getCurrentGrade())) {
                grade2Count++;
            }
        }
        
        model.addAttribute("paramList", paramList);
        LOG.debug("paramList: {}", paramList);
        model.addAttribute("totalParams", totalParams);
        model.addAttribute("grade1Count", grade1Count);
        model.addAttribute("grade2Count", grade2Count);
        
        return "api/sensitiveInfoReport";
    }
    
    /**
     * API 민감정보 등록 페이지
     * @param model Model
     * @return JSP 페이지
     */
    @GetMapping("/register.do")
    public String sensitiveInfoRegister(HttpServletRequest request, HttpSession session, Model model) {
        String loginRedirect = redirectToLoginIfNotAuthenticated(request, session, "/api/sensitiveInfo/register.do");
        if (loginRedirect != null) {
            return loginRedirect;
        }

        // 등록 폼을 위한 초기 데이터 (필요시 수정)
        model.addAttribute("mode", "create");
        UserJoinVO ssUserVo = (UserJoinVO) session.getAttribute("ssUserVo");
        model.addAttribute("userId", ssUserVo.getMbrId());

        return "api/sensitiveInfoRegister";
    }

    /**
     * [JSP -> Thymeleaf 마이그레이션] 원본 JSP의 <c:choose>/<c:redirect> 로그인 체크를 컨트롤러 단으로 이관한 공통 헬퍼.
     * Thymeleaf는 템플릿 렌더링 중간에 리다이렉트를 할 수 없으므로, 뷰 진입 전에 세션을 확인한다.
     * 동작은 원본과 동일: 미로그인 시 현재 URL(쿼리스트링 포함)을 returnUrl로 붙여 로그인 페이지로 리다이렉트.
     *
     * @return 리다이렉트 대상 뷰 이름(미로그인 시), 로그인된 경우 null
     */
    private String redirectToLoginIfNotAuthenticated(HttpServletRequest request, HttpSession session, String defaultReturnUrl) {
        if (session.getAttribute("ssUserVo") != null) {
            return null;
        }
        String returnUrl = request.getRequestURI().substring(request.getContextPath().length());
        if (returnUrl.isEmpty() || returnUrl.equals("/")) {
            returnUrl = defaultReturnUrl;
        }
        if (request.getQueryString() != null && !request.getQueryString().isEmpty()) {
            returnUrl = returnUrl + "?" + request.getQueryString();
        }
        String encodedReturnUrl = UriUtils.encode(returnUrl, StandardCharsets.UTF_8);
        // "redirect:"로 시작하는 컨텍스트 상대경로는 Spring이 컨텍스트 패스를 자동으로 붙여주므로 여기서 직접 붙이지 않는다
        // (직접 붙이면 "/apidev/apidev/login/..."처럼 두 번 붙는 버그가 생김 - 실제로 발생 확인 후 수정).
        return "redirect:/login/loginForm.do?returnUrl=" + encodedReturnUrl;
    }

    /**
     * API 민감정보 등록 처리 (단일)
     * @param vo 등록할 민감정보 정보
     * @param session 세션
     * @param redirectAttributes 리디렉트 속성
     * @return 리디렉트 URL
     * @throws Exception
     */
    @PostMapping("/registerSubmit.do")
    public String registerSensitiveInfo(
            SensitiveInfoSurveyVO vo,
            HttpSession session,
            RedirectAttributes redirectAttributes) throws Exception {
        
        try {
            // 로그인한 사용자 ID 설정
            UserJoinVO ssUserVo = (UserJoinVO) session.getAttribute("ssUserVo");
            if (ssUserVo != null) {
                vo.setCreatedBy(ssUserVo.getMbrId());
            } else {
                vo.setCreatedBy("UNKNOWN");
            }
            
            // 등급 매핑 (폼에서 받은 값을 테이블 컬럼명에 맞게 변환)
            String grade = vo.getSensitivityLevel();
            if ("1 등급".equals(grade)) {
                vo.setSensitivityLevel("1");
            } else if ("2 등급".equals(grade)) {
                vo.setSensitivityLevel("2");
            } else {
                vo.setSensitivityLevel("");
            }
            
            // DB 에 저장
            sensitiveInfoService.registerSensitiveInfo(vo);
            
            LOG.info("민감정보 등록 완료: apiId={}, paramName={}, grade={}", 
                    vo.getApiId(), vo.getParameterName(), vo.getSensitivityLevel());
            
            redirectAttributes.addFlashAttribute("message", "등록이 완료되었습니다.");
            return "redirect:/api/sensitiveInfo/report.do";
            
        } catch (Exception e) {
            LOG.error("민감정보 등록 실패", e);
            redirectAttributes.addFlashAttribute("message", "등록에 실패했습니다: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", true);
            return "redirect:/api/sensitiveInfo/register.do";
        }
    }
    
    /**
     * API 민감정보 배치 등록 처리 (여러 개 동시 등록)
     * 입력된 폼 개수만큼 각각 INSERT 수행
     * @param requestData 등록 데이터 목록
     * @param session 세션
     * @return JSON 응답 (성공 개수, 실패 개수)
     * @throws Exception
     */
    @PostMapping("/registerBatch.do")
    public Map<String, Object> registerBatchSensitiveInfo(
            @RequestBody Map<String, Object> requestData,
            HttpSession session) throws Exception {
        
        // dataList 추출 (List<Map<String, Object>>)
        List<Map<String, Object>> dataListRaw = (List<Map<String, Object>>) requestData.get("dataList");
        Map<String, Object> response = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        
        // 로그인한 사용자 ID 설정
        UserJoinVO ssUserVo = (UserJoinVO) session.getAttribute("ssUserVo");
        String createdBy = "UNKNOWN";
        if (ssUserVo != null) {
            createdBy = ssUserVo.getMbrId();
        }
        
        // 입력된 폼 개수만큼 루프 처리하여 각각 INSERT
        for (Map<String, Object> data : dataListRaw) {
            try {
                // VO 직접 생성 (각 폼마다 새로운 객체)
                SensitiveInfoSurveyVO vo = new SensitiveInfoSurveyVO();
                
                // 데이터 매핑 (각 폼의 값을 개별적으로 설정)
                vo.setSysId(getStringValue(data, "sysId"));
                vo.setApiId(getStringValue(data, "apiId"));
                vo.setSysNm(getStringValue(data, "sysNm"));
                vo.setParameterType(getStringValue(data, "parameterType"));
                vo.setParameterName(getStringValue(data, "parameterName"));
                vo.setParameterDesc(getStringValue(data, "parameterDesc"));
                vo.setCreatedBy(createdBy);
                
                // 등급 매핑 (각 폼의 값을 개별적으로 처리)
                String grade = getStringValue(data, "sensitivityLevel");
                if ("1 등급".equals(grade)) {
                    vo.setSensitivityLevel("1");
                } else if ("2 등급".equals(grade)) {
                    vo.setSensitivityLevel("2");
                } else {
                    vo.setSensitivityLevel("");
                }
                
                // DB 에 저장 (각각 INSERT)
                sensitiveInfoService.registerSensitiveInfo(vo);
                successCount++;
                
                LOG.info("민감정보 등록 완료: apiId={}, paramName={}, grade={}", 
                        vo.getApiId(), vo.getParameterName(), vo.getSensitivityLevel());
            } catch (Exception e) {
                LOG.error("민감정보 등록 실패: error={}", e.getMessage());
                failCount++;
            }
        }
        
        response.put("success", true);
        response.put("successCount", successCount);
        response.put("failCount", failCount);
        response.put("message", successCount + "개 등록 완료, " + failCount + "개 실패");
        
        return response;
    }
    
    /**
     * API 목록 조회 (팝업용)
     * @param map 검색 조건 (page, pageSize, searchTerm)
     * @return API 목록 및 페이지 정보
     */
    @ResponseBody
    @PostMapping("/apiListPopup.do")
    public Map<String, Object> getApiListForPopup(@RequestBody Map<String, Object> map) throws Exception {
        int page = map.containsKey("page") ? Integer.parseInt(map.get("page").toString()) : 1;
        int pageSize = map.containsKey("pageSize") ? Integer.parseInt(map.get("pageSize").toString()) : 10;
        String searchTerm = map.containsKey("searchTerm") ? map.get("searchTerm").toString() : "";
        
        // offset 계산 (1 페이지 = 0)
        int offset = (page - 1) * pageSize;
        
        // 검색 조건 추가
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("offset", offset);
        paramMap.put("pageSize", pageSize);
        paramMap.put("searchTerm", searchTerm);
        
        // API 목록 조회
        List<ApiListPopupVO> apiList = sensitiveInfoService.getApiListForPopup(paramMap);
        
        // 총 개수 조회
        int totalCount = sensitiveInfoService.getApiListForPopupCount(paramMap);
        
        // 페이지 정보 계산
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        if (totalPages == 0) totalPages = 1;
        
        Map<String, Object> response = new HashMap<>();
        response.put("apiList", apiList);
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("totalCount", totalCount);
        response.put("totalPages", totalPages);
        
        return response;
    }
    
    /**
     * 변경된 등급 일괄 업데이트 (배치 INSERT)
     * @param requestData 변경 데이터 목록
     * @param session 세션
     * @return JSON 응답 (성공 개수, 실패 개수)
     */
    @ResponseBody
    @PostMapping("/updateGrades.do")
    public Map<String, Object> updateGrades(@RequestBody Map<String, Object> requestData, HttpSession session) throws Exception {
        List<Map<String, Object>> changesRaw = (List<Map<String, Object>>) requestData.get("changes");
        Map<String, Object> response = new HashMap<>();
        
        // 로그인한 사용자 ID 설정
        UserJoinVO ssUserVo = (UserJoinVO) session.getAttribute("ssUserVo");
        String updatedBy = "UNKNOWN";
        if (ssUserVo != null) {
            updatedBy = ssUserVo.getMbrId();
        }
        
        // 변경된 항목을 VO 목록으로 변환
        List<Map<String, Object>> changes = new ArrayList<>();
        for (Map<String, Object> change : changesRaw) {
            Map<String, Object> item = new HashMap<>();
            item.put("apiId", getStringValue(change, "apiId"));
            
            String sysId = getStringValue(change, "systemId");
            if (sysId == null || sysId.trim().isEmpty()) {
                sysId = "UNKNOWN";
            }
            item.put("sysId", sysId);
            
            item.put("sysNm", getStringValue(change, "systemNm"));
            item.put("paramNm", getStringValue(change, "paramNm"));
            item.put("parameterType", getStringValue(change, "parameterType"));  // 추가
            
            // 등급 매핑
            String newGrade = getStringValue(change, "newGrade");
            String sensitivityLevel = "";
            if ("1 등급".equals(newGrade)) {
                sensitivityLevel = "1";
            } else if ("2 등급".equals(newGrade)) {
                sensitivityLevel = "2";
            }
            item.put("sensitivityLevel", sensitivityLevel);
            item.put("updatedBy", updatedBy);
            
            changes.add(item);
        }
        
        // 배치 INSERT 수행
        int successCount = 0;
        int failCount = 0;
        
        try {
            successCount = sensitiveInfoService.batchUpdateGrades(changes);
        } catch (Exception e) {
            LOG.error("등급 일괄 업데이트 실패: error={}", e.getMessage());
            failCount = changes.size();
        }
        
        response.put("success", true);
        response.put("successCount", successCount);
        response.put("failCount", failCount);
        response.put("message", successCount + "개의 등급이 성공적으로 저장되었습니다.");
        
        return response;
    }
    
    /**
     * Map 에서 String 값 추출 (null 처리)
     */
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }
}
