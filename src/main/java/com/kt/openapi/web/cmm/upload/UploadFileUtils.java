package com.kt.openapi.web.cmm.upload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import com.kt.openapi.web.cmm.vo.CmnFileVo;
import com.kt.openapi.web.util.StringUtil;
import com.kt.openapi.web.util.XSSFilter;



@Component
//@Async
public class UploadFileUtils {

    @Value("${upload.image.path}")
    private String uploadImagePath;

    @Value("${upload.file.path}")
    private String uploadFilePath;
    
    @Value("${imageServer.host}")
	private String imageServerHost;
    
    @Value("${yaml.file.path}")
    private String yamlFilePath;
    
    @Value("${apidocServer.host}")
    private String apidocServerHost;
    
    @Value("${yamlServer.host}")
 	private String yamlServerHost;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadFileUtils.class);

    public static void multiUploadFile(String uploadPath, String savedName, byte[]fileData)throws Exception{
        File target = new File(uploadPath, savedName);
        FileCopyUtils.copy(fileData, target);
    }

    /**
     * 파일 업로드
     * @param fileUploadInfo
     * @throws Exception
     */
    public CmnFileVo uploadFileUpload(FileUploadInfo fileUploadInfo)throws Exception{
    	LOGGER.debug("fileUploadInfo : {}", fileUploadInfo);
    	LOGGER.debug("fileUploadInfo  getOriginalFileName: {}", fileUploadInfo.getOriginalFileName());
    	LOGGER.debug("fileUploadInfo  getFileType: {}", fileUploadInfo.getFileType());
        UUID uid = UUID.randomUUID();
        String savedName = uid.toString();
        String savedPath = "";
        File target = null;
		String formatName = fileUploadInfo.getOriginalFileName().substring(fileUploadInfo.getOriginalFileName().lastIndexOf(".")+1);
		
		fileUploadInfo.setOriginalFileName( XSSFilter.toText(StringUtil.isNullToString(fileUploadInfo.getOriginalFileName())) );
		LOGGER.debug("###uploadFileUpload### FileName : {}", fileUploadInfo.getOriginalFileName());
        if("file".equals(fileUploadInfo.getFileType())){
            savedPath = calcPath(imageServerHost + uploadFilePath + File.separator);
            target = new File(imageServerHost + uploadFilePath + File.separator + savedPath, savedName);
            FileUtil.makeDirectory(imageServerHost +uploadFilePath + File.separator + File.separator + savedPath);
            fileUploadInfo.setFilePath(uploadFilePath + File.separator + savedPath);
            
            LOGGER.debug("imageServerHost : {}",  imageServerHost);
            LOGGER.debug("uploadFilePath : {}",  uploadFilePath);
            LOGGER.debug("savedPath : {}",  savedPath);

        }else if("image".equals(fileUploadInfo.getFileType())){
            savedPath = calcPath(imageServerHost + uploadImagePath + File.separator);
            target = new File(imageServerHost + uploadImagePath + File.separator + savedPath, savedName);
            FileUtil.makeDirectory(imageServerHost + uploadImagePath + File.separator +  File.separator + savedPath);
            fileUploadInfo.setFilePath(uploadImagePath + File.separator + savedPath);
        }
        fileUploadInfo.setFilePath(fileUploadInfo.getFilePath().replace("\\", "/"));
        fileUploadInfo.setSavedFileName(savedName);
        
        LOGGER.debug("fileUploadInfo.getFilePath : {}",  fileUploadInfo.getFilePath());
        LOGGER.debug("fileUploadInfo.getSavedFileName : {}",  fileUploadInfo.getSavedFileName());

        FileCopyUtils.copy(fileUploadInfo.getFile(), target);
        
        return new CmnFileVo(fileUploadInfo.getOriginalFileName(), savedName, fileUploadInfo.getFileSize(), formatName, fileUploadInfo.getFilePath());
    }
    
    
    /**
     * API DOC 파일 업로드
     * @param fileUploadInfo
     * @throws Exception
     */
    public CmnFileVo apidocUpload(FileUploadInfo fileUploadInfo)throws Exception{
    	LOGGER.debug("fileUploadInfo : {}", fileUploadInfo);
    	LOGGER.debug("fileUploadInfo  getOriginalFileName: {}", fileUploadInfo.getOriginalFileName());
    	LOGGER.debug("fileUploadInfo  getFileType: {}", fileUploadInfo.getFileType());
    	LOGGER.debug("fileUploadInfo  getExtension: {}", fileUploadInfo.getExtension());
        UUID uid = UUID.randomUUID();
        String savedName = uid.toString() +"."+fileUploadInfo.getExtension();
        String savedPath = "";
        File target = null;
		String formatName = fileUploadInfo.getOriginalFileName().substring(fileUploadInfo.getOriginalFileName().lastIndexOf(".")+1);
		LOGGER.debug("formatName : {}", formatName);
		
	    //--[tag:SR-20210427][add][extention filtering][구조점검조치]
	    String validExtList = "zip";
		if (FileUtil.IsFilteredFileExtention(savedName, validExtList) == false) {
			throw new Exception("IsFilteredFileExtention(): [FileName: %s][validExtList: %s]".formatted(savedName, validExtList));
		}

        savedPath = calcPath(apidocServerHost + uploadFilePath + File.separator);
        target = new File(apidocServerHost + uploadFilePath + File.separator + savedPath, savedName);
        FileUtil.makeDirectory(apidocServerHost +uploadFilePath + File.separator + File.separator + savedPath);
        fileUploadInfo.setFilePath(uploadFilePath + File.separator + savedPath);
        
        LOGGER.debug("imageServerHost : {}",  imageServerHost);
        LOGGER.debug("uploadFilePath : {}",  uploadFilePath);
        LOGGER.debug("savedPath : {}",  savedPath);

        fileUploadInfo.setFilePath(fileUploadInfo.getFilePath().replace("\\", "/"));
        fileUploadInfo.setFilePath(fileUploadInfo.getFilePath().replace("//", "/"));
        fileUploadInfo.setSavedFileName(savedName);
        
        LOGGER.debug("fileUploadInfo.getFilePath : {}",  fileUploadInfo.getFilePath());
        LOGGER.debug("fileUploadInfo.getSavedFileName : {}",  fileUploadInfo.getSavedFileName());

        FileCopyUtils.copy(fileUploadInfo.getFile(), target);
        
        return new CmnFileVo(fileUploadInfo.getOriginalFileName(), savedName, fileUploadInfo.getFileSize(), formatName, fileUploadInfo.getFilePath());
    }

    public static String calcPath(String uploadPath){

        Calendar cal = Calendar.getInstance();

        String yearPath = File.separator+cal.get(Calendar.YEAR);
        String monthPath = yearPath + File.separator + new DecimalFormat("00").format(cal .get(Calendar.MONTH)+1);
        String datePath = monthPath + File.separator + new DecimalFormat("00").format(cal .get(Calendar.DATE));

        makeDir(uploadPath, yearPath, monthPath, datePath);

        return datePath;
    }

    private static void makeDir(String uploadPath, String... paths){

        if(new File(paths[ paths.length-1]).exists()){
            return;
        }

        for(String path : paths){
        File dirPath = new File(uploadPath + path);
            if(!dirPath.exists()){
                dirPath.mkdir();
            }
        }
    }
    
   
    /**
    * <pre>
    * 1. 메소드명 : transferYamlFile
    * 2. 작성일 : 2017. 11. 24. 오후 9:03:06
    * 3. 작성자 : JungHwan Hwang
    * 4. 설명 : YAML 파일을 새로운 폴더로 복사
    * </pre>
    * @param filePah 기존 패스
    * @param fileName 기존 파일
    * @param apiSpcNo 신규 저장될 파일명
    * @param fileType 파일구분 현재는 yaml  한 개만 존재
    * @return
    * @throws Exception
    */
    public CmnFileVo transferYamlFile(String filePath , String fileName , String apiSpcNo , String fileType)throws Exception{
    	CmnFileVo cmnFileVo = new CmnFileVo();
    	
    	LOGGER.debug("filePah : {}", filePath );
    	LOGGER.debug("fileUploadInfo  fileName: {}", fileName);
    	LOGGER.debug("fileUploadInfo  apiSpcNo: {}", apiSpcNo);
    	
        String savedName = apiSpcNo;
        String savedPath = "";
        File target = null;
		String formatName = fileName.substring(fileName.lastIndexOf(".")+1);
		
		LOGGER.debug("formatName : {}", formatName);
		
		//CmnFileVo fileVo = uploadFileUtiles.transferYamlFile( "//2017/11/23/" , "swagger.yaml"  , "1" ); 
		
		LOGGER.debug("yamlServerHost + yamlFilePath + filePath : {} , {}", yamlServerHost + yamlFilePath + filePath , fileName);
		
		File orgFile = new File(yamlServerHost + yamlFilePath + filePath, fileName);
		
        if("yaml".equals(fileType)){
        	savedName = savedName+"";
            savedPath = calcPath(yamlServerHost + yamlFilePath + File.separator);
            target = new File(yamlServerHost + yamlFilePath + File.separator + savedPath, savedName);
            FileUtil.makeDirectory(yamlServerHost +yamlFilePath + File.separator + File.separator + savedPath);
            cmnFileVo.setFilePath(yamlFilePath + File.separator + savedPath);
            
            LOGGER.debug("cmnFileVo.getFilePath() : {}", cmnFileVo.getFilePath());
            LOGGER.debug("cmnFileVo.getFilePath() : {}", cmnFileVo.getFilePath());
            
        }else if("image".equals(fileType)){
            savedPath = calcPath(yamlServerHost + yamlFilePath + File.separator);
            target = new File(yamlServerHost + yamlFilePath + File.separator + savedPath, savedName);
            FileUtil.makeDirectory(yamlServerHost + yamlFilePath + File.separator +  File.separator + savedPath);
            cmnFileVo.setFilePath(yamlFilePath + File.separator + savedPath);
        }
        
        cmnFileVo.setFilePath(cmnFileVo.getFilePath().replace("\\", "/"));
        cmnFileVo.setSaveFileName(savedName);
       
        FileCopyUtils.copy(orgFile, target);
        
    	return cmnFileVo;
    }
    
    /**
    * <pre>
    * 1. 메소드명 : makeYamlFile
    * 2. 작성일 : 2017. 11. 28. 오전 10:18:30
    * 3. 작성자 : JungHwan Hwang
    * 4. 설명 : API 저장시 YAML 파일 만들기
    * </pre>
    * @param filePath
    * @param fileName
    * @param yaml
    * @throws Exception
    */
    public void makeYamlFile(String filePath , String fileName , String yaml)throws Exception{
    	
    	LOGGER.debug("makeYamlFile Startt");

	    //--[tag:SR-20210427][add][extention filtering][구조점검조치]
	    String validExtList = ",yaml";
		if (FileUtil.IsFilteredFileExtention(fileName, validExtList) == false) {
			throw new Exception("IsFilteredFileExtention(): [FileName: %s][validExtList: %s]".formatted(fileName, validExtList));
		}
    	
    	BufferedWriter fw = null;
    	
    	String fullFilePath = yamlServerHost + yamlFilePath + File.separator + filePath + File.separator + fileName;
    	
    	LOGGER.debug("makeYamlFile fullFilePath === {}"  , fullFilePath );
    	
    	//-- [tag:job-20200420][add] {
    	//-- 폴더미존재시 생성
		File pathFile = new File(yamlServerHost + yamlFilePath + File.separator + filePath);
    	if (false == pathFile.isDirectory()) {
    		pathFile.mkdirs();
    	}
    	//-- [tag:job-20200420][add] }
    	
    	//fullFilePath = fullFilePath.replaceAll("\\" , "/"); 
    	
    	File delFile = new File(fullFilePath);
    	
    	if (delFile.isFile()) {
    		delFile.delete();
	    }
    	
    	try{
    		// BufferedWriter 와 FileWriter를 조합하여 사용 (속도 향상)
            fw = new BufferedWriter(new FileWriter(fullFilePath, true));
             
            // 파일안에 문자열 쓰기
            fw.write(yaml);
            fw.flush();

            // 객체 닫기
            fw.close();
    	}catch(Exception e) {
    		throw e;
    	}finally {
			if(fw!=null) {
				fw.close();
			}
		}
    }
}
