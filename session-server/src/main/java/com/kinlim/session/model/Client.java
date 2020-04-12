package com.kinlim.session.model;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Client implements Serializable {
    @Id
    private UUID userId;

    private ArrayList<UUID> clients;

    public Client(UUID userId, ArrayList<UUID> clients) {
        this.userId = userId;
        this.clients = clients;
    }

    public UUID getUserId() {
        return userId;
    }

    public ArrayList<UUID> getClients() {
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
