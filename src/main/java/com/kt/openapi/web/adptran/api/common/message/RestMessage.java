package com.kt.openapi.web.adptran.api.common.message;

public interface RestMessage {
    void setResultCode(Integer returnCode);
    
    void setResultMessage(String message);

    void setData(Object data);

    void setTotalCount(Integer totalCount);
    void setPageSize(Integer pageSize);
    void setCurrentPage(Integer currentPage);
}
