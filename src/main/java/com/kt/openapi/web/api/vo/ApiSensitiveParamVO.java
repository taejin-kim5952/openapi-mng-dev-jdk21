package com.kt.openapi.web.api.vo;

import java.io.Serializable;

/**
 * API 민감정보 파라미터 VO
 */
public class ApiSensitiveParamVO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String apiNo;        // API 번호
    private String apiId;        // API ID (OIF_ 또는 IF_ 로 시작)
    private String apiNm;        // API 명
    private String systemId;     // 시스템 ID
    private String systemNm;     // 시스템 명
    private String paramNo;      // 파라미터 번호
    private String paramNm;      // 파라미터 명
    private String paramType;    // 파라미터 타입
    private String paramDesc;    // 파라미터 설명
    private String currentGrade; // 현재 등급 (none, 1 등급, 2 등급)
    private String category;     // 분류 코드
    private String categoryKo;   // 분류 명
    
    // Getters and Setters
    public String getApiNo() {
        return apiNo;
    }
    
    public void setApiNo(String apiNo) {
        this.apiNo = apiNo;
    }
    
    public String getApiId() {
        return apiId;
    }
    
    public void setApiId(String apiId) {
        this.apiId = apiId;
    }
    
    public String getApiNm() {
        return apiNm;
    }
    
    public void setApiNm(String apiNm) {
        this.apiNm = apiNm;
    }
    
    public String getSystemId() {
        return systemId;
    }
    
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
    
    public String getSystemNm() {
        return systemNm;
    }
    
    public void setSystemNm(String systemNm) {
        this.systemNm = systemNm;
    }
    
    public String getParamNo() {
        return paramNo;
    }
    
    public void setParamNo(String paramNo) {
        this.paramNo = paramNo;
    }
    
    public String getParamNm() {
        return paramNm;
    }
    
    public void setParamNm(String paramNm) {
        this.paramNm = paramNm;
    }
    
    public String getParamType() {
        return paramType;
    }
    
    public void setParamType(String paramType) {
        this.paramType = paramType;
    }
    
    public String getParamDesc() {
        return paramDesc;
    }
    
    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }
    
    public String getCurrentGrade() {
        return currentGrade;
    }
    
    public void setCurrentGrade(String currentGrade) {
        this.currentGrade = currentGrade;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getCategoryKo() {
        return categoryKo;
    }
    
    public void setCategoryKo(String categoryKo) {
        this.categoryKo = categoryKo;
    }
    
    @Override
    public String toString() {
        return "ApiSensitiveParamVO{" +
                "apiNo='" + apiNo + '\'' +
                ", apiId='" + apiId + '\'' +
                ", apiNm='" + apiNm + '\'' +
                ", systemNm='" + systemNm + '\'' +
                ", paramNm='" + paramNm + '\'' +
                ", currentGrade='" + currentGrade + '\'' +
                '}';
    }
}
