package com.kt.openapi.web.api.vo;

import java.io.Serializable;

/**
 * API 민감정보 전수조사 VO
 * SENSITIVE_INFO_SURVEY 테이블 매핑
 */
public class SensitiveInfoSurveyVO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String seq;              // 시퀀스
    private String apiId;            // API ID
    private String sysNm;            // 시스템명
    private String sysId;            // 시스템 ID
    private String sensitivityLevel; // 민감도 등급
    private String parameterType;    // 파라미터 타입 (Request/Response)
    private String parameterName;    // 파라미터명
    private String createdBy;        // 등록자 ID
    private String createdAt;        // 등록 날짜
    private String parameterDesc;    // 파라미터 설명
    
    // Getters and Setters
    public String getSeq() {
        return seq;
    }
    
    public void setSeq(String seq) {
        this.seq = seq;
    }
    
    public String getApiId() {
        return apiId;
    }
    
    public void setApiId(String apiId) {
        this.apiId = apiId;
    }
    
    public String getSysNm() {
        return sysNm;
    }
    
    public void setSysNm(String sysNm) {
        this.sysNm = sysNm;
    }
    
    public String getSysId() {
        return sysId;
    }
    
    public void setSysId(String sysId) {
        this.sysId = sysId;
    }
    
    public String getSensitivityLevel() {
        return sensitivityLevel;
    }
    
    public void setSensitivityLevel(String sensitivityLevel) {
        this.sensitivityLevel = sensitivityLevel;
    }
    
    public String getParameterType() {
        return parameterType;
    }
    
    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }
    
    public String getParameterName() {
        return parameterName;
    }
    
    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getParameterDesc() {
        return parameterDesc;
    }
    
    public void setParameterDesc(String parameterDesc) {
        this.parameterDesc = parameterDesc;
    }
    
    @Override
    public String toString() {
        return "SensitiveInfoSurveyVO{" +
                "seq='" + seq + '\'' +
                ", apiId='" + apiId + '\'' +
                ", sysNm='" + sysNm + '\'' +
                ", sysId='" + sysId + '\'' +
                ", sensitivityLevel='" + sensitivityLevel + '\'' +
                ", parameterType='" + parameterType + '\'' +
                ", parameterName='" + parameterName + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", parameterDesc='" + parameterDesc + '\'' +
                '}';
    }
}
