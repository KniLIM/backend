package com.knilim.session.handler;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.google.protobuf.InvalidProtocolBufferException;
import com.knilim.data.model.DeviceInfo;
import com.knilim.data.utils.BytesUtil;
import com.knilim.data.utils.Device;
import com.knilim.data.utils.DeviceUtil;
import com.knilim.service.GroupService;
import com.knilim.service.OfflineService;
import com.knilim.service.OnlineService;
import com.knilim.session.dao.ClientDao;
import com.knilim.session.data.AESEncryptor;
import com.knilim.session.data.MsgProto;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class MsgHandler {

    private static final Logger logger = LoggerFactory.getLogger(MsgHandler.class);

    private SocketIONamespace namespace;

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
        this.namespace.addEventListener("send-msg", byte[].class, onSendMsg());
    }

    private DataListener<byte[]> onSendMsg() {
        return (client, data, ackSender) -> {
            try {
                MsgProto.Msg msg = MsgProto.Msg.parseFrom(data);
                ackSender.sendAckData("send-ack");

                // 解密
                String key = dao.getKey(msg.getSender(), DeviceUtil.fromString(msg.getDevice()));
                Byte[] decryptedContent = BytesUtil.toObj(AESEncryptor.decrypt(msg.getContent().toByteArray(), key));

                // 群聊
                if (msg.getMsgType() == MsgProto.Msg.MsgType.P2G) {
                    groupService.sendGroupMsg(msg.getReceiver(), decryptedContent);
                    return;
                }

                // 单聊
                Map<Device, DeviceInfo> online = onlineService.getDevicesByUserId(msg.getSender());
                if (online == null) {
                    // offline
                    offlineService.addOfflineMsg(msg.getSender(), decryptedContent);
                } else {
                    // online
                    Set<String> endpoints = new HashSet<>();

                    for (DeviceInfo device: online.values()) {
                        String endpoint = String.format("%s:%d",
                                device.getSessionServerIp(), device.getSessionServerPort());
                        if (!endpoints.contains(endpoint)) {
                            endpoints.add(endpoint);

                            // TODO: 转发到指定 session server
                            System.out.println("forward");
                        }
                    }
                }
            } catch (InvalidProtocolBufferException e) {
                ackSender.sendAckData("send-error");
            }
        };
    }
}
