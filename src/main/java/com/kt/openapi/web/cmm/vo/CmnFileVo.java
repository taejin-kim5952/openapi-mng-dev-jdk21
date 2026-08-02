package com.kt.openapi.web.cmm.vo;

import java.io.Serial;
import java.io.Serializable;


/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.cmm.vo
* 2. 타입명 : CmnFileVo.java
* 3. 작성일 : 2017. 11. 9. 오후 2:52:05
* 4. 작성자 : user
* 5. 설명 : 공통 파일 처리 vo
* </pre>
*/
public class CmnFileVo implements Serializable{

	@Serial
	private static final long serialVersionUID = 1L;

	private Integer fileNo; //파일번호
	private String downType;
	private String fileCate1; //자료분류1
	private String fileCate2; //자료분류2
	private String fileCate3; //자료분류3
	private Integer refNo; //참조번호
	private String fileType; //파일유형
	private String orgFileName; //원본파일명
	private String saveFileName; //저장파일명
	private String filePath; //저장경로
	private long fileSize; //파일사이즈
	private String useYn; //사용여부
	private Integer dnCnt; //다운로드횟수
	private String regDt; //등록일
	private String regUsr; //등록자
 	private String modDt; //수정일
 	private String modUsr; //수정자
 	
	public CmnFileVo() {
		super();
	}
	
	public CmnFileVo(String originalName, String savedName, long fileSize2, String fileTp, String filePaths) {
		orgFileName = originalName;
		saveFileName = savedName;
		fileType = fileTp;
		fileSize = fileSize2;
		filePath = filePaths;
	}
	
	
	public String getDownType() {
		return downType;
	}
	public void setDownType(String downType) {
		this.downType = downType;
	}
	public Integer getFileNo() {
		return fileNo;
	}
	public void setFileNo(Integer fileNo) {
		this.fileNo = fileNo;
	}
	public Integer getRefNo() {
		return refNo;
	}
	public void setRefNo(Integer refNo) {
		this.refNo = refNo;
	}
	public String getFileCate1() {
		return fileCate1;
	}
	public void setFileCate1(String fileCate1) {
		this.fileCate1 = fileCate1;
	}
	public String getFileCate2() {
		return fileCate2;
	}
	public void setFileCate2(String fileCate2) {
		this.fileCate2 = fileCate2;
	}
	public String getFileCate3() {
		return fileCate3;
	}
	public void setFileCate3(String fileCate3) {
		this.fileCate3 = fileCate3;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getOrgFileName() {
		return orgFileName;
	}
	public void setOrgFileName(String orgFileName) {
		this.orgFileName = orgFileName;
	}
	public String getSaveFileName() {
		return saveFileName;
	}
	public void setSaveFileName(String saveFileName) {
		this.saveFileName = saveFileName;
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
	public Integer getDnCnt() {
		return dnCnt;
	}
	public void setDnCnt(Integer dnCnt) {
		this.dnCnt = dnCnt;
	}
	public String getRegDt() {
		return regDt;
	}
	public void setRegDt(String regDt) {
		this.regDt = regDt;
	}
	public String getRegUsr() {
		return regUsr;
	}
	public void setRegUsr(String regUsr) {
		this.regUsr = regUsr;
	}
	public String getModDt() {
		return modDt;
	}
	public void setModDt(String modDt) {
		this.modDt = modDt;
	}
	public String getModUsr() {
		return modUsr;
	}
	public void setModUsr(String modUsr) {
		this.modUsr = modUsr;
	}

	@Override
	public String toString() {
		return "CmnFileVo [fileNo=" + fileNo + ", fileCate1=" + fileCate1 + ", fileCate2=" + fileCate2 + ", fileCate3="
				+ fileCate3 + ", refNo=" + refNo + ", fileType=" + fileType + ", orgFileName=" + orgFileName
				+ ", saveFileName=" + saveFileName + ", filePath=" + filePath + ", fileSize=" + fileSize + ", useYn="
				+ useYn + ", dnCnt=" + dnCnt + ", regDt=" + regDt + ", regUsr=" + regUsr + ", modDt=" + modDt
				+ ", modUsr=" + modUsr + "]";
	}
}
