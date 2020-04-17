package com.knilim.push;

import com.knilim.data.model.DeviceInfo;
import com.knilim.data.model.Notification;
import com.knilim.data.utils.Device;
import com.knilim.service.ForwardService;
import com.knilim.service.OnlineService;
import com.knilim.service.PushService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
@Service
public class PushServiceImpl implements PushService {

    @Resource
    private RedisTemplate<String, Notification> template;

    @Reference
    private ForwardService forwardService;

    @Reference
    private OnlineService onlineService;

    @Override
    @Async
    public void addNotification(String userId, Notification notification) {
        Map<Device, DeviceInfo> devices = onlineService.getDevicesByUserId(userId);

        if (devices == null) {
            template.boundListOps(userId).rightPush(notification);
        } else {
            Set<String> endpoints = new HashSet<>();

            for (DeviceInfo device: devices.values()) {
                String endpoint = String.format("%s:%d", device.getSessionServerIp(),
                        device.getSessionServerPort());
                if (!endpoints.contains(endpoint)) {
                    endpoints.add(endpoint);
                    // TODO: 指定 ip:port 发送
                    forwardService.publish(userId, notification);
                }
            }
        }
    }

    @Override
    public void addNotification(String[] userIds, Notification notification) {
        for (String userId: userIds) {
            addNotification(userId, notification);
        }
    }

    @Override
    public List<Notification> getOfflineNotificationByUserId(String userId) {
        List<Notification> notifications = template.boundListOps(userId).range(0, -1);
        return notifications == null ? new ArrayList<>() : notifications;
    }
}
