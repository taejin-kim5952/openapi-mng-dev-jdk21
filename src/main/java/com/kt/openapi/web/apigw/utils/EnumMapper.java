package com.kt.openapi.web.apigw.utils;



import com.kt.openapi.web.apigw.type.EnumModel;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EnumMapper {
    private static final Map<String, List<EnumValue>> factory = new HashMap<>();

    public static void put(EnumMapper mapper, Class<? extends EnumModel> e) {
        mapper.put(e.getSimpleName(), e);
    }

    private List<EnumValue> toEnumValues(Class<? extends EnumModel> e) {
        return Arrays.stream(e.getEnumConstants())
                .map(EnumValue::new)
                .collect(Collectors.toList());
    }

    public void put(String key, Class<? extends EnumModel> e) {
        factory.put(key, toEnumValues(e));
    }

    public List<EnumValue> get(String key) {
        return factory.get(key);
    }

    public List<EnumValue> get(Class<? extends EnumModel> e) {
        return this.get(e.getSimpleName());
    }

    public Map<String, List<EnumValue>> get(List<String> keys) {
        if (keys == null || keys.size() == 0) {
            return new LinkedHashMap<>();
        }

        return keys.stream()
                .collect(Collectors.toMap(Function.identity(), key -> factory.get(key)));
    }

    public Map<String, List<EnumValue>> getAll() {
        return factory;
    }
}
