package com.knilim.session;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
//import com.knilim.service.ForwardService;
//import com.knilim.data.utils.Tuple;
//import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

//@Service
@Component
public class ForwardServiceImpl  //implements ForwardService {
{

    private SocketIONamespace nps;

    @Autowired
    public ForwardServiceImpl(SocketIOServer server) {
        this.nps = server.getNamespace("/sockets");
    }

//    @Override
//    public Tuple<String, Integer> getAvailableSession() {
//        return null;
//    }
//
//    @Override
//    public void forward(UUID rcvId, Byte[] data) {
//
//    }
}
