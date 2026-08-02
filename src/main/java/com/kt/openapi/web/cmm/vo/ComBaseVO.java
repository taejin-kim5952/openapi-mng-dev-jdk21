package com.kt.openapi.web.cmm.vo;

import com.kt.openapi.fwk.online.page.Pagination;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 모든 VO의 공통 기반 클래스
 * 페이징 정보 및 등록/수정 공통 필드 포함
 */
@Getter
@Setter
@ToString
public class ComBaseVO extends DefaultVo implements Serializable {

    private static final long serialVersionUID = 1L;

    // 공통 필드
    private Integer rownum;      // 행 번호
    private String regDt;        // 등록 일시
    private String regr;         // 등록자 ID
    private String regNm;        // 등록자 이름
    private String amdDt;        // 수정 일시
    private String amdr;         // 수정자 ID
    private String amdNm;        // 수정자 이름
    private String useYn;        // 사용 여부
    private String delYn;        // 삭제 여부

    // 페이징 처리 객체
    private Pagination paginationInfo;

    /**
     * 페이징 정보 초기화
     */
    public void initPagination(int totalRecordCount) {
        this.paginationInfo = new Pagination();
        this.paginationInfo.setCurrentPageNo(this.getPageIndex());
        this.paginationInfo.setRecordCountPerPage(this.getPageUnit());
        this.paginationInfo.setPageSize(this.getPageSize());
        this.paginationInfo.setTotalRecordCount(totalRecordCount);
        this.paginationInfo.calculate();
        
        // DefaultVo의 인덱스들도 동기화
        this.setFirstIndex(this.paginationInfo.getFirstRecordIndex());
        this.setLastIndex(this.paginationInfo.getLastRecordIndex());
        this.setRecordCountPerPage(this.paginationInfo.getRecordCountPerPage());
    }
}
