package com.knilim.session.handler;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.google.protobuf.InvalidProtocolBufferException;
import com.knilim.data.model.DeviceInfo;
import com.knilim.data.utils.BytesUtil;
import com.knilim.data.utils.Device;
import com.knilim.service.GroupService;
import com.knilim.service.OfflineService;
import com.knilim.service.OnlineService;
import com.knilim.session.dao.ClientDao;
import com.knilim.session.data.AESEncryptor;
import com.knilim.session.data.RedundanceProto;
import com.knilim.session.model.Connect;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
public class MsgHandler {

    private static final Logger logger = LoggerFactory.getLogger(MsgHandler.class);

    private final SocketIONamespace namespace;

    @Reference
    private OnlineService onlineService;

    @Reference
    private OfflineService offlineService;

    @Reference
    private GroupService groupService;

    @Resource
    private ClientDao dao;

    @Autowired
    public MsgHandler(SocketIOServer server) {
        this.namespace = server.getNamespace("/sockets");
        this.namespace.addEventListener("send-msg",byte[].class, onSendMsg());
    }

    private DataListener<byte[]> onSendMsg() {
        logger.info("starting on send msg");
        return (client, data, ackSender) -> {
            try {

                RedundanceProto.Redundance msg = RedundanceProto.Redundance.parseFrom(data);
                logger.info("onSendMsg : msg:[{}]",msg.toString());
                ackSender.sendAckData("send-ack");
                // 解密
//                String key = dao.getKey(msg.getSender(), DeviceUtil.fromString(msg.getDevice()));
//                byte[] decryptedContent = AESEncryptor.decrypt(msg.getContent().toByteArray(), key);

                byte[] decryptedContent = msg.getContent().toByteArray();
                // 发送消息
                sendMsgToRcv(msg.getSender(), msg.getReceiver(), msg.getMsgType(), decryptedContent);
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
                    logger.info("syncMsgAmongDevices : send msg to User:[{}] sessionId:[{}]",userId,thatSessionId);
                    namespace.getClient(UUID.fromString(thatSessionId)).sendEvent("rcv-msg", encrypted);
                }
            });
        }

//        // 处理连接其他 session 的 client 就一个 session
//        Map<Device, DeviceInfo> globalConnects = onlineService.getDevicesByUserId(userId);
//        if (globalConnects != null) {
//            Collection<DeviceInfo> globalDevices = globalConnects.values();
//            globalDevices.removeIf(device -> device.getSessionServerIp().equals(host));
//            httpForward(globalDevices, userId, data);
//        }
    }

    private void sendMsgToRcv(String sender, String rcv, RedundanceProto.Redundance.MsgType type, byte[] data) {
        // 群聊
        if (type == RedundanceProto.Redundance.MsgType.P2G) {
            logger.info("sendMsgToRcv : msgType: 群聊");
            List<String> onlineMembers = groupService.sendGroupMsg(rcv, BytesUtil.toObj(data));
            logger.info("sendToMembers: {}", onlineMembers);
            onlineMembers.forEach(member -> {
                if (!member.equals(sender)) {
                    forwardMsgToOnlineUser(member, data);
                }
            });
            return;
        }

        // 单聊
        logger.info("sendMsgToRcv : msgType: 单聊");
        Map<Device, DeviceInfo> onlineRcv = onlineService.getDevicesByUserId(rcv);
        if (onlineRcv == null) {
            // offline
            offlineService.addOfflineMsg(rcv, BytesUtil.toObj(data));
        } else {
            // online
            forwardMsgToOnlineUser(rcv, data);
        }
    }

    private void forwardMsgToOnlineUser(String rcvId, byte[] data) {
        Map<Device, Connect> connects = dao.getConnectsByUserId(rcvId);
        if (connects != null) {
            connects.forEach((device, connect) -> {
//                String key = dao.getKey(rcvId, device);
//                byte[] encrypted = AESEncryptor.encrypt(data, key);
                UUID sessionId = UUID.fromString(connect.getSessionId());
                logger.info("httpForward : forward to id:[{}] device:[{}]",rcvId,device);
                namespace.getClient(sessionId).sendEvent("rcv-msg", data);
            });
        } else {
            logger.error("connect is null");
        }
    }
}
