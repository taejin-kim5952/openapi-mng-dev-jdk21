package com.kt.openapi.web.cmm.upload;


import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public class WebFileHelper {

	public static File transferToTempFile(MultipartFile multipartFile) throws IOException{
		if(multipartFile.getSize() > 0 ){
			File tempFile = File.createTempFile("kt_temp", "");
			multipartFile.transferTo(tempFile);
			return tempFile;
		}
		return null;
	}
}
