package com.kt.openapi.web.rest.api.vo;


/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.rest.api.vo
* 2. 타입명 : ApiGetSearchVO.java
* 3. 작성일 : 2017. 12. 5. 오후 7:24:28
* 4. 작성자 : JungHwan Hwang
* 5. 설명 :
* </pre>
*/
public class ApiGetSearchVO{
	
	private String mbrId; //회원아이디
	private String apiNo; //api 번호
	
	public String getMbrId() {
		return mbrId;
	}

	public void setMbrId(String mbrId) {
		this.mbrId = mbrId;
	}

	public String getApiNo() {
		return apiNo;
	}

	public void setApiNo(String apiNo) {
		this.apiNo = apiNo;
	}

	@Override
	public String toString() {
		return "SearchVO [mbrId=" + mbrId + ", apiNo=" + apiNo + "]";
	}
}
