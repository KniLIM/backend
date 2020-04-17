package com.knilim.offline;

import com.knilim.data.utils.BytesUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Test OfflineServiceImpl")
@ExtendWith(SpringExtension.class)
class OfflineServiceImplTest {

    @Resource
    private OfflineServiceImpl service;

    private final String msg1 = "hello world";
    private final String msg2 = "redis tests";
    private final String msg3 = "spring boot";

    @Resource
    private RedisTemplate<String, Byte[]> template;

    @BeforeEach
    void cleanDataSource() {
        Set<String> keys = template.keys("*");
        if (keys != null) {
            template.delete(keys);
        }
    }

    @Test
    void connectOfflineRedis() {
        String userId = UUID.randomUUID().toString();
        BoundListOperations<String, Byte[]> op = template.boundListOps(userId);

        op.rightPush(BytesUtil.toObj(msg1.getBytes(StandardCharsets.UTF_8)));
        Byte[] result = op.leftPop();
        assertNotNull(result);
        assertEquals(new String(BytesUtil.toPrimitives(result), StandardCharsets.UTF_8), msg1);
    }

    @Test
    void getOfflineMsgs() {
        String userId = UUID.randomUUID().toString();
        BoundListOperations<String, Byte[]> op = template.boundListOps(userId);

        List<Byte[]> result = service.getOfflineMsgs(userId);
        assertTrue(result.isEmpty());

        op.rightPush(BytesUtil.toObj(msg1.getBytes(StandardCharsets.UTF_8)));
        result = service.getOfflineMsgs(userId);
        assertFalse(result.isEmpty());
        assertEquals(result.size(), 1);
        assertEquals(new String(BytesUtil.toPrimitives(result.get(0)), StandardCharsets.UTF_8), msg1);

        op.rightPush(BytesUtil.toObj(msg2.getBytes(StandardCharsets.UTF_8)));
        op.rightPush(BytesUtil.toObj(msg3.getBytes(StandardCharsets.UTF_8)));
        result = service.getOfflineMsgs(userId);
        assertFalse(result.isEmpty());
        assertEquals(result.size(), 2);
        assertEquals(new String(BytesUtil.toPrimitives(result.get(0)), StandardCharsets.UTF_8), msg2);
        assertEquals(new String(BytesUtil.toPrimitives(result.get(1)), StandardCharsets.UTF_8), msg3);
    }

    @Test
    void addOfflineMsg() {
        String userId = UUID.randomUUID().toString();

        service.addOfflineMsg(userId, BytesUtil.toObj(msg1.getBytes()));
        List<Byte[]> result = service.getOfflineMsgs(userId);
        assertFalse(result.isEmpty());
        assertEquals(result.size(), 1);
        assertEquals(new String(BytesUtil.toPrimitives(result.get(0)), StandardCharsets.UTF_8), msg1);

        service.addOfflineMsg(userId, BytesUtil.toObj(msg2.getBytes()));
        service.addOfflineMsg(userId, BytesUtil.toObj(msg3.getBytes()));
        result = service.getOfflineMsgs(userId);
        assertFalse(result.isEmpty());
        assertEquals(result.size(), 2);
        assertEquals(new String(BytesUtil.toPrimitives(result.get(0)), StandardCharsets.UTF_8), msg2);
        assertEquals(new String(BytesUtil.toPrimitives(result.get(1)), StandardCharsets.UTF_8), msg3);
    }
}