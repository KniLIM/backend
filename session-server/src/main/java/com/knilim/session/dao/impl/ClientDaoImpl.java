package com.knilim.session.dao.impl;

import com.knilim.session.dao.ClientDao;
import com.knilim.session.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.UUID;

@Component
public class ClientDaoImpl implements ClientDao {

    private RedisTemplate<String, Client> template;

    @Autowired
    public ClientDaoImpl(
            @Qualifier("localClientRedisTemplate") RedisTemplate<String, Client> template) {
        this.template = template;
    }

    @Override
    public ArrayList<UUID> getClientsByUserId(UUID userId) {
        return null;
    }

    @Override
    public void addClient(UUID userId, UUID sessionId) {

    }

    @Override
    public void removeClient(UUID userId, UUID sessionId) {

    }
}
