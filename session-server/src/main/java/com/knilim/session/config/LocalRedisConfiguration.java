package com.knilim.session.config;

import com.knilim.data.utils.Device;
import com.knilim.data.utils.FastJsonSerializer;
import com.knilim.session.model.Connect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashMap;

@Configuration
public class LocalRedisConfiguration {

    @Bean("localClientRedisTemplate")
    public RedisTemplate<String, HashMap<Device, Connect>> localClientRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, HashMap<Device,Connect>> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new FastJsonSerializer<>(Connect.class));
        template.setHashKeySerializer(new FastJsonSerializer<>(Device.class));
        template.setHashValueSerializer(new FastJsonSerializer<>(Connect.class));
        template.afterPropertiesSet();
        return template;
    }
}
