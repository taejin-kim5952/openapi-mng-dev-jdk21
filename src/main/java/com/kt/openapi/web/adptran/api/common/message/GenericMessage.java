package com.kt.openapi.web.adptran.api.common.message;

import java.io.Serial;
import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kt.openapi.web.adptran.api.AdptranApiResultCode;

public class GenericMessage implements RestMessage, RestMessageAccessor, Serializable {

	@Serial
	private static final long serialVersionUID = 2716067110062942256L;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer resultCode = AdptranApiResultCode.RC_200_SUCESS.getCode();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String resultMessage = AdptranApiResultCode.RC_200_SUCESS.getMessage();
    
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Integer totalCount;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Integer pageSize;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Integer currentPage;

    //@JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data = null;

	//-- [drm][add]
    public GenericMessage() {
		super();
    }
    
    public GenericMessage(Integer resultCode, String resultMessage) {
		super();
		this.resultCode = resultCode;
		this.resultMessage = resultMessage;
	}

	@Override
    public Integer getResultCode() {
        return resultCode;
    }

    @Override
    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public String getResultMessage() {
        return resultMessage;
    }

    @Override
    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
    
    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object data) {
        this.data = data;
    }

    @Override
	public Integer getTotalCount() {
		return totalCount;
	}

	@Override
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

    @Override
	public Integer getPageSize() {
		return pageSize;
	}

	@Override
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

    @Override
	public Integer getCurrentPage() {
		return currentPage;
	}

	@Override
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	/*--[drm][ing]
	@Override
	public Map<String, ?> getMessageMap() {
		Map<String, Object> map_out = new HashMap<>();
		Integer totalCount = this.getTotalCount();
		if (totalCount != null) {
			map_out.put("totalCount", totalCount);
		}
		map_out.put("data", this.getData());

		//--##Map<String, Object> map_response = new HashMap<>();
		//--##map_response.put("resultCode", this.getResultCode());
		//--##map_response.put("resultMessage", this.getResultMessage());
		//--##map_out.put("response", map_response);
		map_out.put("resultCode", this.getResultCode());
		map_out.put("resultMessage", this.getResultMessage());
		
		return map_out;
		return null;
	}
	*/
}
