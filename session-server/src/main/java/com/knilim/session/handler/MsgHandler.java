package com.knilim.session.handler;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.knilim.service.OfflineService;
import com.knilim.service.OnlineService;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MsgHandler {
    private static final Logger logger = LoggerFactory.getLogger(MsgHandler.class);

    private SocketIONamespace nps;

    @Reference
    private OnlineService onlineService;

    @Reference
    private OfflineService offlineService;

    @Autowired
    public MsgHandler(SocketIOServer server) {
        this.nps = server.addNamespace("/sockets");
        nps.addEventListener("send-msg", byte[].class, onSendMsg());
    }

    private DataListener<byte[]> onSendMsg() {
        // 返回事件为  send-ack
        return null;
    }

}
