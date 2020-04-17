package com.knilim.offline.config;

import com.knilim.data.utils.FastJsonSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class OfflineRedisConfiguration {

    @Bean
    public RedisTemplate<String, Byte[]> offlineTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new FastJsonSerializer<>(Byte[].class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new FastJsonSerializer<>(Byte[].class));
        template.afterPropertiesSet();
        return template;
    }
}
