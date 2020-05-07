package com.knilim.service;

import com.knilim.data.model.Notification;

import java.util.List;

public interface PushService {

    void addNotification(String userId, Notification notification);

    List<Notification> getOfflineNotificationByUserId(String userId);
}
