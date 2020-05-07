package com.knilim.push;

import com.knilim.data.model.Notification;
import com.knilim.data.utils.NotificationType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Test OnlineServiceImpl")
@ExtendWith(SpringExtension.class)
class PushServiceImplTest {

    @Resource
    private PushServiceImpl service;

    @Resource
    private RedisTemplate<String, Notification> template;

    private final String senderId1 = "78ds43j7ds";
    private final NotificationType type1 = NotificationType.N_FRIEND_ADD_APPLICATION;
    private final String content1 = "hello world";
    private final String createAt1 = "20200503 16:45:32";
    private final String senderId2 = "7xc89d234f";
    private final NotificationType type2 = NotificationType.N_GROUP_JOIN_RESULT;
    private final String content2 = "test push service";
    private final String createAt2 = "20200504 18:19:18";

    @AfterEach
    void cleanDataSource() {
        Set<String> keys = template.keys("*");
        if (keys != null) {
            template.delete(keys);
        }
    }

    @Test
    void connectNotiRedis() {
        String userId = UUID.randomUUID().toString();
        BoundListOperations<String, Notification> op = template.boundListOps(userId);

        Notification item = new Notification(userId, senderId1, type1, content1, createAt1);
        op.rightPush(item);
        Notification result = op.rightPop();
        assertNotNull(result);
        assertEquals(result, item);
    }

    @Test
    void addNotification() {
        String userId = UUID.randomUUID().toString();
        BoundListOperations<String, Notification> op = template.boundListOps(userId);

        Notification item = new Notification(userId, senderId1, type1, content1, createAt1);
        service.addNotification(userId, item);

        List<Notification> result = op.range(0, -1);
        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), item);
    }

    @Test
    void getOfflineNotificationByUserId() {
        String userId = UUID.randomUUID().toString();
        BoundListOperations<String, Notification> op = template.boundListOps(userId);

        List<Notification> result = service.getOfflineNotificationByUserId(userId);
        assertNotNull(result);
        assertTrue(result.isEmpty());

        Notification item1 = new Notification(userId, senderId1, type1, content1, createAt1);
        service.addNotification(userId, item1);
        result = service.getOfflineNotificationByUserId(userId);
        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), item1);

        result = service.getOfflineNotificationByUserId(userId);
        assertNotNull(result);
        assertTrue(result.isEmpty());

        Notification item2 = new Notification(userId, senderId2, type2, content2, createAt2);
        service.addNotification(userId, item1);
        service.addNotification(userId, item2);
        result = service.getOfflineNotificationByUserId(userId);
        assertNotNull(result);
        assertEquals(result.size(), 2);
        assertEquals(result.get(0), item1);
        assertEquals(result.get(1), item2);

        result = service.getOfflineNotificationByUserId(userId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}