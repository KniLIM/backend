package com.knilim.data.dao.impl;

import com.knilim.data.dao.OnlineDao;
import com.knilim.data.model.DeviceInfo;
import com.knilim.data.utils.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class OnlineDaoImpl implements OnlineDao {

    private RedisTemplate<String, HashMap<Device, DeviceInfo>> onlineTemplate;

    @Autowired
    public OnlineDaoImpl(RedisTemplate<String, HashMap<Device, DeviceInfo>> onlineTemplate) {
        this.onlineTemplate = onlineTemplate;
    }

    @Override
    public void addOnlineDevice(UUID userId, Device device, String token,
                                String ip, Integer port) {
        onlineTemplate.boundHashOps(userId.toString()).put(device, new DeviceInfo(token, ip, port));
    }

    @Override
    public void removeOnlineDevice(UUID userId, Device device) {
        onlineTemplate.boundHashOps(userId.toString()).delete(device);
    }

    @Override
    public boolean checkToken(UUID userId, Device device, String token) {
        BoundHashOperations<String, Device, DeviceInfo> op = onlineTemplate.boundHashOps(userId.toString());
        DeviceInfo info = op.get(device);

        if (info == null) {
            return false;
        }

        if (!info.isConnect() && info.getToken().equals(token)) {
            info.connectToSession();
            op.put(device, info);
            return true;
        }

        return false;
    }

    @Override
    public DeviceInfo getDevice(UUID userId, Device device) {
        BoundHashOperations<String, Device, DeviceInfo> op = onlineTemplate.boundHashOps(userId.toString());
        DeviceInfo info = op.get(device);

        if (info != null && info.isConnect()) {
            return info;
        }
        return null;
    }

    @Override
    public Map<Device, DeviceInfo> getDevicesByUserId(UUID userId) {
        BoundHashOperations<String, Device, DeviceInfo> op = onlineTemplate.boundHashOps(userId.toString());
        Map<Device, DeviceInfo> devices = op.entries();
        if (devices == null) {
            return null;
        }

        // 过滤 status 不是 connected 的 device
        devices.entrySet().removeIf(entry -> !entry.getValue().isConnect());
        return devices.size() > 0 ? devices : null;
    }
}
