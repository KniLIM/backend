package com.knilim.online;

import com.knilim.data.model.DeviceInfo;
import com.knilim.data.utils.Device;
import com.knilim.service.OnlineService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

//@Component
@Service
public class OnlineServiceImpl implements OnlineService {

    @Resource
    private RedisTemplate<String, HashMap<Device, DeviceInfo>> template;


    @Override
    public void addOnlineDevice(String userId, Device device, String token,
                                String ip, Integer port) {
        template.boundHashOps(userId).put(device, new DeviceInfo(token, ip, port));
    }

    @Override
    public void removeOnlineDevice(String userId, Device device) {
        template.boundHashOps(userId).delete(device);
    }

    @Override
    public boolean checkToken(String userId, Device device, String token) {
        BoundHashOperations<String, Device, DeviceInfo> op = template.boundHashOps(userId);
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
    public DeviceInfo getDevice(String userId, Device device) {
        BoundHashOperations<String, Device, DeviceInfo> op = template.boundHashOps(userId);
        DeviceInfo info = op.get(device);

        if (info != null && info.isConnect()) {
            return info;
        }
        return null;
    }

    @Override
    public Map<Device, DeviceInfo> getDevicesByUserId(String userId) {
        BoundHashOperations<String, Device, DeviceInfo> op = template.boundHashOps(userId);
        Map<Device, DeviceInfo> devices = op.entries();
        if (devices == null) {
            return null;
        }

        // 过滤 status 不是 connected 的 device
        devices.entrySet().removeIf(entry -> !entry.getValue().isConnect());
        return devices.size() > 0 ? devices : null;
    }
}
