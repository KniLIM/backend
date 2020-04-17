package com.knilim.online.config;

import com.knilim.data.model.DeviceInfo;
import com.knilim.data.utils.Device;
import com.knilim.data.utils.FastJsonSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashMap;

@Configuration
public class OnlineRedisConfiguration {

    @Bean
    public RedisTemplate<String, HashMap<Device, DeviceInfo>> template(RedisConnectionFactory factory) {
        RedisTemplate<String, HashMap<Device, DeviceInfo>> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new FastJsonSerializer<>(DeviceInfo.class));
        template.setHashKeySerializer(new FastJsonSerializer<>(Device.class));
        template.setHashValueSerializer(new FastJsonSerializer<>(DeviceInfo.class));
        template.afterPropertiesSet();
        return template;
    }
}
