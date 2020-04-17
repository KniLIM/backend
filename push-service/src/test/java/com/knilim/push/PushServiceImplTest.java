package com.knilim.push;

import com.knilim.data.model.Notification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Test OnlineServiceImpl")
@ExtendWith(SpringExtension.class)
class PushServiceImplTest {

    @Resource
    private PushServiceImpl service;

    @Resource
    private RedisTemplate<String, Notification> template;

    @AfterEach
    void cleanDataSource() {
        Set<String> keys = template.keys("*");
        if (keys != null) {
            template.delete(keys);
        }
    }

    @Test
    void addNotification() {
    }

    @Test
    void testAddNotification() {
    }

    @Test
    void getOfflineNotificationByUserId() {
    }
}