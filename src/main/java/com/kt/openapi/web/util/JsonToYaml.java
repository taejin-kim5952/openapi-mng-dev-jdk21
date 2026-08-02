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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.emitter.ScalarAnalysis;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.util
 * 2. 타입명   : JsonToYaml.java
 * 3. 작성일   : 2017. 11. 9. 오후 7:38:09
 * 4. 작성자   : JeonGeun Kang
 * 5. 설명     : json에서 yaml로 변환
 * </pre>
 */
public abstract class JsonToYaml {
	/**
	 * This method is the workhorse method that implementations provide for
	 * translating json into yaml.
	 */
	public abstract String toYaml(InputStream inputStream);

	/**
	 * @see #toYaml(InputStream)
	 */
	public final String toYaml(String input) {
		return toYaml(new ByteArrayInputStream(input.getBytes(UTF_8)));
	}

	/**
	 * A basic implementation that parses and dumps the yaml using Snake Yaml.
	 */
	public static class Default extends JsonToYaml {

		public Default() {
			this(ImmutableList.<YamlTransform>of());
		}

		public Default(List<YamlTransform> transforms) {
			this.transforms = checkNotNull(transforms);
		}

		/** {@inheritDoc} */
		@Override
		public String toYaml(InputStream inputStream) {

			DumperOptions options = new DumperOptions() {
				/** Force usage of PLAIN style */
				public DumperOptions.ScalarStyle calculateScalarStyle(ScalarAnalysis analysis,
						DumperOptions.ScalarStyle style) {
					return DumperOptions.ScalarStyle.PLAIN;
				}
			};

			options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
			// NOTE: This is inadequate, thus the above hack.
			options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);

			final Yaml yaml = new Yaml(new CustomRepresenter(options, transforms), options);

			return yaml.dump(yaml.load(inputStream));
		}

		private final List<YamlTransform> transforms;
	}

	public static class CustomRepresenter extends Representer {
		public CustomRepresenter(DumperOptions options, List<YamlTransform> transforms) {
			super(options);
			this.representers.put(String.class, new RepresentTransforms(transforms));
		}

		public class RepresentTransforms implements Represent {
			public RepresentTransforms(final List<YamlTransform> inputTransforms) {
				final Map<String, YamlTransform> transforms = Maps.newHashMap();
				final Map<String, Class> classes = Maps.newHashMap();

				for (YamlTransform xform : inputTransforms) {
					for (Class clazz : xform.getClasses()) {
						final String text = xform.construct(xform.represent(clazz));
						// The first transform "wins"
						if (classes.containsKey(text)) {
							continue;
						}
						transforms.put(text, xform);
						classes.put(text, clazz);
					}
				}

				// Store to the final fields as an immutable version
				this.transforms = Collections.unmodifiableMap(transforms);
				this.classes = Collections.unmodifiableMap(classes);
			}

			public Node representData(Object data) {
				final String element = (String) data;
				if (transforms.containsKey(element)) {
					final YamlTransform xform = transforms.get(element);
					final Class clazz = classes.get(element);
					return representScalar(new Tag(xform.getTag()), xform.represent(clazz));
				}
				return representScalar(Tag.STR, element);
			}

			private final Map<String, YamlTransform> transforms;
			private final Map<String, Class> classes;
		}
	}

	public static String Default() {
		// TODO Auto-generated method stub
		return null;
	}
}
