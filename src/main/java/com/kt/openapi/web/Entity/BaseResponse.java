package com.kt.openapi.web.Entity;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class BaseResponse {
	private String code;
	private String message;
	private  int total = 0;
	private List<Map<String,Object>> results;
	
	public BaseResponse() {
		
	}
	
	public BaseResponse(String code,String message, int total,List<Map<String,Object>> results) {
		this.code = code;
		this.message = message;
		this.total = total;
		this.results = results;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<Map<String, Object>> getResults() {
		return results;
	}

	public void setResults(List<Map<String, Object>> results) {
		this.results = results;
	}
	
	@Override
	public String toString(){
		StringBuffer rtn= new StringBuffer();
		rtn.append("code:");
		rtn.append(getCode());
		rtn.append("message:");
		rtn.append(getMessage());
		rtn.append("total:");
		rtn.append(getTotal());
		rtn.append("results:");
		rtn.append(getResults());
		
		return rtn.toString();
		
	}
}
