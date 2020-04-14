package com.knilim.data.dao.impl;

import com.knilim.data.dao.OnlineDao;
import com.knilim.data.model.DeviceInfo;
import com.knilim.data.model.Online;
import com.knilim.data.utils.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Iterator;
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
    public void addOnlineDevice(UUID userId, Device device, String token,
                                String ip, Integer port) {
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
    public boolean checkToken(UUID userId, Device device, String token) {
        Online result = template.opsForValue().get(userId.toString());
        if (result == null) {
            return false;
        }

        DeviceInfo deviceInfo = result.getDevice(device);
        if (deviceInfo == null) {
            return false;
        }

        if (!deviceInfo.isConnect() && deviceInfo.getToken().equals(token)) {
            result.connect(device);
            template.opsForValue().set(userId.toString(), result);
            return true;
        }

        return false;
    }

    @Override
    public DeviceInfo getDevice(UUID userId, Device device) {
        Online result = template.opsForValue().get(userId.toString());
        if (result == null) {
            return null;
        }

        DeviceInfo deviceInfo = result.getDevice(device);
        return deviceInfo.isConnect() ? deviceInfo : null;
    }

    @Override
    public Map<Device, DeviceInfo> getDevicesByUserId(UUID userId) {
        Online result = template.opsForValue().get(userId.toString());
        if (result == null) {
            return null;
        }

        // 过滤 status 不是 connected 的 device
        Map<Device, DeviceInfo> devices = result.getDevices();
        Iterator<Map.Entry<Device, DeviceInfo>> iterator = devices.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Device, DeviceInfo> entry = iterator.next();
            if (!entry.getValue().isConnect()) {
                iterator.remove();
                iterator.next();
            }
        }
        return devices;
    }
}
