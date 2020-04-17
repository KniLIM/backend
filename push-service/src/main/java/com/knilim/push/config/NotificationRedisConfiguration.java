package com.knilim.push.config;

import com.knilim.data.model.Notification;
import com.knilim.data.utils.FastJsonSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class NotificationRedisConfiguration {

    @Bean
    public RedisTemplate<String, Notification> template(RedisConnectionFactory factory) {
        RedisTemplate<String, Notification> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new FastJsonSerializer<>(Notification.class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new FastJsonSerializer<>(Notification.class));
        template.afterPropertiesSet();
        return template;
    }
}
