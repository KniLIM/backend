package com.knilim.session.config;

import com.knilim.model.utils.FastJsonSerializer;
import com.knilim.session.model.Client;
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

@Configuration
public class LocalRedisConfiguration {

    @Bean("localRedisPoolConfig")
    @ConfigurationProperties(prefix = "spring.redis1.lettuce.pool")
    public GenericObjectPoolConfig genericObjectPoolConfig() {
        return new GenericObjectPoolConfig();
    }

    @Bean("localConnectRedisConfiguration")
    @ConfigurationProperties(prefix = "spring.redis1")
    public RedisStandaloneConfiguration localConnectRedisConfiguration() {
        return new RedisStandaloneConfiguration();
    }

    @Bean("localClientRedisConfiguration")
    @ConfigurationProperties(prefix = "spring.redis2")
    public RedisStandaloneConfiguration localClientRedisConfiguration() {
        return new RedisStandaloneConfiguration();
    }

    @Bean("localConnectRedisFactory")
    public LettuceConnectionFactory localConnectRedisFactory(
            @Qualifier("localRedisPoolConfig") GenericObjectPoolConfig poolConfig,
            @Qualifier("localConnectRedisConfiguration") RedisStandaloneConfiguration redisConfig) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig).build();
        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
    }

    @Bean("localClientRedisFactory")
    public LettuceConnectionFactory localClientRedisFactory(
            @Qualifier("localRedisPoolConfig") GenericObjectPoolConfig poolConfig,
            @Qualifier("localClientRedisConfiguration") RedisStandaloneConfiguration redisConfig) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig).build();
        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
    }

    @Bean("localConnectRedisTemplate")
    public RedisTemplate<String, Connect> localConnectRedisTemplate(
            @Qualifier("localConnectRedisFactory") RedisConnectionFactory factory) {
        RedisTemplate<String, Connect> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new FastJsonSerializer<>(Connect.class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new FastJsonSerializer<>(Connect.class));
        template.afterPropertiesSet();
        return template;
    }

    @Bean("localClientRedisTemplate")
    public RedisTemplate<String, Client> localClientRedisTemplate(
            @Qualifier("localClientRedisFactory") RedisConnectionFactory factory) {
        RedisTemplate<String, Client> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new FastJsonSerializer<>(Client.class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new FastJsonSerializer<>(Client.class));
        template.afterPropertiesSet();
        return template;
    }
}
