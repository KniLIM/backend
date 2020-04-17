package com.knilim.session.config;

import com.knilim.data.utils.Device;
import com.knilim.session.model.Client;
import com.knilim.data.utils.FastJsonSerializer;
import com.knilim.session.model.Connect;
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

import java.util.HashMap;

@Configuration
public class LocalRedisConfiguration {

    @Bean("localRedisPoolConfig")
    @ConfigurationProperties(prefix = "spring.redis.lettuce.pool")
    public GenericObjectPoolConfig genericObjectPoolConfig() {
        return new GenericObjectPoolConfig();
    }

    @Bean("localRedisConfiguration")
    @ConfigurationProperties(prefix = "spring.redis")
    public RedisStandaloneConfiguration localRedisConfiguration() {
        return new RedisStandaloneConfiguration();
    }

    @Bean("localRedisFactory")
    public LettuceConnectionFactory localRedisFactory(
            @Qualifier("localRedisPoolConfig") GenericObjectPoolConfig poolConfig,
            RedisStandaloneConfiguration redisConfig) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig).build();
        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
    }


    @Bean("localClientRedisTemplate")
    public RedisTemplate<String, HashMap<Device, Connect>> localClientRedisTemplate(
            @Qualifier("localRedisFactory") RedisConnectionFactory factory) {
        RedisTemplate<String, HashMap<Device,Connect>> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new FastJsonSerializer<>(Connect.class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new FastJsonSerializer<>(Connect.class));
        template.afterPropertiesSet();
        return template;
    }
}
