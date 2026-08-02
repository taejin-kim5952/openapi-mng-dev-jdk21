package com.kt.openapi.fwk.cmm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Collection;

/**
 * 레거시 EgovPropertyService 가교(Bridge) 클래스
 * [마이그레이션] 기존 75개 파일에서 @Resource(name="propertiesService")로 사용하는 의존성을 해결
 * [보안 보완] Static 유틸리티(EgovProperties 등)에서도 접근 가능하도록 정적 참조 제공
 */
@Configuration
public class PropertyConfig {

    private static LegacyPropertyService staticService;

    /**
     * Bean이 아닌 클래스에서 정적으로 접근하기 위한 메소드
     */
    public static LegacyPropertyService getStaticService() {
        return staticService;
    }

    @Bean(name = "propertiesService")
    public LegacyPropertyService propertiesService(Environment env) {
        LegacyPropertyService service = new LegacyPropertyService(env);
        staticService = service;
        return service;
    }

    /**
     * 기존 소스와의 호환성을 위한 내부 클래스
     */
    public static class LegacyPropertyService {
        
        private final Environment env;

        public LegacyPropertyService(Environment env) {
            this.env = env;
        }

        public boolean getBoolean(String name) {
            String val = env.getProperty(name);
            return val != null && Boolean.parseBoolean(val);
        }

        public double getDouble(String name) {
            return Double.parseDouble(env.getProperty(name, "0.0"));
        }

        public float getFloat(String name) {
            return Float.parseFloat(env.getProperty(name, "0.0"));
        }

        public int getInt(String name) {
            return Integer.parseInt(env.getProperty(name, "0"));
        }

        public long getLong(String name) {
            return Long.parseLong(env.getProperty(name, "0"));
        }

        public String getString(String name) {
            return env.getProperty(name);
        }

        public String getString(String name, String def) {
            return env.getProperty(name, def);
        }

        public Collection<?> getAllKeys() {
            return null;
        }
    }
}
