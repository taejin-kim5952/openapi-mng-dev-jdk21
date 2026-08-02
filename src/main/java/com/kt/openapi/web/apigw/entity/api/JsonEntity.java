//-- [tag:SR-20210222][i][mpybe_not_used]
package com.kt.openapi.web.apigw.entity.api;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = 6633056478693437208L;
    private String name;
    private Map<String, String> properties = new HashMap<>();
    private Map<String, Object> param;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonAnyGetter
    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @JsonAnySetter
    public void add(String key, String value) {
        properties.put(key, value);
    }


    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        JsonEntity entity = new JsonEntity();
        entity.setName("test");

        Map<String, String> map = new HashMap<>();
        map.put("attr1", "val1");
        map.put("attr2", "val2");
        entity.setProperties(map);

        Map<String, Object> params = new HashMap<>();
        params.put("a", "string");
        params.put("array", Arrays.asList("1", "2", "3"));
        entity.setParam(params);
        String json = mapper.writeValueAsString(entity);
//        System.out.println(json);
        JsonEntity entity1 = mapper.readValue(json, JsonEntity.class);
//        JsonEntity entity1 = mapper
//                .readerFor(JsonEntity.class).readValue(json);


//        System.out.println(String.format("name: %s, attr1:%s, param1: %s", entity1.getName(), entity1.getProperties().get("attr1"), entity1.getParam().get("a")));

    }
}
