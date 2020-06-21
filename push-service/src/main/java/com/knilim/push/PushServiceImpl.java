package com.knilim.push;

import com.knilim.data.model.Notification;
import com.knilim.service.PushService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
@Service
public class PushServiceImpl implements PushService {

    @Resource
    private RedisTemplate<String, Notification> template;

    @Override
    public List<Notification> getOfflineNotificationByUserId(String userId) {
        List<Notification> notifications = template.boundListOps(userId).range(0, -1);
        if (notifications != null) {
            template.delete(userId);
            return notifications;
        }
        return new ArrayList<>();
    }

    @Override
    public void addOfflineNotification(String userId, Notification notification) {
        template.boundListOps(userId).rightPush(notification);
    }
}
