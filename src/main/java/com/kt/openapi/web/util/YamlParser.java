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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import io.swagger.parser.SwaggerParser;
import io.swagger.parser.util.SwaggerDeserializationResult;
/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.util
 * 2. 타입명   : YamlParser.java
 * 3. 작성일   : 2017. 11. 10. 오전 9:41:32
 * 4. 작성자   : JeonGeun Kang
 * 5. 설명     : yaml파일 parser
 * </pre>
 */
public class YamlParser {
	
	protected static Log log = LogFactory.getLog(StringUtil.class);
	
	public static void main(String[] args) {
		String json = """
                {
                  "swagger": "2.0",
                  "basePath": "dsfsdf",
                  "paths": {
                    "/pet/findByStatus": {
                      "get": {
                        "parameters": [
                          {
                            "name": "status",
                            "in": "query",
                            "description": "Status values that need to be considered for filter",
                            "required": false,
                            "type": "array",
                            "items": {
                              "type": "string"
                            },
                            "collectionFormat": "pipes",
                            "default": "available"
                          }
                        ],
                        "responses": {
                          "200": {
                            "description": "successful operation",
                            "schema": {
                              "$ref": "#/definitions/PetArray"
                            }
                          }
                        }
                      }
                    }
                  }
                }\
                """; 
 
        SwaggerParser parser = new SwaggerParser(); 
 
        SwaggerDeserializationResult result = parser.readWithInfo(json); 
 
        Swagger swagger = result.getSwagger();
        for(int i = 0;i < swagger.getPaths().size();i++) {
        	System.out.println(swagger.getPaths());
        }
        Parameter param = swagger.getPath("/pet/findByStatus").getGet().getParameters().get(0); 
 
        assertTrue(param instanceof QueryParameter); 
        QueryParameter qp = (QueryParameter) param; 
        Property p = qp.getItems(); 
 
        assertEquals(qp.getType(), "array"); 
        assertTrue(p instanceof StringProperty); 
	}
}