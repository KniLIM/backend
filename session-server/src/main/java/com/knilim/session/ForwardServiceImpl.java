package com.knilim.session;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.knilim.data.model.DeviceInfo;
import com.knilim.data.model.Notification;
import com.knilim.data.utils.Device;
import com.knilim.service.ForwardService;
import com.knilim.data.utils.Tuple;
import com.knilim.service.OnlineService;
import com.knilim.service.PushService;
import com.knilim.session.dao.ClientDao;
import com.knilim.session.data.NotiProto;
import com.knilim.session.model.Connect;
import com.knilim.session.utils.HostManager;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Service
@Component
public class ForwardServiceImpl implements ForwardService {
    @Value("${com.knilim.session.port}")
    private Integer port;

    private SocketIONamespace nps;

    @Autowired
    public ForwardServiceImpl(SocketIOServer server) {
        this.nps = server.getNamespace("/sockets");
    }

    @Reference
    private OnlineService onlineService;

    @Reference
    private PushService pushService;

    @Resource
    private ClientDao dao;

    @Override
    public Tuple<String, Integer> getAvailableSession() {
        return new Tuple<>(HostManager.INSTANCE.getHost(), port);
    }

    @Override
    public void addNotification(String userId, Notification notification) {
        Map<Device, DeviceInfo> devices = onlineService.getDevicesByUserId(userId);

        if (devices == null) {
            pushService.addOfflineNotification(userId, notification);
        } else {
            Map<Device, Connect> connects = dao.getConnectsByUserId(userId);
            if (connects != null) {
                connects.forEach((device, connect) -> {
                    UUID sessionId = UUID.fromString(connect.getSessionId());
                    nps.getClient(sessionId).sendEvent("notification", makeNotificationBytes(notification));
                });
            }
        }
    }

    private static byte[] makeNotificationBytes(Notification notification) {
        return NotiProto.Notification.newBuilder()
                .setReceiver(notification.getRcvId())
                .setSender(notification.getSenderId())
                .setNotificationType(NotiProto.Notification.NotiType
                        .forNumber(notification.getType().ordinal()))
                .setContent(notification.getContent())
                .build()
                .toByteArray();
    }
}
