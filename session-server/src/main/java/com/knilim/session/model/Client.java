package com.knilim.session.model;

import com.knilim.data.utils.Device;
import org.springframework.data.annotation.Id;
import java.io.Serializable;
import java.util.HashMap;


public class Client implements Serializable {

    @Id
    private String userId;

    private HashMap<Device,Connect> connectHashMap;


    public Client(String userId) {
        this.userId = userId;
        this.connectHashMap = new HashMap<Device,Connect>();
    }

    public Client(String userId, HashMap<Device,Connect> connectHashMap) {
        this.userId = userId;
        this.connectHashMap = connectHashMap;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public HashMap<Device, Connect> getConnectHashMap() {
        return connectHashMap;
    }

    public void setConnectHashMap(HashMap<Device, Connect> connectHashMap) {
        this.connectHashMap = connectHashMap;
    }

    public void addConnect(Device device,String sessionId, String host, Integer port, String key) {
        this.connectHashMap.put(device,new Connect(sessionId, host, port, key));
    }

    public void removeConnect(Device device) {
        this.connectHashMap.remove(device);
    }

    @Override
    public String toString() {
        return "Client{" +
                "userId='" + userId + '\'' +
                ", connectHashMap=" + connectHashMap +
                '}';
    }
}
