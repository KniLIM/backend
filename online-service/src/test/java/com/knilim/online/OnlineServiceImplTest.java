package com.knilim.online;

import com.knilim.data.model.DeviceInfo;
import com.knilim.data.utils.Device;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Test OnlineServiceImpl")
@ExtendWith(SpringExtension.class)
class OnlineServiceImplTest {

    @Resource
    private OnlineServiceImpl service;

    @Resource
    private RedisTemplate<String, HashMap<Device, DeviceInfo>> template;

    private final String token1 = "hello world";
    private final String token2 = "redis tests";
    private final String ip1 = "192.168.1.1";
    private final String ip2 = "192.168.2.1";
    private final Integer port1= 8888;
    private final Integer port2 = 8000;

    @AfterEach
    void cleanDataSource() {
        Set<String> keys = template.keys("*");
        if (keys != null) {
            template.delete(keys);
        }
    }

    @Test
    void connectOnlineRedis() {
        BoundHashOperations<String, Device, DeviceInfo> op = template.boundHashOps(UUID.randomUUID().toString());
        op.put(Device.D_PC, new DeviceInfo("hello world", "localhost", 80));
        DeviceInfo info =  op.get(Device.D_PC);

        assertNotNull(info);
        assertEquals(info.getToken(), "hello world");
        assertEquals(info.getSessionServerIp(), "localhost");
        assertEquals(info.getSessionServerPort(), 80);
    }

    @Test
    void addOnlineDevice() {
        String userId = UUID.randomUUID().toString();
        BoundHashOperations<String, Device, DeviceInfo> op = template.boundHashOps(userId);

        // 加入第一个设备
        service.addOnlineDevice(userId, Device.D_WEB, token1, ip1, port1);
        DeviceInfo info1 = op.get(Device.D_WEB);
        assertNotNull(info1);
        assertEquals(info1.getToken(), token1);
        assertEquals(info1.getSessionServerIp(), ip1);
        assertEquals(info1.getSessionServerPort(), port1);

        // 加入第二个设备
        service.addOnlineDevice(userId, Device.D_PC, token2, ip2, port2);
        DeviceInfo info2 = op.get(Device.D_PC);
        assertNotNull(info2);
        assertEquals(info2.getToken(), token2);
        assertEquals(info2.getSessionServerIp(), ip2);
        assertEquals(info2.getSessionServerPort(), port2);
    }

    @Test
    void removeOnlineDevice() {
        String userId = UUID.randomUUID().toString();
        BoundHashOperations<String, Device, DeviceInfo> op = template.boundHashOps(userId);

        service.addOnlineDevice(userId, Device.D_WEB, token1, ip1, port1);
        DeviceInfo info1 = op.get(Device.D_WEB);
        assertNotNull(info1);

        service.removeOnlineDevice(userId, Device.D_WEB);
        DeviceInfo info2 = op.get(Device.D_WEB);
        assertNull(info2);
    }

    @Test
    void checkToken() {
        String userId = UUID.randomUUID().toString();
        service.addOnlineDevice(userId, Device.D_WEB, token1, ip1, port1);
        assertFalse(service.checkToken(userId, Device.D_WEB, token2));
        assertTrue(service.checkToken(userId, Device.D_WEB, token1));
        assertTrue(service.checkToken(userId, Device.D_WEB, token1));
    }

    @Test
    void connect() {
        String userId = UUID.randomUUID().toString();
        BoundHashOperations<String, Device, DeviceInfo> op = template.boundHashOps(userId);

        service.addOnlineDevice(userId, Device.D_WEB, token1, ip1, port1);
        DeviceInfo info = op.get(Device.D_WEB);
        assertNotNull(info);
        assertFalse(info.isConnect());

        assertTrue(service.connect(userId, Device.D_WEB));
        assertFalse(service.connect(userId, Device.D_WEB));
        info = op.get(Device.D_WEB);
        assertNotNull(info);
        assertTrue(info.isConnect());

        assertTrue(service.disconnect(userId, Device.D_WEB));
        assertFalse(service.disconnect(userId, Device.D_WEB));
        info = op.get(Device.D_WEB);
        assertNotNull(info);
        assertFalse(info.isConnect());

        assertFalse(service.connect(userId, Device.D_PC));
        assertFalse(service.disconnect(userId, Device.D_PC));
    }

    @Test
    void getDevice() {
        String userId = UUID.randomUUID().toString();

        service.addOnlineDevice(userId, Device.D_WEB, token1, ip1, port1);
        assertNull(service.getDevice(userId, Device.D_WEB));
        service.connect(userId, Device.D_WEB);
        assertNotNull(service.getDevice(userId, Device.D_WEB));
    }

    @Test
    void getDevicesByUserId() {
        String userId = UUID.randomUUID().toString();

        service.addOnlineDevice(userId, Device.D_WEB, token1, ip1, port1);
        assertNull(service.getDevicesByUserId(userId));
        service.addOnlineDevice(userId, Device.D_PC, token2, ip2, port2);
        assertNull(service.getDevicesByUserId(userId));

        service.connect(userId, Device.D_WEB);
        assertEquals(service.getDevicesByUserId(userId).size(), 1);
        assertEquals(service.getDevicesByUserId(userId).get(Device.D_WEB).getToken(), token1);

        service.connect(userId, Device.D_PC);
        assertEquals(service.getDevicesByUserId(userId).size(), 2);
        assertEquals(service.getDevicesByUserId(userId).get(Device.D_PC).getToken(), token2);

        service.disconnect(userId, Device.D_WEB);
        assertEquals(service.getDevicesByUserId(userId).size(), 1);
    }
}