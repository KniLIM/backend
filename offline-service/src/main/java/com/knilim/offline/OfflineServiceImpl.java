package com.knilim.offline;

import com.knilim.service.OfflineService;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Service
public class OfflineServiceImpl implements OfflineService {

    @Resource
    private RedisTemplate<String, Byte[]> template;

    private static final Logger logger = LoggerFactory.getLogger(OfflineServiceImpl.class);


    @Override
    public List<Byte[]> getOfflineMsgs(String userId) {

        List<Byte[]> msgs = template.boundListOps(userId).range(0, -1);
        logger.info("getOfflineMsgs : from redis msg[{}]",msgs);

        if (msgs != null) {
            template.delete(userId);
            logger.info("getOfflineMsgs : final redis msg[{}]",msgs);
            return msgs;
        }
        logger.info("getOfflineMsgs : final redis msg is empty");
        return new ArrayList<>();
    }

    @Override
    public void addOfflineMsg(String userId, Byte[] msg) {
        template.boundListOps(userId).rightPush(msg);
    }
}
