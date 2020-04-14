package com.knilim.data.dao.impl;

import com.knilim.data.dao.OfflineDao;
import com.knilim.data.model.Offline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class OfflineDaoImpl implements OfflineDao {

    private MongoTemplate template;

    @Autowired
    public OfflineDaoImpl(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public List<Byte[]> getOfflineMsgs(UUID userId) {
        Offline result = template.findById(userId, Offline.class);

        if (result == null) {
            return new ArrayList<>();
        } else {
            Query query = new Query(Criteria.where("user_id").is(userId));
            template.remove(query, Offline.class);
            return result.getMsgs();
        }
    }

    @Override
    public void addOfflineMsg(UUID userId, Byte[] msg) {
        Offline result = template.findById(userId, Offline.class);

        if (result == null) {
            Offline offline = new Offline(userId);
            offline.addMsg(msg);
            template.save(offline);
        } else {
            result.addMsg(msg);
            Query query = new Query(Criteria.where("user_id").is(userId));
            Update update = Update.update("msgs", result);
            template.updateFirst(query, update, Offline.class);
        }
    }
}
