package com.knilim.data.model;

public class DeviceInfo {

    private String token;

    private String sessionServerIp;

    private Integer sessionServerPort;

    public DeviceInfo(String token, String sessionServerIp, Integer sessionServerPort) {
        this.token = token;
        this.sessionServerIp = sessionServerIp;
        this.sessionServerPort = sessionServerPort;
    }

    public String getToken() {
        return token;
    }

    public String getSessionServerIp() {
        return sessionServerIp;
    }

    public Integer getSessionServerPort() {
        return sessionServerPort;
    }
}
