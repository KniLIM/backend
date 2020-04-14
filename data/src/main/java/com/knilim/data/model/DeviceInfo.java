package com.knilim.data.model;

import com.knilim.data.utils.ConnectStatus;

import java.util.Objects;

public class DeviceInfo {

    // 验证 token
    private String token;

    // 连接的 session server 的 ip
    private String sessionServerIp;

    // 连接的 session server 的端口
    private Integer sessionServerPort;

    private ConnectStatus status;

    public DeviceInfo(String token, String sessionServerIp, Integer sessionServerPort) {
        this.token = token;
        this.sessionServerIp = sessionServerIp;
        this.sessionServerPort = sessionServerPort;
        this.status = ConnectStatus.S_LOGIN;
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
        return this.status == ConnectStatus.S_CONNECT;
    }

    public void connect() {
        this.status = ConnectStatus.S_CONNECT;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" + "token='" + token + '\'' +
                ", sessionServerIp='" + sessionServerIp + '\'' +
                ", sessionServerPort=" + sessionServerPort + '}';
    }

    @Override
    public boolean equals(Object rhs) {
        if (this == rhs) return true;
        if (!(rhs instanceof DeviceInfo)) return false;
        DeviceInfo that = (DeviceInfo) rhs;
        return Objects.equals(token, that.token) &&
                Objects.equals(sessionServerIp, that.sessionServerIp) &&
                Objects.equals(sessionServerPort, that.sessionServerPort) &&
                status == that.status;
    }
}
