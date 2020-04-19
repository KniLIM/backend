package com.knilim.session.dao.impl;

import com.knilim.data.utils.Device;
import com.knilim.session.dao.ClientDao;
import com.knilim.session.model.Connect;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class ClientDaoImpl implements ClientDao {

    @Resource
    private RedisTemplate<String,HashMap<Device,Connect>> template;

    @Override
    public Map<Device, Connect> getConnectsByUserId(String userId) {
        BoundHashOperations<String, Device, Connect> op = template.boundHashOps(userId);
        Map<Device, Connect> map = op.entries();
        return (map!=null&& map.size() > 0)? map:null;
    }

    @Override
    public Connect getConnect(String userId, Device device) {
        return (Connect) template.boundHashOps(userId).get(device);
    }

    @Override
    public String getKey(String userId, Device device) {
        Connect connect = getConnect(userId, device);
        if (connect !=null)
            return connect.getKey();
        return null;
    }

    @Override
    public void addConnect(String userId, Device device, String sessionId, String host, Integer port, String key) {
        template.boundHashOps(userId).put(device,new Connect(sessionId, host, port, key));
    }

    @Override
    public void removeConnect(String userId, Device device) {
        template.boundHashOps(userId).delete(device);
    }

}
