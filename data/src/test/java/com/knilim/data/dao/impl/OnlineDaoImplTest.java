package com.knilim.data.dao.impl;

import com.knilim.data.dao.OnlineDao;
import com.knilim.data.model.DeviceInfo;
import com.knilim.data.utils.Device;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Test Online Dao")
@ExtendWith(SpringExtension.class)
class OnlineDaoImplTest {

    private OnlineDao dao;

    private RedisTemplate<String, HashMap<Device, DeviceInfo>> onlineTemplate;

    private String token1 = "hello world";
    private String token2 = "redis tests";
    private String ip1 = "192.168.1.1";
    private String ip2 = "192.168.2.1";
    private Integer port1= 8888;
    private Integer port2 = 8000;

    @Autowired
    public OnlineDaoImplTest(OnlineDao dao, RedisTemplate<String, HashMap<Device, DeviceInfo>> onlineTemplate) {
        this.dao = dao;
        this.onlineTemplate = onlineTemplate;
    }

    @AfterEach
    void cleanDataSource() {
        Set<String> keys = onlineTemplate.keys("*");
        if (keys != null) {
            onlineTemplate.delete(keys);
        }
    }

    @Test
    void connectOnlineRedis() {
        BoundHashOperations<String, Device, DeviceInfo> op = onlineTemplate.boundHashOps(UUID.randomUUID().toString());
        op.put(Device.D_PC, new DeviceInfo("hello world", "localhost", 80));
        DeviceInfo info =  op.get(Device.D_PC);

        assertNotNull(info);
        assertEquals(info.getToken(), "hello world");
        assertEquals(info.getSessionServerIp(), "localhost");
        assertEquals(info.getSessionServerPort(), 80);
    }

    @Test
    void addOnlineDevice() {
        UUID userId = UUID.randomUUID();
        BoundHashOperations<String, Device, DeviceInfo> op = onlineTemplate.boundHashOps(userId.toString());

        // 加入第一个设备
        dao.addOnlineDevice(userId, Device.D_WEB, token1, ip1, port1);
        DeviceInfo info1 = op.get(Device.D_WEB);
        assertNotNull(info1);
        assertEquals(info1.getToken(), token1);
        assertEquals(info1.getSessionServerIp(), ip1);
        assertEquals(info1.getSessionServerPort(), port1);

        // 加入第二个设备
        dao.addOnlineDevice(userId, Device.D_PC, token2, ip2, port2);
        DeviceInfo info2 = op.get(Device.D_PC);
        assertNotNull(info2);
        assertEquals(info2.getToken(), token2);
        assertEquals(info2.getSessionServerIp(), ip2);
        assertEquals(info2.getSessionServerPort(), port2);
    }

    @Test
    void removeOnlineDevice() {
        UUID userId = UUID.randomUUID();
        BoundHashOperations<String, Device, DeviceInfo> op = onlineTemplate.boundHashOps(userId.toString());

        dao.addOnlineDevice(userId, Device.D_WEB, token1, ip1, port1);
        DeviceInfo info1 = op.get(Device.D_WEB);
        assertNotNull(info1);

        dao.removeOnlineDevice(userId, Device.D_WEB);
        DeviceInfo info2 = op.get(Device.D_WEB);
        assertNull(info2);
    }

    @Test
    void checkToken() {
        UUID userId = UUID.randomUUID();
        dao.addOnlineDevice(userId, Device.D_WEB, token1, ip1, port1);
        assertFalse(dao.checkToken(userId, Device.D_WEB, token2));
        assertTrue(dao.checkToken(userId, Device.D_WEB, token1));
        assertFalse(dao.checkToken(userId, Device.D_WEB, token1));
    }

    @Test
    void getDevice() {
        UUID userId = UUID.randomUUID();

        dao.addOnlineDevice(userId, Device.D_WEB, token1, ip1, port1);
        assertNull(dao.getDevice(userId, Device.D_WEB));
        dao.checkToken(userId, Device.D_WEB, token1);
        assertNotNull(dao.getDevice(userId, Device.D_WEB));
    }

    @Test
    void getDevicesByUserId() {
        UUID userId = UUID.randomUUID();

        dao.addOnlineDevice(userId, Device.D_WEB, token1, ip1, port1);
        assertNull(dao.getDevicesByUserId(userId));
        dao.addOnlineDevice(userId, Device.D_PC, token2, ip2, port2);
        assertNull(dao.getDevicesByUserId(userId));

        dao.checkToken(userId, Device.D_WEB, token1);
        assertEquals(dao.getDevicesByUserId(userId).size(), 1);
        assertEquals(dao.getDevicesByUserId(userId).get(Device.D_WEB).getToken(), token1);

        dao.checkToken(userId, Device.D_PC, token2);
        assertEquals(dao.getDevicesByUserId(userId).size(), 2);
        assertEquals(dao.getDevicesByUserId(userId).get(Device.D_PC).getToken(), token2);
    }
}