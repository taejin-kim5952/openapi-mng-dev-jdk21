package com.kt.openapi.web.cmm.upload;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;

/**
 * 파일 업로드 정보 
 * @author baekjin
 *
 */
public class FileUploadInfo implements Serializable{
	
	public final static String TYPE_FILE = "file";
	public final static String TYPE_IMAGE = "image";

	/** S-UID */
	@Serial
	private static final long serialVersionUID = 2791218706396709983L;
		
	/** File data */
	private File file;
	/** originalFileName */
	private String originalFileName;
	/** upload seq */
	private int uploadSeq;
		
	/** file type */
	private String fileType;
	/** file path*/
	private String filePath;
	/** size */
	private Long fileSize;	
	/** useYn */
	private String useYn;
	
	private String extension;
	
	/** savedName */
	private String savedFileName;
	
	public FileUploadInfo(String originalFinalName, File file) {
		
		String extension = FileUtil.getExtension(originalFinalName);
		
		setExtension(extension);
		setOriginalFileName(originalFinalName);
		setFile(file);
		setFileSize(file.length());
		checkFileType(extension);		
	}
	
	public FileUploadInfo() {}

	public void checkFileType(String extension) {
		if(MediaUtil.getMediaType(extension)!= null) {
			setFileType(FileUploadInfo.TYPE_IMAGE);	
		}else{
			setFileType(FileUploadInfo.TYPE_FILE);			
		}
	}

	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getOriginalFileName() {
		return originalFileName;
	}
	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}
	public int getUploadSeq() {
		return uploadSeq;
	}
	public void setUploadSeq(int uploadSeq) {
		this.uploadSeq = uploadSeq;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public Long getFileSize() {
		return fileSize;
	}
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	public String getUseYn() {
		return useYn;
	}
	public void setUseYn(String useYn) {
		this.useYn = useYn;
	}
	public String getSavedFileName() {
		return savedFileName;
	}
	public void setSavedFileName(String savedFileName) {
		this.savedFileName = savedFileName;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	
}
