package com.knilim.session.dao.impl;

import com.knilim.data.utils.Device;
import com.knilim.session.dao.ClientDao;
import com.knilim.session.model.Connect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Component
public class ClientDaoImpl implements ClientDao {

    private RedisTemplate<String,HashMap<Device,Connect>> template;

    @Autowired
    public ClientDaoImpl(RedisTemplate<String, HashMap<Device,Connect>> template) {
        this.template = template;
    }

    @Override
    public HashMap<Device, Connect> getConnectsByUserId(String userId) {
        return template.boundValueOps(userId).get();
    }

    @Override
    public Connect getConnect(String userId, Device device) {
        return template.boundValueOps(userId).get().get(device);
    }

    @Override
    public void addConnect(String userId, Device device, String sessionId, String host, Integer port, String key) {
        template.boundHashOps(userId).put(device,new Connect(sessionId, host, port, key));
    }

    @Override
    public void removeConnect(String userId, Device device) {
        template.boundValueOps(userId).get().remove(device);

    }
}
