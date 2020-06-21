package com.knilim.session.controller;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.knilim.data.utils.Device;
import com.knilim.session.dao.ClientDao;
import com.knilim.session.data.AESEncryptor;
import com.knilim.session.model.Connect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;
import java.util.UUID;

@RestController
public class ForwardController {

    private static final Logger logger = LoggerFactory.getLogger(ForwardController.class);

    private SocketIONamespace nps;

    @Resource
    private ClientDao dao;

    @Autowired
    public ForwardController(SocketIOServer server) {
        this.nps = server.getNamespace("/sockets");
    }

    @PostMapping("/forward")
    public void forward(@RequestBody String body) {
        try {
            JSONObject object = JSONObject.parseObject(body);
            logger.info("ForwardController: forward  body[{}]",body);
            String rcvId = object.getString("rcvId");
            byte[] data = object.getString("data").getBytes();

            Map<Device, Connect> connects = dao.getConnectsByUserId(rcvId);
            if (connects != null) {
                connects.forEach((device, connect) -> {
                    String key = dao.getKey(rcvId, device);
                    byte[] encrypted = AESEncryptor.encrypt(data, key);
                    UUID sessionId = UUID.fromString(connect.getSessionId());
                    logger.info("ForwardController: forward encrypted[{}]",encrypted);
                    nps.getClient(sessionId).sendEvent("rcv-msg", encrypted);
                });
            }
        } catch (Exception e) {
            logger.error("Error request body in /forward");
        }
    }

    @PostMapping("/publish")
    public void publish(@RequestBody String body) {
        try {
            JSONObject object = JSONObject.parseObject(body);
            logger.info("ForwardController: forward publish[{}]",body);
            String userId = object.getString("userId");
            byte[] data = object.getString("notification").getBytes();

            Map<Device, Connect> connects = dao.getConnectsByUserId(userId);
            if (connects != null) {
                connects.forEach((device, connect) -> {
                    String key = dao.getKey(userId, device);
                    byte[] encrypted = AESEncryptor.encrypt(data, key);
                    UUID sessionId = UUID.fromString(connect.getSessionId());
                    logger.info("ForwardController: forward encrypted[{}]",encrypted);
                    nps.getClient(sessionId).sendEvent("notification", encrypted);
                });
            }
        } catch (Exception e) {
            logger.error("Error request body in /publish");
        }
    }

//    @GetMapping("/test")
//    public String test() {
//        RestTemplate template = new RestTemplate();
//        String url = "http://localhost:9002/hello";
//
//        Map<String, String> requestBody = new HashMap<>();
//        requestBody.put("rcvId", "3njkleg890");
//        requestBody.put("data", "hello world");
//        return template.postForObject(url, requestBody, String.class) + " test";
//    }
//
//    @PostMapping("/hello")
//    public String hello(@RequestBody String body) {
//        try {
//            JSONObject object = JSONObject.parseObject(body);
//            String rcvId = object.getString("rcvId");
//            String data = object.getString("data");
//            return String.format("%s: %s", rcvId, data);
//        } catch (Exception e) {
//            return null;
//        }
//    }
}
