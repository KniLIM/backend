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
import com.knilim.session.ForwardServiceImpl;
import com.knilim.session.dao.ClientDao;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.net.InetAddress;
import java.util.List;


@Component
public class ConnectHandler {
    private static final Logger logger = LoggerFactory.getLogger(ConnectHandler.class);

    private SocketIONamespace nps;

    @Reference
    private OnlineService onlineService;

    @Reference
    private OfflineService offlineService;

    @Reference
    private PushService pushService;

    @Resource
    private ForwardServiceImpl forwardService;

    @Resource
    private ClientDao localRedis;

    @Autowired
    public ConnectHandler(SocketIOServer server) {
        this.nps = server.getNamespace("/sockets");
        nps.addConnectListener(onConnect());
        nps.addDisconnectListener(onDisConnect());
        nps.addEventListener("hello", Void.class, onHello());
        nps.addEventListener("leave", Void.class, onLeave());
    }

    /**
     * 监听 connect 事件并触发相应回调事件：
     * 1. check token ，成功转下条，失败触发 connect-error
     * 2. 生成密钥 initKey ， 成功触发 转下条 ，失败触发 connect-error
     * 3. hello
     */
    private ConnectListener onConnect() {
        // 返回事件为  connect-error connect-ack
        return socketIOClient -> {
            HandshakeData handshake = socketIOClient.getHandshakeData();
//            String clientKey = handshake.getSingleUrlParam("clientKey");
            String token = handshake.getSingleUrlParam("token");
            String userId = handshake.getSingleUrlParam("userId");
            Device device = DeviceUtil.fromString(handshake.getSingleUrlParam("device"));

            // check token
            if (!onlineService.checkToken(userId, device, token)) {
                logger.info("Client[{}] userId[{}] and token[{}] mismatch", socketIOClient.getSessionId(), userId, token);
                socketIOClient.sendEvent("connect-error", "token error");
                socketIOClient.disconnect();
            } else {
                logger.info("Client[{}] userId[{}] connect", socketIOClient.getSessionId(), userId);
                // init key
//                logger.info("key[{}] byte[{}]", clientKey,clientKey.getBytes());
                try {
//                    Map<String, Object> keyMap = initKey();
//                    logger.info(keyMap.toString());
//                    byte[] key = getSecretKey(getPublicKey(keyMap),getPrivateKey(initKey(getPublicKey(keyMap))));
//                    logger.info("get key success",key);
//                    byte[] key = getSecretKey(clientKey.getBytes(),getPrivateKey(keyMap));
                    localRedis.addConnect(userId, device, socketIOClient.getSessionId().toString(),
                            handshake.getAddress().getHostString(), handshake.getAddress().getPort(),
                            "key");

                    if (localRedis.getKey(userId, device) != null) {
//                        String hello = Arrays.toString(AESEncryptor.encrypt("hello".getBytes(), Arrays.toString(key)));
//                        logger.info("primary final key[{}]", key);
//                        socketIOClient.sendEvent("connect-ack", hello,getPublicKey(keyMap));
                        onlineService.connect(userId,device);
                        socketIOClient.sendEvent("connect-ack", "hello");
                    } else {
                        socketIOClient.sendEvent("connect-error", "key init error");
                        socketIOClient.disconnect();
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        };
    }



    private DisconnectListener onDisConnect() {
        return socketIOClient -> {
            HandshakeData handshake = socketIOClient.getHandshakeData();
            String userId = handshake.getSingleUrlParam("userId");
            Device device = DeviceUtil.fromString(handshake.getSingleUrlParam("device"));
            logger.info("onDisConnect : Client[{}] userId[{}] disconnect", socketIOClient.getSessionId(), userId);
            localRedis.removeConnect(userId, device);
            onlineService.disconnect(userId,device);
        };
    }

    /**
     * 真正的登出
     * 将localRedis删除，但将online设置为 disconnect
     *
     * @return null
     */
    private DataListener<Void> onLeave(){
        return (client, data, ackSender) -> {
            HandshakeData handshake = client.getHandshakeData();
            String userId = handshake.getSingleUrlParam("userId");
            Device device = DeviceUtil.fromString(handshake.getSingleUrlParam("device"));
            logger.info("onLeave : user[{}] logout ,leave ",userId);
            localRedis.removeConnect(userId, device);
            onlineService.removeOnlineDevice(userId,device);
            client.disconnect();
        };
    }

    /**
     * Client 准备好了密钥，发送hello消息，确认连接成功
     * 逻辑续 {@link #onConnect()}
     * 3. local redis记录
     * 4. 离线消息
     * 5. 可能有离线通知
     *
     * @return 离线消息
     */
    private DataListener<Void> onHello() {
        return (socketIOClient, data, ackRequest) -> {
            HandshakeData handshake = socketIOClient.getHandshakeData();
            String userId = handshake.getSingleUrlParam("userId");
            Device device = DeviceUtil.fromString(handshake.getSingleUrlParam("device"));
            String token = handshake.getSingleUrlParam("token");

//            byte[] decryptData = AESEncryptor.decrypt(data, localRedis.getKey(userId, device));
            // check key for decry ping
//            if (Arrays.equals(decryptData, "hello".getBytes())) {
            String host = InetAddress.getLocalHost().getHostAddress();
            onlineService.connect(userId, device);

            List<Byte[]> offlineMsgs = offlineService.getOfflineMsgs(userId);
            List<Notification> pushMsgs = pushService.getOfflineNotificationByUserId(userId);
            logger.info("onHello : off[{}] \n push[{}]",offlineMsgs,pushMsgs);

            if (pushMsgs != null && !pushMsgs.isEmpty()) {
                for (Notification pushMsg : pushMsgs) {
                    forwardService.addNotification(userId, pushMsg);
                }
                logger.info("onHello : user[{}] get offline push",userId);
            }

            if (offlineMsgs != null && !offlineMsgs.isEmpty()) {
                logger.info("onHello : user[{}] get offline msg :[{}] ",userId,offlineMsgs);
                ackRequest.sendAckData(offlineMsgs);
            } else {
                logger.info("onHello : user[{}] get hello ",userId);
                ackRequest.sendAckData("hello");
            }
        };
    }
}
