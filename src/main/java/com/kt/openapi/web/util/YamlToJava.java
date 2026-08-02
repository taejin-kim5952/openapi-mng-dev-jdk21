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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.openapi.web.api.vo.ApiRegVO;

import io.swagger.util.Yaml;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.util
* 2. 타입명 : YamlToJava.java
* 3. 작성일 : 2017. 11. 16. 오전 9:27:11
* 4. 작성자 : JungHwan Hwang
* 5. 설명 : Yaml 데이터를 읽어와서 데이터 컨트롤 하기 위함.
* </pre>
*/
@Component
public class YamlToJava {

	protected static Log log = LogFactory.getLog(StringUtil.class);
	
	private static final String spaceBar	= "  ";
	private static final String spaceBar4 	= "        ";
	private static final String spaceBar5 	= "          ";
	private static final String spaceBar6 	= "            ";
	private static final String spaceBar7 	= "              ";
	
	
    @Value("${yaml.file.path}")
    private String yamlFilePath;
    
    @Value("${yamlServer.host}")
 	private String yamlServerHost;
    
	/**
	* <pre>
	* 1. 메소드명 : getYamlToString
	* 2. 작성일 : 2017. 11. 16. 오전 9:32:14
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : YAML 파일을 읽어와서 
	* </pre>
	* @param yamlInputPath
	* @return
	*/
	public static HashMap<String,Object> getYamlToString(String yamlInputPath) {
		HashMap<String,Object> resultMap	= new HashMap<String,Object>();
        
        try {
        	
        	YamlToJson YamlToJson2 = new YamlToJson("C:\\Users\\keith\\Downloads\\swagger.yaml");
			String jNode2 = YamlToJson2.getYamlFileContentAsJsonString();
			
			HashMap<String,Object> jsonResult = new ObjectMapper().readValue(jNode2, HashMap.class);
			
			/** INFO */
			log.info("swagger=" + jsonResult.get("swagger"));
			log.info("host:" + jsonResult.get("host"));
			log.info("basePath:" + jsonResult.get("basePath"));
			
			ArrayList<String> schemesList = getStringVectorToString(jsonResult.get("schemes"));
			if(schemesList!=null ) {
				log.info("schemes:");
				for(String str : schemesList) {
					log.info(spaceBar + "" + str);
				}
			}
			
			//log.info("info=" + jsonResult.get("info"));
			log.info("info.getClass=" + jsonResult.get("info").getClass());
			
			HashMap<String,Object> infoMap = (HashMap<String,Object>)jsonResult.get("info");
			
			log.info("info.version=" + infoMap.get("version"));
			log.info("info.title=" + infoMap.get("title"));
			log.info("info.description=" + infoMap.get("description"));
			log.info("info.termsOfService=" + infoMap.get("termsOfService"));
			
			infoMap.put("version",infoMap.get("version"));
			infoMap.put("title",infoMap.get("title"));
			infoMap.put("description",infoMap.get("description"));
			infoMap.put("termsOfService",infoMap.get("termsOfService"));
			
			if(infoMap.get("contact") instanceof java.util.LinkedHashMap ) {
				HashMap<String,Object> infoContactMap = (HashMap<String,Object>)infoMap.get("contact");
				log.info("info.contact.name=" + infoContactMap.get("name"));
				log.info("info.contact.email=" + infoContactMap.get("email"));
				log.info("info.contact.url=" + infoContactMap.get("url"));
				
				infoMap.put("contactName",infoContactMap.get("name"));
				infoMap.put("contactEmail",infoContactMap.get("email"));
				infoMap.put("contactUrl",infoContactMap.get("url"));
			}
			
			if(infoMap.get("license") instanceof java.util.LinkedHashMap ) {
				HashMap<String,Object> infoContactMap = (HashMap<String,Object>)infoMap.get("contact");
				log.info("info.license.name=" + infoContactMap.get("name"));
				log.info("info.license.url=" + infoContactMap.get("url"));
				
				infoMap.put("contactName",infoContactMap.get("name"));
				infoMap.put("contactEmail",infoContactMap.get("url"));
			}
			
			resultMap.put("info",infoMap);
			
			/** PATH START */
			ArrayList pathsList = (ArrayList)getMapKeyReuturnArrayList( jsonResult , "paths" );
			if(pathsList!=null) {
				for(int i=0 ; i < pathsList.size() ; i++) {
					String pathsName = (String)pathsList.get(i);
					HashMap<String,Object> pathsMap = (HashMap<String,Object>)jsonResult.get("paths");
					ArrayList pathList = (ArrayList)getMapKeyReuturnArrayList( pathsMap , pathsName );
					
					log.info(spaceBar + pathsName);
					
					//System.out.println("pathsMap.get(pathsName);="+pathsMap.get(pathsName));
					
					for(int j=0 ; j < pathList.size() ; j++) {
						String pathName = (String)pathList.get(j);
						HashMap<String,Object> pathMap 		= (HashMap<String,Object>)pathsMap.get(pathsName);	// ex) 	paths 	/pets  	
						HashMap<String,Object> pathInfoMap 	= (HashMap<String,Object>)pathMap.get(pathName);	// ex) 	paths 	/pets  	get:
						
						log.info(spaceBar + spaceBar + pathName);
						
						ArrayList tags = (ArrayList) getStringVectorToString(pathInfoMap.get("tags"));
						log.info(spaceBar + spaceBar + spaceBar + "tags:" + pathInfoMap.get("tags") );	// [string]
						
						log.info(spaceBar + spaceBar + spaceBar + "summary:" + pathInfoMap.get("summary") );	// string
						log.info(spaceBar + spaceBar + spaceBar + "description:" + pathInfoMap.get("description") );	// string
						log.info(spaceBar + spaceBar + spaceBar + "externalDocs:" + pathInfoMap.get("externalDocs") ); // 없음. External Documentation Object
						log.info(spaceBar + spaceBar + spaceBar + "operationId:" + pathInfoMap.get("operationId") );	// 

						log.info(spaceBar + spaceBar + spaceBar + "consumes:" );
						ArrayList consumes = (ArrayList) getStringVectorToString(pathInfoMap.get("consumes"));	// [string]
						if(consumes!=null) {
							for(int ii = 0 ; ii < consumes.size(); ii ++ ) {
								log.info(spaceBar + spaceBar + spaceBar + spaceBar +"- " + consumes.get(ii) );
							}
						}
					
						log.info(spaceBar + spaceBar + spaceBar + "produces:" );
						ArrayList produces = (ArrayList) getStringVectorToString(pathInfoMap.get("produces"));	// [string]
						if(produces!=null) {
							for(int ii = 0 ; ii < produces.size(); ii ++ ) {
								log.info(spaceBar + spaceBar + spaceBar + spaceBar +"- " + produces.get(ii) );
							}
						}
						
						log.info(spaceBar + spaceBar + spaceBar + "parameters:" );
						ArrayList parameters = (ArrayList) getStringVectorToString(pathInfoMap.get("parameters"));	// Required // Parameter Object | 
						if(parameters!=null) {
							for(int ii = 0 ; ii < parameters.size(); ii ++ ) {
								HashMap<String,Object> paramMap = (HashMap<String,Object>)parameters.get(ii);
								ArrayList parametersKeyList = getMapKeyReuturnArrayList(paramMap,null);
								for(int k=0 ; k< parametersKeyList.size() ; k++) {
									log.info(spaceBar + spaceBar + spaceBar + spaceBar +"" + parametersKeyList.get(k) + ": "  + paramMap.get(parametersKeyList.get(k)));
									if((paramMap.get(parametersKeyList.get(k))).equals("schema")) { // !!! 
										
									}
								}
							}
						}
						
						log.info(spaceBar + spaceBar + spaceBar + "responses:");  /** responses 는 존재 . 데이터 없으면 에러 **/
						ArrayList responses = (ArrayList) getStringVectorToString(pathInfoMap.get("responses"));	// Responses Object
						
						/** schema , headers , examples 는 존재 . 데이터 없으면 에러 **/
						String[] responseObjStr = { "description" , "schema" , "headers" , "examples" };
						if(responses!=null) {
							for(int ii = 0 ; ii < responses.size(); ii ++ ) {
								String paramStr = (String)responses.get(ii);
								log.info(spaceBar + spaceBar + spaceBar + spaceBar +"" + paramStr );
								HashMap<String,Object> responsesInfoMap = (HashMap<String,Object>)pathInfoMap.get("responses");
								HashMap<String,Object> responsesInfo2Map = (HashMap<String,Object>) responsesInfoMap.get(paramStr);
								
								for(String str : responseObjStr) {
									if(!str.equals("description")) {
										HashMap<String,Object> responsesInfo3Map = (HashMap<String,Object>) responsesInfo2Map.get(str);
										if(str.equals("headers")) {
											if(responsesInfo3Map!=null) {
												log.info(spaceBar5 + str + ":");
												//log.info(spaceBar6 + "responsesInfo3Map= " + responsesInfo3Map + " :");
												// key 가져오기
												ArrayList<String> strKeyList = (ArrayList<String>)getMapKeyReuturnArrayList( responsesInfo3Map , null );
												for(String keyStr : strKeyList) {
													log.info(spaceBar6 + keyStr + ":");
													
													HashMap<String,Object> headerResMap = (HashMap<String,Object>)responsesInfo3Map.get(keyStr);
													
													log.info(spaceBar7  + "description:" + headerResMap.get("description"));
													log.info(spaceBar7  + "type:" + headerResMap.get("type"));
												}
											}
										}else if(str.equals("examples")) {
											if(responsesInfo3Map!=null) {
												log.info(spaceBar5 + str + ":");
												//log.info(spaceBar6 + "responsesInfo3Map= " + responsesInfo3Map + " :");
												// key 가져오기
												ArrayList<String> strKeyList = (ArrayList<String>)getMapKeyReuturnArrayList( responsesInfo3Map , null );
												for(String keyStr : strKeyList) {
													log.info(spaceBar6 + keyStr + ":");
													
													HashMap<String,Object> headerResMap = (HashMap<String,Object>)responsesInfo3Map.get(keyStr);
													
													log.info(spaceBar7  + "name:" + headerResMap.get("name"));
													log.info(spaceBar7  + "type:" + headerResMap.get("type"));
													log.info(spaceBar7  + "color:" + headerResMap.get("color"));
													log.info(spaceBar7  + "gender:" + headerResMap.get("gender"));
													log.info(spaceBar7  + "breed:" + headerResMap.get("breed"));
													
												}
											}
										}
									}else {
										log.info(spaceBar5 + str + " :" + responsesInfo2Map.get(str));
									}
								}
							}
						}
						
						ArrayList schemes = (ArrayList) getStringVectorToString(pathInfoMap.get("schemes"));	// [string]
						if(schemes!=null) {
							for(int ii = 0 ; ii < schemes.size(); ii ++ ) {
									log.info(spaceBar + spaceBar + spaceBar + spaceBar +"- " + schemes.get(ii) );
							}
						}
						
						log.info(spaceBar + spaceBar + spaceBar + "deprecated:" + pathInfoMap.get("deprecated") ); // boolean
						
						/** Security Requirement Object 에 대한 고민이 필요.*/
						ArrayList security = getStringVectorToString(pathInfoMap.get("security"));	// Security Requirement Object]
						log.info(spaceBar + spaceBar + spaceBar + "security:");
						if(security!=null) {
							for(int ii = 0 ; ii < security.size(); ii ++ ) {
								HashMap<String,Object> hMap = (HashMap<String,Object>)security.get(ii);
								ArrayList jjList = getMapKeyReuturnArrayList(hMap,null);
								for(int jj = 0 ; jj < jjList.size(); jj ++ ) {
									log.info(spaceBar4 +"- " +  jjList.get(jj));
									ArrayList kkList = (ArrayList)hMap.get(jjList.get(jj));
									for(int kk = 0 ; kk < kkList.size() ; kk++) {
										log.info(spaceBar5 + "- " +  kkList.get(kk));
									}
								}
							}
						} // end : if(security!=null) {
						
					} // end : for(int j=0 ; j < pathList.size() ; j++) {
					
				} // end : for(int i=0 ; i < pathsList.size() ; i++) {
				 
			} // end : if(pathsList!=null) {
			
			/** securityDefinitions start */
			log.info("securityDefinitions:");
			HashMap<String,Object> securityDefinitionsMap = (HashMap<String,Object>)jsonResult.get("securityDefinitions");
			ArrayList<String> securityDefinitionsNameList = getMapKeyReuturnArrayList(securityDefinitionsMap,null);
			for(String str : securityDefinitionsNameList) {
				log.info(spaceBar + str+ ":");
				HashMap<String,Object> securDetailMap = (HashMap<String,Object>) securityDefinitionsMap.get(str);
				ArrayList<String> securDetailNameList = getMapKeyReuturnArrayList(securDetailMap,null);
				for(String str2 : securDetailNameList) {
					Object obj = (Object)securDetailMap.get(str2);
					if(obj instanceof String) {
						log.info(spaceBar + spaceBar + str2+ ":" + obj);
					}else if(obj instanceof java.util.LinkedHashMap ) {
						HashMap objHMap= (HashMap<String,Object>)obj;
						log.info(spaceBar + spaceBar + str2+ ":");
						ArrayList<String> objNameList = getMapKeyReuturnArrayList(objHMap,null);
						for(String str3 : objNameList) {
							log.info(spaceBar + spaceBar + spaceBar + str3+ ":" + objHMap.get(str3));
							Object obj3 = (Object)securDetailMap.get(str3);
							if(obj3 instanceof String) {
								log.info(spaceBar4 + str3+ ":" + obj);
							}else if(obj3 instanceof java.util.LinkedHashMap ) {
								HashMap obj3HMap= (HashMap<String,Object>)obj3;
								ArrayList<String> obj3HMapNameList = getMapKeyReuturnArrayList(obj3HMap,null);
								for(String str4 : obj3HMapNameList) {
									log.info(spaceBar4 + str4+ ":" + obj3HMap.get(str4) + obj3HMap);
								}
							}
						}
					}
				}
			}
//			log.info("securityDefinitions:");
//			HashMap<String,Object> securityDefinitionsHmap = (HashMap<String,Object>)jsonResult.get("securityDefinitions");
//			getFactorilMap(securityDefinitionsHmap,null,0);
			
			/** securityDefinitions end */
			
			/** definitions START */
			log.info("definitions:");
			HashMap<String,Object> definitionsMap = (HashMap<String,Object>)jsonResult.get("definitions");
			ArrayList<String> definitionsNameList = getMapKeyReuturnArrayList(definitionsMap,null);
			for(String definStr : definitionsNameList) {
				log.info(spaceBar + spaceBar + definStr + ":" );
				
				HashMap<String,Object> definInfoMap = (HashMap<String,Object>)definitionsMap.get(definStr);
				
				log.info(spaceBar + spaceBar + spaceBar + "type:"  + definInfoMap.get("type") );
				log.info(spaceBar + spaceBar + spaceBar + "properties:");
				
				HashMap<String,Object> properHmap = (HashMap<String,Object>)definInfoMap.get("properties");
				
				ArrayList<String> propertiesNameList = getMapKeyReuturnArrayList(properHmap,null);
				for(String properStr : propertiesNameList) {
					log.info(spaceBar4 + properStr + ":");
					HashMap<String,Object> properDetailHmap = (HashMap<String,Object>)properHmap.get(properStr);
					log.info(spaceBar5 + "type:" + properDetailHmap.get("type") );
					log.info(spaceBar5 + "format:" + properDetailHmap.get("format") );
					log.info(spaceBar5 + "description" + ":" + properDetailHmap.get("description") );
					log.info(spaceBar5 + "title" + ":" + properDetailHmap.get("title") );
				}
			}
			
    } catch (FileNotFoundException e) {
      //-- [2023:codeeyes][empty_block issue]
			//e.printStackTrace();
		} catch (Exception e) {
			//-- [2023:codeeyes][empty_block issue]
			//e.printStackTrace();
		}
      return resultMap;
  }
	
	private static HashMap<String,Object> setJavaToYaml(Object obj){
		HashMap<String,Object> resultHmap = new HashMap<String,Object>();
		
		// DB 에서 조회
		
		// 
		
		return resultHmap;
	}


	private static HashMap<String,Object> setYamlBasic(Object obj){
		HashMap<String,Object> resultHmap = new HashMap<String,Object>();
		resultHmap.put("swagger","");
		resultHmap.put("host", "");
		resultHmap.put("basePath","");
		resultHmap.put("schemes",""); // String[]
		// DB 에서 조회
		
		// 
		return resultHmap;
	}
	
	private static HashMap<String,Object> setYamlInfo(Object obj){
		HashMap<String,Object> resultHmap = new HashMap<String,Object>();
		resultHmap.put("description","");
		resultHmap.put("version", "");
		resultHmap.put("title","");
		resultHmap.put("termsOfService","");
		resultHmap.put("contact","");
		return resultHmap;
	}
	
	/**
	* <pre>
	* 1. 메소드명 : getFactorilMap
	* 2. 작성일 : 2017. 11. 15. 오전 11:01:00
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 맵의 하위의 데이터 파싱
	* </pre>
	* @param targetMap
	* @param targetName
	* @param tabInt
	*/
	public static void getFactorilMap(HashMap targetMap, String targetName , Integer tabInt) {
		String spaceBar = "  ";
		for(int i=0;i < tabInt;i++) {
			spaceBar=spaceBar+"  ";
		}
		HashMap<String,Object> targetHMap;
		if(targetName!=null) {
			targetHMap 	= (HashMap<String,Object>)targetMap.get(targetName);
		}else {
			targetHMap = targetMap;
		}
		
		ArrayList<String> targetNameList 	= getMapKeyReuturnArrayList(targetHMap,null);
		
		for(String strName : targetNameList ) {
			
			Object strObj = targetHMap.get(strName);
			
			if(strObj instanceof String) {
				log.info(spaceBar + strName+ ":" + strObj);
			}else if(strObj instanceof java.util.LinkedHashMap ) {
				log.info(spaceBar + strName+ ":");
				getFactorilMap((HashMap<String,Object>)strObj, null , tabInt+1);
			}else {
				String strs = "";
				for(String strVec : (ArrayList<String>)strObj) {
					strs += strVec+ ", ";
				}
				log.info(spaceBar + strName + ": [ " + strs + " ]");
			}
		}
	}
	
	
	/**
	* <pre>
	* 1. 메소드명 : getMapKeyReuturnArrayList
	* 2. 작성일 : 2017. 11. 13. 오후 2:16:26
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 멥의 키값을 가져와서 ArrayList 로 반환
	* </pre>
	* @param targetMap
	* @param targetStr
	* @return
	*/
	public static ArrayList getMapKeyReuturnArrayList(HashMap<String,Object> targetMap, String targetStr) {
		ArrayList alist = new ArrayList();
		Set set = null;
		if(targetStr==null) {
			set = targetMap.keySet();
		}else {
			set = ((HashMap<String,Object>)targetMap.get(targetStr)).keySet();
		}
		Iterator iterator = set.iterator();
		while(iterator.hasNext()){
		  String key = (String)iterator.next();
		  alist.add(key);
		}
		return alist;
	}
	
	
	/**
	* <pre>
	* 1. 메소드명 : getStringVectorToString
	* 2. 작성일 : 2017. 11. 13. 오후 3:14:26
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : String[] or MAP or ArrayList 의 내용을 ArrayList로 반환. 
	* </pre>
	* @param targetStr
	*/
	public static ArrayList getStringVectorToString(Object targetStr) {
		ArrayList returnArrayList 	= new ArrayList();
		if(targetStr!=null) {
			if(targetStr instanceof java.util.LinkedHashMap) {
				returnArrayList = getMapKeyReuturnArrayList((HashMap<String,Object>)targetStr, null);
			}
			
			if(targetStr instanceof java.util.ArrayList<?> tStr) {
				for(int i=0; i< tStr.size() ; i++) {
					returnArrayList.add(tStr.get(i));
				}
			}
			if(targetStr instanceof String[] tStr) {
				for(int i=0; i< tStr.length ; i++) {
					returnArrayList.add(tStr[i]);
				}
			}
		}else {
			returnArrayList = null;
		}
		return returnArrayList;
	}
	
	public static ApiRegVO getYamlToInfoData(String yamlStr) throws Exception{
		
		ApiRegVO apiRegVO = new ApiRegVO();
		
		try {
			
			ObjectMapper yamlMapper = Yaml.mapper();
			JsonNode rootNode = yamlMapper.readTree(yamlStr);
			
			log.info("rootNode=" + rootNode);
			
			// must have swagger node set
			JsonNode swaggerNode = rootNode.get("swagger");
			
			log.info("swagger=" + rootNode.get("swagger"));
			log.info("host:" 	+ rootNode.get("host"));
			log.info("basePath:"+ rootNode.get("basePath"));
			
			JsonNode infoNode = rootNode.get("info");
			
			log.info("info.version=" + infoNode.get("version"));
			log.info("info.title=" + infoNode.get("title"));
			log.info("info.description=" + infoNode.get("description"));
			log.info("info.termsOfService=" + infoNode.get("termsOfService"));
			
			apiRegVO.setVer(removeDupQut((infoNode.get("version")).toString()));
			apiRegVO.setApiNm(removeDupQut((infoNode.get("title")).toString()));
			
			if(infoNode.get("description")!=null) {
				apiRegVO.setApiDesc(removeDupQut((infoNode.get("description")).toString()));
			}
			if(rootNode.get("host")!=null) {
				apiRegVO.setHost(removeDupQut((rootNode.get("host")).toString())); 
			}
			if(rootNode.get("basePath")!=null) {
				apiRegVO.setBasPath(removeDupQut((rootNode.get("basePath")).toString()));
			}
			
		}catch(Exception e) {
//			e.printStackTrace();
			throw e;
		}
		
		return apiRegVO;
	}
	
	
	
	/**
	* <pre>
	* 1. 메소드명 : getYamlToPathParam
	* 2. 작성일 : 2017. 12. 4. 오후 10:53:17
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 :
	* </pre>
	* @param yamlStr
	* @return
	* @throws Exception
	*/
	public static ApiRegVO getYamlToPathParam(String yamlStr) throws Exception{
		
		log.info("getYamlToPathParam ===============================================>");
		log.info("yamlStr ==========>" + yamlStr);
		String yamlStrs = "";
		
		//log.info("yamlStrs ==========>" + yamlStrs);
		
		yamlStr = yamlStrs;
		ApiRegVO apiRegVO = new ApiRegVO();
		
		try {
			
			ObjectMapper yamlMapper = Yaml.mapper();
			JsonNode rootNode = yamlMapper.readTree(yamlStr);
			
			//log.info("rootNode=" + rootNode);
			
			// must have swagger node set
			JsonNode swaggerNode = rootNode.get("swagger");
			
			log.info("swagger=" + rootNode.get("swagger"));
			log.info("host:" 	+ rootNode.get("host"));
			log.info("basePath:"+ rootNode.get("basePath"));
			
			JsonNode infoNode = rootNode.get("info");
			
			log.info("info.version=" + infoNode.get("version"));
			log.info("info.title=" + infoNode.get("title"));
			log.info("info.description=" + infoNode.get("description"));
			log.info("info.termsOfService=" + infoNode.get("termsOfService"));
			
			apiRegVO.setVer(removeDupQut((infoNode.get("version")).toString()));
			apiRegVO.setApiNm(removeDupQut((infoNode.get("title")).toString()));
			
			if(infoNode.get("description")!=null) {
				apiRegVO.setApiDesc(removeDupQut((infoNode.get("description")).toString()));
			}
			if(rootNode.get("host")!=null) {
				apiRegVO.setHost(removeDupQut((rootNode.get("host")).toString())); 
			}
			if(rootNode.get("basePath")!=null) {
				apiRegVO.setBasPath(removeDupQut((rootNode.get("basePath")).toString()));
			}
			
			ObjectMapper mapper = new ObjectMapper();
			HashMap<String,Object> hMap = new HashMap<String,Object>();
			
			log.info("rootNode.toString()=" + rootNode.toString());
			// TODO error: incompatible types: inference variable T has incompatible bounds
			/*
			* new TypeReference<HashMap<String, String>>() -> new TypeReference<HashMap<String, Object>>()
			* type을 HashMap<String, Object> 라고 가정함
			* 반드시 비즈니스 로직 체크 후 적용 필요
			* */
			hMap = mapper.readValue(rootNode.toString(), new TypeReference<HashMap<String, Object>>() {});
			
			log.info("peths:");
			HashMap<String,Object> pethsMap = (HashMap<String,Object>)hMap.get("peths");
			getFactorilMap(pethsMap,null,0);
			
			log.info("hMap=====================>" + hMap);
			
		}catch(Exception e) {
//			e.printStackTrace();
			throw e;
		}
		
		return apiRegVO;
	}
	
	public static String removeDupQut(String str) {
		//log.info("Start removeDupQut=====================>" + str);
		if(!str.equals("")) {
			String strFirst = str.charAt(0)+"";
			String strLast 	= str.charAt(str.length()-1)+"";
			
			//log.info("Start strFirst=>" + strFirst + "  strLast=>"+strLast);
			//log.info("Start strFirst=>" + strFirst.indexOf("\"") + "  strLast=>"+strLast.indexOf("\""));
			if(strFirst.indexOf("\"") > -1 && strLast.indexOf("\"") > -1) {
				str = str.substring(1, str.length()-1);
			}
		}else {
			str = "";
		}
		//log.info("End removeDupQut=====================>" + str);
		return str;
	}
}

