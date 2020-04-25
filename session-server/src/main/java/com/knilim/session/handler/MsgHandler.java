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
import com.knilim.session.data.MsgProto;
import com.knilim.session.model.Connect;
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

    private SocketIONamespace namespace;

    @Reference
    private OnlineService onlineService;

    @Reference
    private OfflineService offlineService;

//    @Reference
//    private GroupService groupService;

    @Resource
    private ClientDao dao;

    private String host;

    @Autowired
    public MsgHandler(SocketIOServer server) {
        this.namespace = server.getNamespace("/sockets");
        this.namespace.addEventListener("send-msg", byte[].class, onSendMsg());

        try {
            this.host = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
            this.host = "";
        }
    }

    private DataListener<byte[]> onSendMsg() {
        return (client, data, ackSender) -> {
            try {
                MsgProto.Msg msg = MsgProto.Msg.parseFrom(data);
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

    private void sendMsgToRcv(String rcv, MsgProto.Msg.MsgType type, byte[] data) {
        // 群聊
//        if (type == MsgProto.Msg.MsgType.P2G) {
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
        RestTemplate template = new RestTemplate();
        Set<String> endpoints = new HashSet<>();

        devices.forEach(device -> {
            String ip = device.getSessionServerIp();
            Integer port = device.getSessionServerPort();
            String endpoint = String.format("http://%s:%d/forward", ip, port);

            if (!endpoints.contains(endpoint)) {
                endpoints.add(endpoint);

                Map<String, String> requestBody = new HashMap<>();
                requestBody.put("rcvId", rcvId);
                requestBody.put("data", new String(data));
                template.postForObject(endpoint, requestBody, Void.class);
            }
        });
    }
}
