package com.knilim.data.config;

import com.knilim.data.model.Online;
import com.knilim.data.utils.FastJsonSerializer;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class GlobalRedisConfiguration {

    @Bean("globalOnlineRedisPoolConfig")
    @ConfigurationProperties(prefix = "spring.redis.lettuce.pool")
    public GenericObjectPoolConfig genericObjectPoolConfig() {
        return new GenericObjectPoolConfig();
    }

    @Bean("globalOnlineRedisConfiguration")
    @ConfigurationProperties(prefix = "spring.redis")
    public RedisStandaloneConfiguration globalRedisConfiguration() {
        return new RedisStandaloneConfiguration();
    }

    @Bean("globalOnlineRedisConnectionFactory")
    public LettuceConnectionFactory globalRedisFactory(
            @Qualifier("globalOnlineRedisPoolConfig") GenericObjectPoolConfig poolConfig,
            @Qualifier("globalOnlineRedisConfiguration") RedisStandaloneConfiguration redisConfig) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig).build();
        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
    }

    @Bean("globalOnlineRedisTemplate")
    public RedisTemplate<String, Online> localConnectRedisTemplate(
            @Qualifier("globalOnlineRedisConnectionFactory") RedisConnectionFactory factory) {
        RedisTemplate<String, Online> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new FastJsonSerializer<>(Online.class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new FastJsonSerializer<>(Online.class));
        template.afterPropertiesSet();
        return template;
    }
}