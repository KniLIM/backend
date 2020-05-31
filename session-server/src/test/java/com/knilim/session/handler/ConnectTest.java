package com.knilim.session.handler;

import com.knilim.data.utils.Device;
import com.knilim.service.OfflineService;
import com.knilim.service.OnlineService;
import com.knilim.session.dao.ClientDao;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

@SpringBootTest
@DisplayName("Test connect")
@ExtendWith(SpringExtension.class)
public class ConnectTest {

    @Reference
    private OnlineService onlineService;

    @Reference
    private OfflineService offlineService;


    @Resource
    private ClientDao localRedis;

    @Test
    void addData(){
        onlineService.addOnlineDevice("123456789", Device.D_PC,"qwertyuiop","localhost",3000);
    }
}
