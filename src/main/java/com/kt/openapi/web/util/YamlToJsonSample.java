/**
 *  OPEN API version 1.0
 *
 *  Copyright ⓒ 2017 kt corp. All rights reserved.
 *
 *  This is a proprietary software of kt corp, and you may not use this file except in
 *  compliance with license agreement with kt corp. Any redistribution or use of this
 *  software, with or without modification shall be strictly prohibited without prior written
 *  approval of kt corp, and the copyright notice above does not evidence any actual or
 *  intended publication of such software.
 * 
 */
package com.kt.openapi.web.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * yamlData을 jsonData로 변환
 * 
 */
public class YamlToJsonSample {
	
	protected static Log log = LogFactory.getLog(StringUtil.class);
	
	public static void main(String[] args) {
		BufferedReader br = null;
		FileReader textFileReader = null;
		
		String yamlLine = "";
		String yamlData = "";
		Object jsonData = "";
		try {
			textFileReader = new FileReader("C:\\Users\\user\\Downloads\\swagger.yaml");
			br = new BufferedReader(textFileReader);
			try {
				while ((yamlLine = br.readLine()) != null) {
					yamlData = yamlData + yamlLine;
				}
				//yaml을 json으로 변환
				YamlToJson YamlToJson = new YamlToJson(yamlData);
				jsonData = YamlToJson.convert();
				log.info("jsonData" + jsonData);
			} catch (IOException e) {
			  //-- [2023:codeeyes][empty_block issue]
				//e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
		  //-- [2023:codeeyes][empty_block issue]
			//e1.printStackTrace();
		}
		finally {
			//-- [2023:codeeyes][File 자원 해제 검사 필수 issue]
	    try {
				if (textFileReader != null) { textFileReader.close(); }
				if (br != null) { br.close(); }
	    } catch (IOException e) {
	      //-- [2023:codeeyes][empty_block issue]
	      //e.printStackTrace();
	    }
		}
	}
}