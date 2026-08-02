package com.kt.openapi.web.api.vo;

import com.kt.openapi.web.sample.vo.SampleDefaultVO;
import com.kt.openapi.web.util.CommonFunc;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.api.vo
 * 2. 타입명   : ApiMainVo.java
 * 3. 작성일   : 2017. 11. 10. 오후 5:36:17
 * 4. 작성자   : JeonGeun Kang
 * 5. 설명     : ApiMainVo
 * </pre>
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ApiMainVo extends SampleDefaultVO {

	@Serial
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private static CommonFunc commonFunc;
	
	private String apiSpcNo;
	
	private String newApiSpcNo;
	
	private String newApiCtgryNo;
	
	private String newApiNo;
	
	private String newParamNo;
	
	private String apiNo;
	
	private String prntsParamNo;
	
	private String paramNo;
	
	private String paramNm;
	
	/**
	 * 검토요청 타입
	 */
	private String reviewRqtTypeCd;  
	
	private String reviewRqtTitle;  
	
	private String reviewRqtSbst;  
	
	/**
	 * 시스템ID
	 */
	private String reviewSysId;  
	
	private String apiReviewAnsNo;	
	
	private String reviewOpin;				
	
	private String apiReviewRqtNo;	
	
	private String atcFileNo; 
	private String saveFileNm;      
	private String originFileNm;        
	private String fileTypeCd;          
	private String filePath ;            
	private String fileSize;             
	private String downlCnt;             
	
	private String apiSpcId;    
	
	private String ver;  
	
	private String yamlFileNm;  
	
	private String yamlFilePath;  
	
	private String YamlSbst;
	
	private String apiPath;
	private String method;
	private String sysId;
	private String leftDept;
	/**
	 * 검색어필드
	 */
	private String schText		= "";
	
	/**
	 * 탭구분
	 */
	private String tabCode		= "";
	
	/**
	 * 등록상태
	 */
	private String regSttusCd		= "";
	
	
	private List<String> apiSpcIdList	= new ArrayList<>();
	
	/**
	 * 사용자 권한 목록
	 */
	private List<String> userAutIdList	= new ArrayList<>();
	
	/**
	 * 사용자 시스템 목록
	 */
	private List<String> userSysIdList	= new ArrayList<>();
	
	
	/**
	 * 
	 */
	public String apiCtgryNo;
	
	public String apiCtgryNm;
	
	
	public String verDesc;
	
	
	/** */
	private String importType		= "";
	
	private String targetCd;
	
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

	@Override
	public void setRegr(String regr) {
		super.setRegr(commonFunc != null ? commonFunc.urlDecodeStr(regr) : regr);
	}

	@Override
	public void setAmdr(String amdr) {
		super.setAmdr(commonFunc != null ? commonFunc.urlDecodeStr(amdr) : amdr);
	}
	
}