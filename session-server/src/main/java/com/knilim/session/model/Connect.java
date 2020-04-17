package com.knilim.session.model;

import java.io.Serializable;

public final class Connect implements Serializable {

    private String sessionId;

    private String host;

    private Integer port;

    private String key;


    public Connect(String sessionId, String host, Integer port, String key) {
        this.sessionId = sessionId;
        this.host = host;
        this.port = port;
        this.key = key;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }


    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "Connect{" +
                "sessionId='" + sessionId + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", key='" + key + '\'' +
                '}';
    }
}