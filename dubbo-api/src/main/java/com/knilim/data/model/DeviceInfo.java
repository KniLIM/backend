package com.knilim.data.model;


import java.io.Serializable;
import java.util.Objects;

public class DeviceInfo implements Serializable {

    // 验证 token
    private String token;

    // 连接的 session server 的 ip
    private String sessionServerIp;

    // 连接的 session server 的端口
    private Integer sessionServerPort;

    // true => connect false => login
    private boolean connect;

    public DeviceInfo(String token, String sessionServerIp, Integer sessionServerPort, boolean connect) {
        this.token = token;
        this.sessionServerIp = sessionServerIp;
        this.sessionServerPort = sessionServerPort;
        this.connect = connect;
    }

    public DeviceInfo(String token, String sessionServerIp, Integer sessionServerPort) {
        this(token, sessionServerIp, sessionServerPort, false);
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

    public boolean isConnect() {
        return connect;
    }

    public void connectToSession() {
        connect = true;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" + "token='" + token + '\'' +
                ", sessionServerIp='" + sessionServerIp + '\'' +
                ", sessionServerPort=" + sessionServerPort + '\'' +
                ", connect=" + connect + '}';
    }

    @Override
    public boolean equals(Object rhs) {
        if (this == rhs) return true;
        if (!(rhs instanceof DeviceInfo)) return false;
        DeviceInfo that = (DeviceInfo) rhs;
        return Objects.equals(token, that.token) &&
                Objects.equals(sessionServerIp, that.sessionServerIp) &&
                Objects.equals(sessionServerPort, that.sessionServerPort) &&
                connect == that.connect;
    }
}
