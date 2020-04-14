package com.knilim.data.dao.impl;

import com.knilim.data.dao.OnlineDao;
import com.knilim.data.model.DeviceInfo;
import com.knilim.data.model.Online;
import com.knilim.data.utils.Device;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Test Online Dao")
@ExtendWith(SpringExtension.class)
class OnlineDaoImplTest {

    @Resource
    private OnlineDao dao;

    @Resource
    private RedisTemplate<String, Online> template;

    @AfterEach
    void cleanDataSource() {
        Set<String> keys = template.keys("*");
        if (keys != null) {
            template.delete(keys);
        }
    }

    @Test
    void connectGlobalRedis() {
        UUID id = UUID.randomUUID();

        template.opsForValue().set(id.toString(), new Online(id));
        Online result = template.opsForValue().get(id.toString());
        assertEquals(result.getUserId(), id);
    }

    @Test
    void addOnlineDevice() {
        UUID userId = UUID.randomUUID();
        String token = "34jk80dj0934h&*gq3y89nuIO1u23*903e4";
        String ip = "192.168.2.1";
        Integer port = 8888;

        // 加入第一个设备
        dao.addOnlineDevice(userId, Device.D_WEB, token, ip, port);
        Online result = template.opsForValue().get(userId.toString());
        assertEquals(result.getUserId(), userId);
        assertEquals(result.getDevice(Device.D_WEB), new DeviceInfo(token, ip, port));

        String token2 = "k342n90s34jns8x0-8*&8dfis8b4|";
        String ip2 = "192.168.3.1";
        Integer port2 = 8000;

        // 加入第二个设备
        dao.addOnlineDevice(userId, Device.D_PC, token2, ip2, port2);
        result = template.opsForValue().get(userId.toString());
        assertEquals(result.getUserId(), userId);
        assertEquals(result.getDevice(Device.D_WEB), new DeviceInfo(token, ip, port));
        assertEquals(result.getDevice(Device.D_PC), new DeviceInfo(token2, ip2, port2));
    }
}