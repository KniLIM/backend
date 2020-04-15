package com.knilim.data.dao.impl;

import com.knilim.data.dao.OfflineDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class OfflineDaoImpl implements OfflineDao {

    private RedisTemplate<String, Byte[]> offlineTemplate;

    @Autowired
    public OfflineDaoImpl(RedisTemplate<String, Byte[]> offlineTemplate) {
        this.offlineTemplate = offlineTemplate;
    }

    @Override
    public List<Byte[]> getOfflineMsgs(UUID userId) {
        List<Byte[]> msgs = offlineTemplate.boundListOps(userId.toString()).range(0, -1);
        if (msgs != null) {
            offlineTemplate.delete(userId.toString());
            return msgs;
        }

        return new ArrayList<>();
    }

    @Override
    public void addOfflineMsg(UUID userId, Byte[] msg) {
        offlineTemplate.boundListOps(userId.toString()).rightPush(msg);
    }
}
