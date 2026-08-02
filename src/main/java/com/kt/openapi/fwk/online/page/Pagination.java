package com.kt.openapi.fwk.online.page;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 전자정부 프레임워크 PaginationInfo 를 대체하는 통합 커스텀 페이징 클래스
 * [mngOnm 표준 + PaginationInfo 호환성 통합]
 */
@Getter
@Setter
@ToString
public class Pagination implements Serializable {
    private static final long serialVersionUID = 1L;

    private int currentPageNo;      // 현재 페이지 번호 (egov 호환)
    private int recordCountPerPage; // 페이지당 게시글 수
    private int pageSize;           // 페이징 리스트의 사이즈 (1~10 등)
    private int totalRecordCount;   // 전체 게시글 수
    
    // 계산된 필드들
    private int totalPageCount;     // 전체 페이지 수
    private int firstIndex;         // MyBatis 쿼리용 시작 인덱스 (OFFSET)
    private int lastIndex;          // MyBatis 쿼리용 끝 인덱스 (LIMIT)
    private int firstPage;          // 페이징 리스트의 첫 페이지 번호
    private int lastPage;           // 페이징 리스트의 마지막 페이지 번호

    public Pagination() {
        this.currentPageNo = 1;
        this.recordCountPerPage = 10;
        this.pageSize = 10;
    }

    public Pagination(int currentPageNo, int recordCountPerPage, int pageSize, int totalRecordCount) {
        this.currentPageNo = (currentPageNo <= 0) ? 1 : currentPageNo;
        this.recordCountPerPage = (recordCountPerPage <= 0) ? 10 : recordCountPerPage;
        this.pageSize = (pageSize <= 0) ? 10 : pageSize;
        this.totalRecordCount = totalRecordCount;
        calculate();
    }

    /**
     * 페이징 관련 수치를 자동 계산합니다.
     * totalRecordCount 가 설정된 후 호출하거나, Getter 호출 시 자동 호출될 수 있습니다.
     */
    public void calculate() {
        if (this.totalRecordCount == 0) {
            this.totalPageCount = 1;
            this.firstPage = 1;
            this.lastPage = 1;
            this.firstIndex = 0;
            this.lastIndex = 0;
            return;
        }

        this.totalPageCount = ((this.totalRecordCount - 1) / this.recordCountPerPage) + 1;
        this.firstIndex = (this.currentPageNo - 1) * this.recordCountPerPage;
        this.lastIndex = this.currentPageNo * this.recordCountPerPage;
        this.firstPage = ((this.currentPageNo - 1) / this.pageSize) * this.pageSize + 1;
        this.lastPage = this.firstPage + this.pageSize - 1;
        
        if (this.lastPage > this.totalPageCount) {
            this.lastPage = this.totalPageCount;
        }
    }

    // --- egovframe PaginationInfo 호환 메서드 (Aliasing) ---

    public int getFirstRecordIndex() {
        return (currentPageNo - 1) * recordCountPerPage;
    }

    public int getLastRecordIndex() {
        return currentPageNo * recordCountPerPage;
    }

    public int getFirstPageNo() {
        return 1;
    }

    public int getLastPageNo() {
        return getTotalPageCount();
    }

    public int getFirstPageNoOnPageList() {
        return ((currentPageNo - 1) / pageSize) * pageSize + 1;
    }

    public int getLastPageNoOnPageList() {
        int lastPageNoOnPageList = getFirstPageNoOnPageList() + pageSize - 1;
        if (lastPageNoOnPageList > getTotalPageCount()) {
            lastPageNoOnPageList = getTotalPageCount();
        }
        return lastPageNoOnPageList;
    }

    public int getTotalPageCount() {
        if (totalRecordCount == 0) return 1;
        return ((totalRecordCount - 1) / recordCountPerPage) + 1;
    }

    // 기존 Pagination 클래스 필드명 대응 (추가 호환성)
    public int getCurrentPage() {
        return this.currentPageNo;
    }
}
