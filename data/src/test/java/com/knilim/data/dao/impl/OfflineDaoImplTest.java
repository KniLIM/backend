package com.knilim.data.dao.impl;

import com.knilim.data.dao.OfflineDao;
import com.knilim.data.utils.BytesUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Test Offline Dao")
@ExtendWith(SpringExtension.class)
class OfflineDaoImplTest {

    private OfflineDao dao;


    private RedisTemplate<String, Byte[]> offlineTemplate;

    private String msg1 = "hello world";
    private String msg2 = "redis tests";
    private String msg3 = "spring boot";

    @Autowired
    public OfflineDaoImplTest(OfflineDao dao, RedisTemplate<String, Byte[]> offlineTemplate) {
        this.dao = dao;
        this.offlineTemplate = offlineTemplate;
    }

    @BeforeEach
    void cleanDataSource() {
        Set<String> keys = offlineTemplate.keys("*");
        if (keys != null) {
            offlineTemplate.delete(keys);
        }
    }

    @Test
    void connectOfflineRedis() {
        UUID userId = UUID.randomUUID();
        BoundListOperations<String, Byte[]> op = offlineTemplate.boundListOps(userId.toString());

        op.rightPush(BytesUtil.toObj(msg1.getBytes(StandardCharsets.UTF_8)));
        Byte[] result = op.leftPop();
        assertNotNull(result);
        assertEquals(new String(BytesUtil.toPrimitives(result), StandardCharsets.UTF_8), msg1);
    }

    @Test
    void getOfflineMsgs() {
        UUID userId = UUID.randomUUID();
        BoundListOperations<String, Byte[]> op = offlineTemplate.boundListOps(userId.toString());

        List<Byte[]> result = dao.getOfflineMsgs(userId);
        assertTrue(result.isEmpty());

        op.rightPush(BytesUtil.toObj(msg1.getBytes(StandardCharsets.UTF_8)));
        result = dao.getOfflineMsgs(userId);
        assertFalse(result.isEmpty());
        assertEquals(result.size(), 1);
        assertEquals(new String(BytesUtil.toPrimitives(result.get(0)), StandardCharsets.UTF_8), msg1);

        op.rightPush(BytesUtil.toObj(msg2.getBytes(StandardCharsets.UTF_8)));
        op.rightPush(BytesUtil.toObj(msg3.getBytes(StandardCharsets.UTF_8)));
        result = dao.getOfflineMsgs(userId);
        assertFalse(result.isEmpty());
        assertEquals(result.size(), 2);
        assertEquals(new String(BytesUtil.toPrimitives(result.get(0)), StandardCharsets.UTF_8), msg2);
        assertEquals(new String(BytesUtil.toPrimitives(result.get(1)), StandardCharsets.UTF_8), msg3);
    }

    @Test
    void addOfflineMsg() {
        UUID userId = UUID.randomUUID();

        dao.addOfflineMsg(userId, BytesUtil.toObj(msg1.getBytes()));
        List<Byte[]> result = dao.getOfflineMsgs(userId);
        assertFalse(result.isEmpty());
        assertEquals(result.size(), 1);
        assertEquals(new String(BytesUtil.toPrimitives(result.get(0)), StandardCharsets.UTF_8), msg1);

        dao.addOfflineMsg(userId, BytesUtil.toObj(msg2.getBytes()));
        dao.addOfflineMsg(userId, BytesUtil.toObj(msg3.getBytes()));
        result = dao.getOfflineMsgs(userId);
        assertFalse(result.isEmpty());
        assertEquals(result.size(), 2);
        assertEquals(new String(BytesUtil.toPrimitives(result.get(0)), StandardCharsets.UTF_8), msg2);
        assertEquals(new String(BytesUtil.toPrimitives(result.get(1)), StandardCharsets.UTF_8), msg3);
    }
}