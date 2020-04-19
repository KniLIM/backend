package com.knilim.session.dao.impl;

import com.knilim.data.utils.Device;
import com.knilim.session.dao.ClientDao;
import com.knilim.session.model.Connect;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@DisplayName("Test client Dao")
@ExtendWith(SpringExtension.class)
class ClientDaoImplTest {

    @Resource
    private ClientDao dao;

    @Resource
    private RedisTemplate<String, HashMap<Device, Connect>> template;

    private final String token1 = "hello world";
    private final String token2 = "redis tests";
    private final String ip1 = "192.168.1.1";
    private final String ip2 = "192.168.2.1";
    private final Integer port1= 8888;
    private final Integer port2 = 8000;

    private final String sessionIdTest = UUID.randomUUID().toString();
    String userId = UUID.randomUUID().toString();

    @Test
    void connect(){
        String sid = UUID.randomUUID().toString();
        String uid = UUID.randomUUID().toString();
        template.boundHashOps(uid).put(Device.D_PC,new Connect(sid,"host",1234,"key"));
        Connect connect= (Connect) template.boundHashOps(uid).get(Device.D_PC);
        assertNotNull(connect);
        String sessionId = connect.getSessionId();
        assertNotNull(sessionId);
        assertEquals(sid,sessionId);
    }

    @Test
    void Test() {
        // 加入第一个设备
        dao.addConnect(userId,Device.D_WEB,sessionIdTest,ip1,port1,token1);
        Connect info1 = (Connect) template.boundHashOps(userId).get(Device.D_WEB);
        assertNotNull(info1);
        assertEquals(info1.getKey(), token1);
        assertEquals(info1.getHost(), ip1);
        assertEquals(info1.getPort(), port1);

        // 加入第二个设备
        dao.addConnect(userId,Device.D_PC,sessionIdTest,ip2,port2,token2);
        Connect info2 = (Connect) template.boundHashOps(userId).get(Device.D_PC);
        assertNotNull(info2);
        assertEquals(info2.getKey(), token2);
        assertEquals(info2.getHost(), ip2);
        assertEquals(info2.getPort(), port2);

        Map<Device,Connect> connects = dao.getConnectsByUserId(userId);
        assertNotNull(connects);
        Connect connect = dao.getConnect(userId,Device.D_PC);
        assertNotNull(connect);
        String key = dao.getKey(userId,Device.D_WEB);
        assertNotNull(key);
        assertEquals(token1,key);

        dao.removeConnect(userId,Device.D_WEB);
        Connect reconnect = dao.getConnect(userId,Device.D_WEB);
        assertNull(reconnect);
    }


}