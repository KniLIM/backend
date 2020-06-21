package com.knilim.service;

import com.knilim.data.model.Notification;

import java.util.List;

public interface PushService {
    List<Notification> getOfflineNotificationByUserId(String userId);

    void addOfflineNotification(String userId, Notification notification);
}
