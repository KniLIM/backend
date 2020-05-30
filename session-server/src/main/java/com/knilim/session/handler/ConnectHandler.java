package com.knilim.session.handler;

import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.knilim.data.model.Notification;
import com.knilim.data.utils.Device;
import com.knilim.data.utils.DeviceUtil;
import com.knilim.service.OfflineService;
import com.knilim.service.OnlineService;
import com.knilim.service.PushService;
import com.knilim.session.dao.ClientDao;
import com.knilim.session.data.AESEncryptor;
import com.knilim.session.data.DH;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Component
public class ConnectHandler {
    private static final Logger logger = LoggerFactory.getLogger(ConnectHandler.class);

    private SocketIONamespace nps;

    @Reference
    private OnlineService onlineService;

    @Reference
    private OfflineService offlineService;

//    @Reference
//    private PushService pushService;

    @Resource
    private ClientDao localRedis;

    @Autowired
    public ConnectHandler(SocketIOServer server) {
        this.nps = server.getNamespace("/sockets");
        nps.addConnectListener(onConnect());
        nps.addDisconnectListener(onDisConnect());
//        nps.addEventListener("connect-ack",void.class,onConnectAck());
    }

    /**
     * 监听 connect 事件并触发相应回调事件：
     * 1. check token ，成功转下条，失败触发 connect-error
     * 2. 生成密钥 {@link DH#initSecretKey(String clientKey)} ， 成功触发 转下条 ，失败触发 connect-error
     * 3. local redis记录
     * 4. 离线消息
     * 5. 可能有离线通知
     */
    private ConnectListener onConnect() {
        // 返回事件为  connect-error connect-ack
        return socketIOClient -> {
            HandshakeData handshake = socketIOClient.getHandshakeData();
            String hello = handshake.getSingleUrlParam("hello");
            String clientKey = handshake.getSingleUrlParam("clientKey");
            String token = handshake.getSingleUrlParam("token");
            String userId = handshake.getSingleUrlParam("userId");
            Device device = DeviceUtil.fromString(handshake.getSingleUrlParam("device"));

            // check token
            if (!onlineService.checkToken(userId, device, token)) {
                logger.info("Client[{}] userId[{}] and token[{}] mismatch", socketIOClient.getSessionId(), userId, token);
                socketIOClient.sendEvent("connect-error", "token error");
                socketIOClient.disconnect();
            } else {
                // init key
                String key = DH.initSecretKey(clientKey);
                String decryptData = new String(AESEncryptor.decrypt(hello.getBytes(), key));

                // check key for decry ping
                if (decryptData.equals("hello")) {
                    localRedis.addConnect(userId, device, socketIOClient.getSessionId().toString(),
                            handshake.getAddress().getHostString(), handshake.getAddress().getPort(),
                            clientKey);
                    List<Byte[]> offlineMsgs = offlineService.getOfflineMsgs(userId);
//                    List<Notification> pushMsgs = pushService.getOfflineNotificationByUserId(userId);
//                    if (offlineMsgs != null && pushMsgs != null
//                            && !offlineMsgs.isEmpty() && !pushMsgs.isEmpty()) {
//                        socketIOClient.sendEvent("connect-ack", offlineMsgs, pushMsgs);
//                    } else if (offlineMsgs != null && !offlineMsgs.isEmpty()) {
//                        socketIOClient.sendEvent("connect-ack", offlineMsgs);
//                    } else if (pushMsgs != null && !pushMsgs.isEmpty()) {
//                        socketIOClient.sendEvent("connect-ack", pushMsgs);
//                    } else {
//                        socketIOClient.sendEvent("connect-ack");
//                    }
                    if (offlineMsgs != null
                            && !offlineMsgs.isEmpty()) {
                        socketIOClient.sendEvent("connect-ack", offlineMsgs);
                    } else {
                        socketIOClient.sendEvent("connect-ack");
                    }
                } else {
                    socketIOClient.sendEvent("connect-error", "key error");
                    socketIOClient.disconnect();
                }
            }
        };
    }

    private DisconnectListener onDisConnect() {
        return socketIOClient -> {
            HandshakeData handshake = socketIOClient.getHandshakeData();
            String userId = handshake.getSingleUrlParam("userId");
            Device device = DeviceUtil.fromString(handshake.getSingleUrlParam("device"));
            localRedis.removeConnect(userId, device);
        };
    }

//    /**
//     * Client 准备好了密钥，确认连接成功
//     * 逻辑续 {@link #onConnect()}
//     * 3. local redis记录
//     * 4. 离线消息
//     * 5. 可能有离线通知
//     *
//     * @return 离线消息
//     */
//    private DataListener<Void> onConnectAck() {
//        return (socketIOClient, aVoid, ackRequest) -> {
//            onlineService.addOnlineDevice();
//
//           socketIOClient.sendEvent("disconnect","error info");
//        }
//    }
}
