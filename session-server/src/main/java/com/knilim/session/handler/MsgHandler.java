package com.knilim.session.handler;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.google.protobuf.InvalidProtocolBufferException;
import com.knilim.data.model.DeviceInfo;
import com.knilim.data.utils.BytesUtil;
import com.knilim.data.utils.Device;
import com.knilim.data.utils.DeviceUtil;
//import com.knilim.service.GroupService;
import com.knilim.service.OfflineService;
import com.knilim.service.OnlineService;
import com.knilim.session.dao.ClientDao;
import com.knilim.session.data.AESEncryptor;
import com.knilim.session.data.RedundanceProto;
import com.knilim.session.model.Connect;
import com.knilim.session.utils.HostManager;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.util.*;

@Component
public class MsgHandler {

    private static final Logger logger = LoggerFactory.getLogger(MsgHandler.class);

    private final SocketIONamespace namespace;

    @Reference
    private OnlineService onlineService;

    @Reference
    private OfflineService offlineService;

//    @Reference
//    private GroupService groupService;

    @Resource
    private ClientDao dao;

    private final String host;

    @Autowired
    public MsgHandler(SocketIOServer server) {
        this.namespace = server.getNamespace("/sockets");
        this.namespace.addEventListener("send-msg",byte[].class, onSendMsg());
        this.host = HostManager.getInstance().getHost();
    }

    private DataListener<byte[]> onSendMsg() {
        logger.info("starting on send msg");
        return (client, data, ackSender) -> {
            try {
                logger.info("data[{}] ",data);
                RedundanceProto.Redundance msg = RedundanceProto.Redundance.parseFrom(data);
                logger.info(msg.toString());
                ackSender.sendAckData("send-ack");
                // 解密
                String key = dao.getKey(msg.getSender(), DeviceUtil.fromString(msg.getDevice()));
                byte[] decryptedContent = AESEncryptor.decrypt(msg.getContent().toByteArray(), key);
                // 发送消息
                sendMsgToRcv(msg.getReceiver(), msg.getMsgType(), decryptedContent);
                // 在线设备消息同步
                syncMsgAmongDevices(msg.getSender(), decryptedContent, client.getSessionId().toString());
            } catch (InvalidProtocolBufferException e) {
                ackSender.sendAckData("send-error");
                logger.error(e.getMessage());
            }
        };
    }

    private void syncMsgAmongDevices(String userId, byte[] data, String thisSessionId) {
        // 处理连接本 session server 的 client
        Map<Device, Connect> localConnects = dao.getConnectsByUserId(userId);
        if (localConnects != null && localConnects.size() > 1) {
            // 转发给非自身的其他 client
            localConnects.values().forEach(connect -> {
                String thatSessionId = connect.getSessionId();
                if (!thatSessionId.equals(thisSessionId)) {
                    String key = connect.getKey();
                    byte[] encrypted = AESEncryptor.encrypt(data, key);
                    namespace.getClient(UUID.fromString(thatSessionId)).sendEvent("rcv-msg", encrypted);
                }
            });
        }

        // 处理连接其他 session 的 client
        Map<Device, DeviceInfo> globalConnects = onlineService.getDevicesByUserId(userId);
        if (globalConnects != null) {
            Collection<DeviceInfo> globalDevices = globalConnects.values();
            globalDevices.removeIf(device -> device.getSessionServerIp().equals(host));
            httpForward(globalDevices, userId, data);
        }
    }

    private void sendMsgToRcv(String rcv, RedundanceProto.Redundance.MsgType type, byte[] data) {
        // 群聊
//        if (type == RedundanceProto.Redundance.MsgType.P2G) {
//            groupService.sendGroupMsg(rcv, BytesUtil.toObj(data));
//            return;
//        }

        // 单聊
        Map<Device, DeviceInfo> onlineRcv = onlineService.getDevicesByUserId(rcv);
        if (onlineRcv == null) {
            // offline
            offlineService.addOfflineMsg(rcv, BytesUtil.toObj(data));
        } else {
            // online
            httpForward(onlineRcv.values(), rcv, data);
        }
    }

    private void httpForward(Collection<DeviceInfo> devices, String rcvId, byte[] data) {
        Map<Device, Connect> connects = dao.getConnectsByUserId(rcvId);
        if (connects != null) {
            logger.info("send to connected device");
            connects.forEach((device, connect) -> {
                String key = dao.getKey(rcvId, device);
                byte[] encrypted = AESEncryptor.encrypt(data, key);
                UUID sessionId = UUID.fromString(connect.getSessionId());
                logger.info("ForwardController: forward to id:[{}] device:[{}]",rcvId,device);
                namespace.getClient(sessionId).sendEvent("rcv-msg", encrypted);
            });
        } else {
            logger.error("connect is null");
        }
    }
}
