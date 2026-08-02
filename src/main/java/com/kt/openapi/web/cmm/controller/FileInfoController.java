package com.kt.openapi.web.cmm.controller;

import com.kt.openapi.web.api.dao.ApiRegDAO;
import com.kt.openapi.web.cmm.vo.CmnFileVo;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.*;
import java.net.URLEncoder;

/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.cmm.controller
* 2. 타입명 : EditorFileInfoController.java
* 3. 작성일 : 2017. 11. 10. 오전 10:14:42
* 4. 작성자 : user
* 5. 설명 : 다음에디터 파일 처리
* </pre>
*/
@Controller
@RequestMapping(value="/file")
public class FileInfoController{
	
	private static final Logger LOG = LoggerFactory.getLogger(FileInfoController.class);
	
    @Value("${imageServer.host}")
	private String imageServerHost;
    
    @Value("${typeServer.host}")
	private String typeServerHost;
    
    @Value("${yamlServer.host}")
  	private String yamlServerHost;
    
    @Value("${mypage.fileNm}")
  	private String mypageFileNm;
    
	@Autowired
	private ApiRegDAO apiRegDAO;
    
//    @Autowired
//    private UploadFileUtils uploadFileUtiles;
    
  
//	/**
//	* <pre>
//	* 1. 메소드명 : editorFileUpload
//	* 2. 작성일 : 2017. 11. 10. 오전 10:14:52
//	* 3. 작성자 : user
//	* 4. 설명 : 다음 에디터 이미지 업로드 처리
//	* </pre>
//	* @param request
//	* @param res
//	* @throws IOException
//	*/
//	@RequestMapping(value="/editorFileUpload.do", method=RequestMethod.POST)
//	public void editorFileUpload(HttpServletRequest request, HttpServletResponse res , @RequestParam("image_file") MultipartFile uploadFiles) throws IOException {
//		LOG.info("Start EditorFileInfoController editorFileUpload @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//		try {
//			LOG.debug("FILE isMultipartContent :: {}" , ServletFileUpload.isMultipartContent(request) );
//			LOG.debug("FILE uploadFiles getOriginalFilename :: {}" , uploadFiles.getOriginalFilename() );
//			 
//			if (ServletFileUpload.isMultipartContent(request)){
//				File tempFile = WebFileHelper.transferToTempFile(uploadFiles);//임시파일 생성
//				FileUploadInfo file = new FileUploadInfo(uploadFiles.getOriginalFilename(),tempFile);
//				CmnFileVo fileVo = uploadFileUtiles.uploadFileUpload(file);
//				LOG.debug("fileVo :: {}" ,fileVo );
//				LOG.debug("fileVo  getFilePath:: {}" ,fileVo.getFilePath() );
//				LOG.debug("fileVo  FULL PATH:: {}" ,fileVo.getFilePath()+fileVo.getSaveFileName() );
//	            //파일 기본경로
//			    res.setContentType("text/plain; charset=UTF-8");
//			    PrintWriter pw = res.getWriter();
////			    pw.print("{\"imageurl\" : \"/upload/"+fileVo.getOrgFileName()+"\",\"filename\":\""+fileVo.getOrgFileName()+"\",\"filesize\":\"" +fileVo.getFileSize() +"\",\"imagealign\":\"C\"}");
//			    pw.print("{\"imageurl\" : \""+fileVo.getFilePath()+fileVo.getSaveFileName()+"\",\"filename\":\""+fileVo.getOrgFileName()+"\",\"filesize\":\"" +fileVo.getFileSize() +"\",\"imagealign\":\"C\"}");
//			    pw.flush();
//			    pw.close();
//			}
//		} catch (Exception e) {
//				LOG.error("file upload error : {}", e);
//		}
//	}
	
	/**
	* <pre>
	* 1. 메소드명 : fileDownLoad
	* 2. 작성일 : 2017. 11. 13. 오후 1:53:54
	* 3. 작성자 : user
	* 4. 설명 : 파일 다운로드
	* </pre>
	* @param request
	* @param response
	* @param param
	* @throws Exception
	*/
	@RequestMapping(value="/fileDownLoad.do")
	public void fileDownLoad(HttpServletRequest request,HttpServletResponse response, HttpSession session, CmnFileVo fileInfo) throws Exception {
		LOG.debug("fileDownLoad Start");
		
		UserJoinVO userJoinVO = (UserJoinVO)session.getAttribute("ssUserVo");
		
		if(userJoinVO == null) {
			throw new Exception("No Login");
		} else {
			LOG.debug("searchFileParam>>" + fileInfo);
			//--[tag:SR-20210427][add]['..'경로 filtering][구조점검조치]
			String saveFileName = fileInfo.getSaveFileName();
			if (saveFileName == null) {
				throw new Exception("required saveFileName");
			}
			if (saveFileName.matches("..") == true) {
				throw new Exception("filtered Filename [FileName: %s]".formatted(saveFileName));
			}

			// Validate that the filename does not contain path traversal or path separator characters
			if (saveFileName.contains("..") || saveFileName.contains("/") || saveFileName.contains("\\")) {
				throw new Exception("Invalid or dangerous filename: " + saveFileName);
			}
							
			File uFile = null;
			int fSize = 0;
			
			LOG.debug("searchFileInfo>>" + fileInfo);
			LOG.debug("File.separator::" + File.separator);
			if(fileInfo != null
					&& fileInfo.getFilePath().indexOf("..") == -1
					&& fileInfo.getFilePath().indexOf(".\\") == -1
					&& fileInfo.getFilePath().indexOf(":") == -1
					&& fileInfo.getFilePath().indexOf("./") == -1
					&& fileInfo.getFilePath().indexOf("&quot;") == -1
					&& fileInfo.getFilePath().indexOf(".") == -1
					&& fileInfo.getFilePath().indexOf("\\") == -1
					
					&& fileInfo.getSaveFileName().indexOf("..") == -1
					&& fileInfo.getSaveFileName().indexOf(".\\") == -1
					&& fileInfo.getSaveFileName().indexOf(":") == -1
					&& fileInfo.getSaveFileName().indexOf("./") == -1
					&& fileInfo.getSaveFileName().indexOf("/") == -1
					&& fileInfo.getSaveFileName().indexOf("&quot;") == -1
					&& fileInfo.getSaveFileName().indexOf("\\") == -1
					){
				fileInfo.setFilePath(fileInfo.getFilePath().replaceAll("//", "/"));
				LOG.debug("Search File Path::" + imageServerHost + fileInfo.getFilePath());
				LOG.debug("fileInfo.getDownType():{}", fileInfo.getDownType());
				if("docx".equals(fileInfo.getDownType())) {
					LOG.debug("DOC File Path::" + typeServerHost + saveFileName);
					uFile = new File(typeServerHost, saveFileName);
				}else {
					uFile = new File(imageServerHost + fileInfo.getFilePath(), saveFileName);	
				}
				
				fSize = (int) uFile.length();
			}
			LOG.debug("File Size[" + fSize + "]");
			 
			if(fSize > 0){
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(uFile));
				// 원본 파일명을 가져온 후 개행 문자를 제거
				String orgFileName = fileInfo.getOrgFileName();
				String mimetype = "applicaiton/download;charset=utf-8";
				response.setBufferSize(fSize);
				response.setContentType(mimetype);
				response.setHeader("Content-Disposition", "attachment; filename=\""+ getDisposition(this.filterSafeValue(orgFileName), getBrowser(request)) + "\"");
				response.setHeader("Content-Transfer-Encoding", "binary");
				
				response.setContentLength(fSize);
	 
				FileCopyUtils.copy(in, response.getOutputStream());
				in.close();
				response.getOutputStream().flush();
				response.getOutputStream().close();
			}else{
				if(!"img".equals(fileInfo.getDownType())){
					response.setContentType("text/html;charset=utf-8");
					PrintWriter printwriter = response.getWriter();
					printwriter.println("<script>");
					printwriter.println("alert(\"요청하신 파일을 찾을 수 없습니다.\");");
					printwriter.println("</script>");
					printwriter.flush();
					printwriter.close();
				}else{
					//-- [2023:codeeyes][File 자원 해제 검사 필수 issue]
					BufferedInputStream in = null;
					try {
	  				uFile = new File(request.getSession().getServletContext().getRealPath("/") + "images", "noimg.png");
	  				fSize = (int) uFile.length();
						in = new BufferedInputStream(new FileInputStream(uFile));
	  				String mimetype = "applicaiton/download;charset=utf-8";
	  				response.setBufferSize(fSize);
	  				response.setContentType(mimetype);
	  				response.setHeader("Content-Disposition", "attachment; filename=\"" + "NoImage.png" + "\"");
	  				response.setHeader("Content-Transfer-Encoding", "binary");
	  				response.setContentLength(fSize);
	  				FileCopyUtils.copy(in, response.getOutputStream());
	  				in.close();
					}
					catch (IOException e) {
					  //-- [2023:codeeyes][empty_block issue]
						//e.printStackTrace();
					}
					finally {
						if (in != null) { in.close(); }
					}
					response.getOutputStream().flush();
					response.getOutputStream().close();
				}
			}
		}
		
	}
	
	// 1. 별도의 안전한 헤더 값 생성 메서드
	private String filterSafeValue(String value) {
	    if (value == null) return "";
	    // \r (CR), \n (LF) 문자를 제거하여 응답 분할 공격을 방지
	    return value.replaceAll("\r", "").replaceAll("\n", "");
	}
	  
//	 /**
//	* <pre>
//	* 1. 메소드명 : fileDownType
//	* 2. 작성일 : 2017. 11. 30. 오후 3:02:31
//	* 3. 작성자 : ANEUNTAEK
//	* 4. 설명 : 마이페이지 양식 다운로드
//	* </pre>
//	* @param request
//	* @param response
//	* @throws Exception
//	*/
//	@RequestMapping(value="/fileDownType.do")
//	 public void fileDownType(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		
//		File uFile = null;
//		int fSize = 0;
//		
//		String upDir = typeServerHost;
//		upDir = upDir.replaceAll("//", "/");
//		upDir = upDir+File.separator;
//		LOG.debug("upDir:{}", upDir);
//		String fileName = mypageFileNm;
//
//		uFile = new File(upDir, fileName);
//		fSize = (int) uFile.length();
//		
//		if(fSize > 0){
//			//-- [2023:codeeyes][File 자원 해제 검사 필수 issue]
//			BufferedInputStream in = null;
//			try {
//    		in = new BufferedInputStream(new FileInputStream(uFile));
//    		String mimetype = "applicaiton/download;charset=utf-8";
//    		response.setBufferSize(fSize);
//    		response.setContentType(mimetype);
//    		response.setHeader("Content-Disposition", "attachment; filename=\""+ getDisposition(fileName, getBrowser(request)) + "\"");
//    		response.setHeader("Content-Transfer-Encoding", "binary");
//  			response.setContentLength(fSize);
//  			FileCopyUtils.copy(in, response.getOutputStream());
//  			in.close();
//			}
//			catch (IOException e) {
//			  //-- [2023:codeeyes][empty_block issue]
//				//e.printStackTrace();
//			}
//			finally {
//				if (in != null) { in.close(); }
//			}
//			response.getOutputStream().flush();
//			response.getOutputStream().close();
//		}
// }
	
	 /**
	* <pre>
	* 1. 메소드명 : getBrowser
	* 2. 작성일 : 2017. 11. 13. 오후 1:53:38
	* 3. 작성자 : user
	* 4. 설명 : 접근 브라우저 체크
	* </pre>
	* @param request
	* @return
	*/
	private String getBrowser(HttpServletRequest request) { 
	    	String header = request.getHeader("User-Agent"); 
		if (header.indexOf("MSIE") > -1) { 
			return "MSIE"; 
		}else if (header.indexOf("Chrome") > -1) { 
			return "Chrome"; 
		}else if (header.indexOf("Opera") > -1) { 
			return "Opera"; 
		}else if (header.indexOf("Trident/7.0") > -1){
		//IE 11 이상 //IE 버전 별 체크 >> Trident/6.0(IE 10) , Trident/5.0(IE 9) , Trident/4.0(IE 8) 
			return "MSIE"; 
		} 
		return "Firefox"; 
		}
	
		private String getDisposition(String filename, String browser) throws Exception { 
			String encodedFilename = null;
	    	if (browser.equals("MSIE")) { 
	    		encodedFilename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20"); 
	    	}else if(browser.equals("Firefox")) { 
	    		encodedFilename = "\"" + new String(filename.getBytes("UTF-8"), "8859_1") + "\""; 
	    	}else if(browser.equals("Opera")) { 
	    		encodedFilename = "\"" + new String(filename.getBytes("UTF-8"), "8859_1") + "\""; 
	    	}else if(browser.equals("Chrome")) { 
	    		StringBuffer sb = new StringBuffer(); 
				for (int i = 0; i < filename.length(); i++) { 
					char c = filename.charAt(i); 
					if (c > '~') { 
						sb.append(URLEncoder.encode("" + c, "UTF-8")); 
					}else{ 
						sb.append(c); 
					} 
				} 
				encodedFilename = sb.toString(); 
			}else{
				throw new RuntimeException("Not supported browser"); 
			} 
	    	return encodedFilename; 
		}
		
		
		
//		/**
//		* <pre>
//		* 1. 메소드명 : yamlDownLoad
//		* 2. 작성일 : 2017. 12. 8. 오후 6:49:14
//		* 3. 작성자 : JungHwan Hwang
//		* 4. 설명 : YAML 파일 다운로드
//		* </pre>
//		* @param request
//		* @param response
//		* @param fileInfo
//		* @throws Exception
//		*/
//		@RequestMapping(value="/yamlDownLoad.do")
//		public void yamlDownLoad(HttpServletRequest request,HttpServletResponse response, ApiRegVO fileInfo) throws Exception {
//			LOG.debug("fileDownLoad Start");
//			LOG.debug("searchFileParam>>" + fileInfo);
//			
//			File uFile = null;
//			int fSize = 0;
//			
//			LOG.debug("searchFileInfo>>" + fileInfo);
//			LOG.debug("File.separator::" + File.separator);
//			
//			EgovMap fInfo = (EgovMap)apiRegDAO.selApiFileInfo(fileInfo);
//			
//			String filePath = (String)fInfo.get("yamlFilePath");
//			String fileName = (String)fInfo.get("yamlFileNm");
//			
//			if(fileInfo != null){
//				fileInfo.setFilePath(filePath.replaceAll("//", "/"));
//				LOG.debug("Search File Path::" + imageServerHost + filePath);
//				uFile = new File(yamlServerHost + filePath, fileName);
//				fSize = (int) uFile.length();
//			}
//			
//			LOG.debug("File Size[" + fSize + "]");
//			
//			if(fSize > 0){
//				//-- [2023:codeeyes][File 자원 해제 검사 필수 issue]
//				BufferedInputStream in = null;
//				try {
//  				in = new BufferedInputStream(new FileInputStream(uFile));
//  				String mimetype = "applicaiton/download;charset=utf-8";
//  				response.setBufferSize(fSize);
//  				response.setContentType(mimetype);
//  				response.setHeader("Content-Disposition", "attachment; filename=\""+ getDisposition(fileName, getBrowser(request)) + "\"");
//  				response.setHeader("Content-Transfer-Encoding", "binary");
//  				response.setContentLength(fSize);
//  				FileCopyUtils.copy(in, response.getOutputStream());
//  				in.close();
//				}
//  			catch (IOException e) {
//  			  //-- [2023:codeeyes][empty_block issue]
//  				//e.printStackTrace();
//  			}
//  			finally {
//  				if (in != null) { in.close(); }
//  			}
//				response.getOutputStream().flush();
//				response.getOutputStream().close();
//			}
//			
//		}
		
}
