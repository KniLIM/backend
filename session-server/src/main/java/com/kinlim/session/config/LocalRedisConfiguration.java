package com.kinlim.session.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.kinlim.session.model.Client;
import com.kinlim.session.model.Connect;
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
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Configuration
public class LocalRedisConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.redis.lettuce.pool")
    public GenericObjectPoolConfig genericObjectPoolConfig() {
        return new GenericObjectPoolConfig();
    }

    @Bean("localConnectRedisConfiguration")
    @ConfigurationProperties(prefix = "spring.redis")
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
            GenericObjectPoolConfig poolConfig,
            @Qualifier("localConnectRedisConfiguration") RedisStandaloneConfiguration redisConfig) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig).build();
        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
    }

    @Bean("localClientRedisFactory")
    public LettuceConnectionFactory localClientRedisFactory(
            GenericObjectPoolConfig poolConfig,
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

class FastJsonSerializer<T> implements RedisSerializer<T> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Class<T> clazz;

    static {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
    }

    public FastJsonSerializer(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(T item) throws SerializationException {
        if (item == null) {
            return new byte[0];
        }

        return JSON.toJSONString(item).getBytes(DEFAULT_CHARSET);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length < 1) {
            return null;
        }

        return JSON.parseObject(new String(bytes, DEFAULT_CHARSET), clazz);
    }
}
