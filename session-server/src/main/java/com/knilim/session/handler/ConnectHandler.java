package com.knilim.session.handler;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.knilim.service.OfflineService;
import com.knilim.service.OnlineService;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ConnectHandler {
    private static final Logger logger = LoggerFactory.getLogger(ConnectHandler.class);

    private SocketIONamespace nps;

    @Reference
    private OnlineService onlineService;

    @Reference
    private OfflineService offlineService;

    @Autowired
    public ConnectHandler(SocketIOServer server) {
        this.nps = server.getNamespace("/sockets");
        nps.addConnectListener(onConnect());
        nps.addDisconnectListener(onDisConnect());
    }

    private ConnectListener onConnect(){
        // 返回事件为  connect-ack
        return socketIOClient -> {
            HandshakeData handshake = socketIOClient.getHandshakeData();
            String p = handshake.getUrlParams().get("p").get(0);
            String a = handshake.getUrlParams().get("a").get(0);
            String token = handshake.getUrlParams().get("token").get(0);

            // Todo
        };
    }

    private DisconnectListener onDisConnect(){
        return null;
    }


}
