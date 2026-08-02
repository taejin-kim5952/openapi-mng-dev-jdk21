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

import java.util.List;


/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.util
 * 2. 타입명   : YamlTransform.java
 * 3. 작성일   : 2017. 11. 10. 오후 4:36:01
 * 4. 작성자   : JeonGeun Kang
 * 5. 설명     : This interface describes a transformation we would like to 
 * 				 use tags to affect in the Yaml <--> translation process. 
 * 				 <p> 
 * 				 One anticipated use case of this will be to allow users to adopt 
 * 				 a shorthand to avoid learning fully qualified class named for plugins: 
 * </pre>
 */
public interface YamlTransform {
	/** @return the {@code "!foo"} literal we expect to appear in YAML */
	String getTag();

	/** @return the set of classes for which we are exposing a shorthand */
	List<Class> getClasses();

	/**
	 * This takes the argument supplied to the tag (e.g. {@code !foo bar}) and
	 * determines to what class that translates.
	 * <p>
	 * NOTE: This is the dual of {@link #represent(Class)}
	 */
	String construct(String value);

	/**
	 * This take the class we are restoring to Yaml and determines the appropriate
	 * argument for our tag.
	 * <p>
	 * NOTE: This is the dual of {@link #construct(String)}
	 */
	String represent(Class clazz);
}
