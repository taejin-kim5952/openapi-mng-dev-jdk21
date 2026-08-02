package com.kt.openapi.web.userJoin.vo;

import com.kt.openapi.web.auth.vo.AuthVO;
import com.kt.openapi.web.util.CommonFunc;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.userJoin.vo
* 2. 타입명 : UserJoinVO.java
* 3. 작성일 : 2017. 11. 30. 오후 2:58:04
* 4. 작성자 : ANEUNTAEK
* 5. 설명 : 회원 가입 저장
* [마이그레이션] Lombok 적용 및 validator.xml 규칙 이식
* </pre>
*/

@Getter
@Setter
@ToString
public class UserJoinVO  {
	
	private String mbrId ;

	@NotBlank(message="{userJoinVO.idDivCd}")
	private String idDivCd;

	@NotBlank(message="{userJoinVO.mbrNm}")
    private String mbrNm;

	@NotBlank(message="{userJoinVO.cmpnNm}")
    private String cmpnNm;

    private String telNo;
    private String telNo1;

	@NotBlank(message="{bbsSaveVo.telNo2}")
    private String telNo2;

	@NotBlank(message="{bbsSaveVo.telNo3}")
    private String telNo3;

	@NotBlank(message="{bbsSaveVo.email}")
	@Email(message="{errors.email}")
    private String email;
    private String stpltAgreeYn;
    private String indvInfoAgreeYn;
    private String mbrSttusCd;
    private String admrYn;
    private String lastLoginDt;
    private String sbscDt;
    private String useYn;
    private String regDt;
    private String regr;
    private String amdDt;
    private String amdr;
    private List<AuthVO> authList;
    private String enCmbrId;
    private String maskingMbrId;
    
    /*
     * API Link(Studio) Gateway Writer 권한 설정
     *   Y: Being Writer
     *   N: Not Writer
     * CYD - 2020.07.08
     */
    private String writerYn;
    
    /*
     * API Link(Studio) Gateway Observer 권한 설정
     *   Y: Being Observer(=Read)
     *   N: Not Observer
     * CYD - 2020.07.08
     */
    private String observerYn;

	//-- [커스텀 Setter] URL 디코딩 로직 유지
	public void setMbrId(String mbrId) {
		this.mbrId = CommonFunc.urlDecodeStr(mbrId);
	}
	public void setMbrNm(String mbrNm) {
		this.mbrNm = CommonFunc.urlDecodeStr(mbrNm);
	}
	public void setRegr(String regr) {
		this.regr = CommonFunc.urlDecodeStr(regr);
	}
	public void setAmdr(String amdr) {
		this.amdr = CommonFunc.urlDecodeStr(amdr);
	}
	public void setEnCmbrId(String enCmbrId) {
		this.enCmbrId = CommonFunc.urlDecodeStr(enCmbrId);
	}
}
