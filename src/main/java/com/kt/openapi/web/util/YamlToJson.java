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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.util.Yaml;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.util
 * 2. 타입명   : YamlToJson.java
 * 3. 작성일   : 2017. 11. 9. 오후 7:37:02
 * 4. 작성자   : JeonGeun Kang
 * 5. 설명     : yaml에서 json으로 변환
 * </pre>
 */
public class YamlToJson {

	private String yamlInputPath;

	protected static Log log = LogFactory.getLog(StringUtil.class);

	YamlToJson(final String yamlInputPath) {
		this.yamlInputPath = yamlInputPath;
	}

	/**
	 * <pre>
	 * 1. 메소드명 : convert
	 * 2. 작성일   : 2017. 11. 10. 오전 9:30:18
	 * 3. 작성자   : JeonGeun Kang
	 * 4. 설명     : yamlInputPath에 있는 파일 변환
	 * </pre>
	 * 
	 * @return String
	 */
	public String convert() {
		/*
		// 파일 저장시에 사용
		File outputDirectory = new File(outputDirectoryPath);
		if (!outputDirectory.exists()) {
			outputDirectory.mkdirs();
			log.info("OutputDirectory created");
		}
		File jsonFile = new File(outputDirectory, getYamlFilename() + ".json");
		FileWriter fileWriter = null;
		*/
		try {
			return getYamlFileContentAsJsonString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public String getYamlFileContentAsJsonString() throws IOException {
		String data = "";

		// yaml데이터를 url에서 제공받을때 사용
		// if (yamlInputPath.startsWith("http") || yamlInputPath.startsWith("https")) {
		// data = new String(Resources.toByteArray(new URL(yamlInputPath)));
		// } else {
		log.info("yamlInputPath=" + yamlInputPath);
		data = new String(Files.readAllBytes(Path.of(yamlInputPath)));
		// }
		ObjectMapper yamlMapper = Yaml.mapper();
		JsonNode rootNode = yamlMapper.readTree(data);
		// must have swagger node set
		JsonNode swaggerNode = rootNode.get("swagger");

		return rootNode.toString();
	}

	/**
	* <pre>
	* 1. 메소드명 : getYamlFileContentAsJsonObject
	* 2. 작성일 : 2017. 11. 10. 오후 5:21:47
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : JSON 파일을 읽어서 JSON 객체를 내려준다.
	* </pre>
	* @return
	* @throws IOException
	*/
	public JsonNode getYamlFileContentAsJsonObject() throws IOException {
		String data = "";

		// 로컬 파일 경로 체크 로직

		// 파일 읽기
		data = new String(Files.readAllBytes(Path.of(yamlInputPath)));
		
		ObjectMapper yamlMapper = Yaml.mapper();
		JsonNode rootNode = yamlMapper.readTree(data);

		return rootNode;
	}
	
	public String getYamlFilename() {
		if (yamlInputPath.startsWith("http") || yamlInputPath.startsWith("https")) {
			int lastSlashIndex = yamlInputPath.lastIndexOf("/");
			String filename = yamlInputPath.substring(lastSlashIndex + 1);
			return filenameSubstring(filename);
		} else {
			File file = new File(yamlInputPath);
			if (!file.exists()) {
				throw new RuntimeException("Api-File not found: " + yamlInputPath);
			} else {
				String filename = file.getName();
				return filenameSubstring(filename);
			}
		}
	}

	public String filenameSubstring(String filename) {
		return filename.substring(0, filename.indexOf("."));
	}

	public YamlToJsonBuilder builder() {
		return new YamlToJsonBuilder();
	}

	public class YamlToJsonBuilder {
		private String yamlInputPath;

		public YamlToJson build() {
			return new YamlToJson(yamlInputPath);
		}
	}
}