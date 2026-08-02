package com.kt.openapi.web.cmm.upload;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RunCmdUtil {
   
    private static final Logger LOGGER = LoggerFactory.getLogger(RunCmdUtil.class);
    
    @Value("${apidocServer.host}")
    private String apidocServerHost;
    
    @Value("${wsdlImport.js.fileNm}")
    private String wsdlImportJsFileNm;

    public boolean runCmd(String inputFilePath , String outFilePath) {
    	LOGGER.error("RunCmdUtil runCmd  START ######################################");
    	LOGGER.error("RunCmdUtil runCmd  inputFilePath : {}", inputFilePath);
    	LOGGER.error("RunCmdUtil runCmd  outFilePath : {}", outFilePath);
    	///c : 명령어 실행 옵션
    	String command = "cmd.exe /c apidoc-swagger -i "+apidocServerHost + inputFilePath+" -o "+outFilePath;
    	LOGGER.error("command :: {}", command);
    	//C://openapi/attachfile/apidoc/upload//2018/01/05/f4ff5f9e-9269-44b1-b3b6-806f0b47f101.js
		try {
			Runtime.getRuntime().exec(command);
			LOGGER.error("getRuntime START");
		} catch (IOException e) {
			LOGGER.error("cmd running error : {}", e.toString());
			return false;
		} catch (Exception e) {
			LOGGER.error("Exception running error : {}", e.toString());
			return false;
		}
		return true;
    }
    
    public boolean runNodeCmd(String url, String wsdlPath, String rPath) {
    	LOGGER.error("RunCmdUtil runCmd  START ######################################");
    	
    	//--[tag:SR-20210427][add][구조점검조치]
    	if (this.IsFilteredCmdInjection(url) || this.IsFilteredCmdInjection(wsdlPath) || this.IsFilteredCmdInjection(rPath)) {
    		return false;
    	} 

    	///c : 명령어 실행 옵션
    	String command = "cmd.exe /c node " + wsdlPath + File.separator + wsdlImportJsFileNm + " " + url + " " + rPath;
    	LOGGER.error("command :: {}", command);
    	//C://openapi/attachfile/apidoc/upload//2018/01/05/f4ff5f9e-9269-44b1-b3b6-806f0b47f101.js
		try {
			Runtime.getRuntime().exec(command);
			LOGGER.error("getRuntime START");
		} catch (IOException e) {
			LOGGER.error("cmd running error : {}", e.toString());
			return false;
		} catch (Exception e) {
			LOGGER.error("Exception running error : {}", e.toString());
			return false;
		}
		return true;
    }

    //--[tag:SR-20210427][add][injection filtering][구조점검조치]
    private boolean IsFilteredCmdInjection(String token) {
		return ((token == null) ? "" : token).matches("[|;&]");
    }
}
