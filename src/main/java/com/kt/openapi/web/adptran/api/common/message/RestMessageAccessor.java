package com.kt.openapi.web.adptran.api.common.message;

import java.util.Map;

public interface RestMessageAccessor {

	Integer getResultCode();
    
	String getResultMessage();
    
	Object getData();

	Integer getTotalCount();
	Integer getPageSize();
	Integer getCurrentPage();

    //--[drm][ing]Map<String, ?> getMessageMap();
}
