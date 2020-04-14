package com.knilim.data.dao.impl;

import com.knilim.data.dao.OnlineDao;
import com.knilim.data.model.DeviceInfo;
import com.knilim.data.model.Online;
import com.knilim.data.utils.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class OnlineDaoImpl implements OnlineDao {

    RedisTemplate<String, Online> template;

    @Autowired
    public OnlineDaoImpl(RedisTemplate<String, Online> template) {
        this.template = template;
    }

    @Override
    public void addOnlineDevice(UUID userId, Device device, String token, String ip, Integer port) {
        Online result = template.opsForValue().get(userId.toString());
        if (result == null) {
            result = new Online(userId);
        }
        result.addDevice(device, token, ip, port);
        template.opsForValue().set(userId.toString(), result);
    }

    @Override
    public void removeOnlineDevice(UUID userId, Device device) {
        Online result = template.opsForValue().get(userId.toString());
        if (result != null) {
            result.removeDevice(device);
        }
    }

    @Override
    public DeviceInfo getDevice(UUID userId, Device device) {
        Online result = template.opsForValue().get(userId.toString());
        if (result == null) {
            return null;
        }
        return result.getDevice(device);
    }

    @Override
    public Map<Device, DeviceInfo> getDevicesByUserId(UUID userId) {
        Online result = template.opsForValue().get(userId.toString());
        if (result == null) {
            return null;
        }
        return result.getDevices();
    }
}
