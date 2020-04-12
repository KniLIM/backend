package com.kinlim.session.model;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.UUID;

public final class Connect implements Serializable {
    @Id
    private UUID sessionId;

    private UUID userId;

    private String device;

    private String host;

    private Integer port;

    private String key;

    public Connect(UUID sessionId, UUID userId, String device, String host, Integer port, String key) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.device = device;
        this.host = host;
        this.port = port;
        this.key = key;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getDevice() {
        return device;
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
}
