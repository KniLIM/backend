package com.knilim.data.dao.impl;

import com.knilim.data.dao.OfflineDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Test Offline Dao")
@ExtendWith(SpringExtension.class)
class OfflineDaoImplTest {

    @Resource
    private OfflineDao dao;

    @Test
    void getOfflineMsgs() {
    }

    @Test
    void addOfflineMsg() {
    }
}