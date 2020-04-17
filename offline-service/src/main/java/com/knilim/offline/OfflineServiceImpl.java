package com.knilim.offline;

import com.knilim.service.OfflineService;
import org.apache.dubbo.config.annotation.Service;
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

    @Override
    public List<Byte[]> getOfflineMsgs(String userId) {
        List<Byte[]> msgs = template.boundListOps(userId).range(0, -1);
        if (msgs != null) {
            template.delete(userId);
            return msgs;
        }

        return new ArrayList<>();
    }

    @Override
    public void addOfflineMsg(String userId, Byte[] msg) {
        template.boundListOps(userId).rightPush(msg);
    }
}
