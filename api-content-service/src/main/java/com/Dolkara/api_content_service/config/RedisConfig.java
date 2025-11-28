package com.Dolkara.api_content_service.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {

        RedisCacheConfiguration defConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeValuesWith(
                        RedisSerializationContext
                                .SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer())
                )
                .entryTtl(Duration.ofHours(2));

        Map<String, RedisCacheConfiguration> configs = new HashMap<>();
        configs.put("triviaCache", defConfig.entryTtl(Duration.ofMinutes(20)));
        configs.put("quotesCache", defConfig);
        configs.put("weatherCache", defConfig);

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defConfig)
                .withInitialCacheConfigurations(configs)
                .build();
    }
}
