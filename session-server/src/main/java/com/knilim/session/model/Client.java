package com.knilim.session.model;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Client implements Serializable {

    @Id
    private UUID userId;

    private List<UUID> clients;

    public Client(UUID userId) {
        this.userId = userId;
        this.clients = new ArrayList<>();
    }

    public UUID getUserId() {
        return userId;
    }

    public List<UUID> getClients() {
        return clients;
    }

    public void addClient(UUID sessionId) {
        this.clients.add(sessionId);
    }

    public void removeClient(UUID sessionId) {
        this.clients.remove(sessionId);
    }

    @Override
    public String toString() {
        return "Client{userId=" + userId + ", clients=" + clients + '}';
    }
}
