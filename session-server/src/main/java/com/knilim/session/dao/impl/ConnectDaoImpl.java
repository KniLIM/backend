package com.knilim.session.dao.impl;

import com.knilim.session.dao.ConnectDao;
import com.knilim.session.model.Connect;
import com.knilim.data.utils.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConnectDaoImpl implements ConnectDao {

    private RedisTemplate<String, Connect> template;

    @Autowired
    public ConnectDaoImpl(
            @Qualifier("localConnectRedisTemplate") RedisTemplate<String, Connect> template) {
        this.template = template;
    }

    @Override
    public Connect getConnectBySessionId(UUID sessionId) {
        return null;
    }

    @Override
    public void addConnect(UUID sessionId, UUID userId, Device device,
                           String host, Integer port, String key) {

    }

    @Override
    public void removeConnect(UUID sessionId) {

    }
}
