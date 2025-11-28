package com.Dolkara.api_content_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.StringJoiner;


@Configuration
public class CacheKeyConfig {

    private final ObjectMapper objectMapper;

    public CacheKeyConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean("keyGenerator")
    public KeyGenerator keyGenerator() {

        return (Object target, Method method, Object... params) -> {

            StringJoiner key = new StringJoiner("_", method.getName() + "_", "");

            if (params.length == 0) return key.add("no_params").toString();

            for(Object param : params) {
                if(param == null) key.add("null");
                else key.add(toJson(param));
            }

            return key.toString();
        };
    }

    private String toJson(Object param) {

        try {
            return objectMapper.writeValueAsString(param);
        }
        catch (Exception e) {
            return param.toString();
        }
    }
}
