package com.kt.openapi.web.qna.vo;

import java.io.Serial;
import java.io.Serializable;


/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.qna.vo
* 2. 타입명 : QnAFileVO.java
* 3. 작성일 : 2017. 11. 30. 오후 8:46:36
* 4. 작성자 : user
* 5. 설명 : qna 첨부파일 등록
* </pre>
*/
public class QnAFileVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 699436723589855368L;
	
	private String atcFileNo;
	private String qnaId;
	private String saveFileNm;
	private String originFileNm;
	private String fileTypeCd;
	private String filePath;
	private long fileSize;
	private String useYn;
	private String downlCnt;
	private String showOdrg;
	private String regDt;
	private String regr;
	private String amdDt;
	private String amdr;
	
	public String getAtcFileNo() {
		return atcFileNo;
	}
	public void setAtcFileNo(String atcFileNo) {
		this.atcFileNo = atcFileNo;
	}
	public String getQnaId() {
		return qnaId;
	}
	public void setQnaId(String qnaId) {
		this.qnaId = qnaId;
	}
	public String getSaveFileNm() {
		return saveFileNm;
	}
	public void setSaveFileNm(String saveFileNm) {
		this.saveFileNm = saveFileNm;
	}
	public String getOriginFileNm() {
		return originFileNm;
	}
	public void setOriginFileNm(String originFileNm) {
		this.originFileNm = originFileNm;
	}
	public String getFileTypeCd() {
		return fileTypeCd;
	}
	public void setFileTypeCd(String fileTypeCd) {
		this.fileTypeCd = fileTypeCd;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public String getUseYn() {
		return useYn;
	}
	public void setUseYn(String useYn) {
		this.useYn = useYn;
	}
	public String getDownlCnt() {
		return downlCnt;
	}
	public void setDownlCnt(String downlCnt) {
		this.downlCnt = downlCnt;
	}
	public String getShowOdrg() {
		return showOdrg;
	}
	public void setShowOdrg(String showOdrg) {
		this.showOdrg = showOdrg;
	}
	public String getRegDt() {
		return regDt;
	}
	public void setRegDt(String regDt) {
		this.regDt = regDt;
	}
	public String getRegr() {
		return regr;
	}
	public void setRegr(String regr) {
		this.regr = regr;
	}
	public String getAmdDt() {
		return amdDt;
	}
	public void setAmdDt(String amdDt) {
		this.amdDt = amdDt;
	}
	public String getAmdr() {
		return amdr;
	}
	public void setAmdr(String amdr) {
		this.amdr = amdr;
	}
	
	@Override
	public String toString() {
		return "QnAFileVO [atcFileNo=" + atcFileNo + ", qnaId=" + qnaId + ", saveFileNm=" + saveFileNm
				+ ", originFileNm=" + originFileNm + ", fileTypeCd=" + fileTypeCd + ", filePath=" + filePath
				+ ", fileSize=" + fileSize + ", useYn=" + useYn + ", downlCnt=" + downlCnt + ", showOdrg=" + showOdrg
				+ ", regDt=" + regDt + ", regr=" + regr + ", amdDt=" + amdDt + ", amdr=" + amdr + "]";
	}

	
}
