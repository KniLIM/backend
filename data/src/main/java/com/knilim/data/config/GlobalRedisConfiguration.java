package com.knilim.data.config;

import com.knilim.data.model.DeviceInfo;
import com.knilim.data.utils.Device;
import com.knilim.data.utils.FastJsonSerializer;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Priority;
import java.util.HashMap;

@Configuration
public class GlobalRedisConfiguration {

    @Bean("globalRedisPoolConfig")
    @ConfigurationProperties(prefix = "spring.redis.lettuce.pool")
    public GenericObjectPoolConfig poolConfig() {
        return new GenericObjectPoolConfig();
    }

    @Bean("onlineRedisConfig")
    @ConfigurationProperties(prefix = "spring.redis")
    public RedisStandaloneConfiguration onlineRedisConfig() {
        return new RedisStandaloneConfiguration();
    }

    @Bean("offlineRedisConfig")
    @ConfigurationProperties(prefix = "spring.redis3")
    public RedisStandaloneConfiguration offlineRedisConfig() {
        return new RedisStandaloneConfiguration();
    }

    @Bean("onlineRedisFactory")
    @Primary
    public LettuceConnectionFactory onlineRedisFactory(
            GenericObjectPoolConfig globalRedisPoolConfig, RedisStandaloneConfiguration onlineRedisConfig) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .poolConfig(globalRedisPoolConfig).build();
        return new LettuceConnectionFactory(onlineRedisConfig, clientConfiguration);
    }

    @Bean("offlineRedisFactory")
    public LettuceConnectionFactory offlineRedisFactory(
            GenericObjectPoolConfig globalRedisPoolConfig, RedisStandaloneConfiguration offlineRedisConfig) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .poolConfig(globalRedisPoolConfig).build();
        return new LettuceConnectionFactory(offlineRedisConfig, clientConfiguration);
    }

    @Bean("onlineTemplate")
    public RedisTemplate<String, HashMap<Device, DeviceInfo>> onlineTemplate(RedisConnectionFactory onlineRedisFactory) {
        RedisTemplate<String, HashMap<Device, DeviceInfo>> template = new RedisTemplate<>();
        template.setConnectionFactory(onlineRedisFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new FastJsonSerializer<>(DeviceInfo.class));
        template.setHashKeySerializer(new FastJsonSerializer<>(Device.class));
        template.setHashValueSerializer(new FastJsonSerializer<>(DeviceInfo.class));
        template.afterPropertiesSet();
        return template;
    }

    @Bean("offlineTemplate")
    public RedisTemplate<String, Byte[]> offlineTemplate(RedisConnectionFactory offlineRedisFactory) {
        RedisTemplate<String, Byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(offlineRedisFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new FastJsonSerializer<>(Byte[].class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new FastJsonSerializer<>(Byte[].class));
        template.afterPropertiesSet();
        return template;
    }
}
