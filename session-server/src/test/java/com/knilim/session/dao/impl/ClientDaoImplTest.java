package com.knilim.session.dao.impl;

import com.knilim.session.dao.ClientDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

@SpringBootTest
@DisplayName("Test client Dao")
@ExtendWith(SpringExtension.class)
class ClientDaoImplTest {

    @Resource
    private ClientDao dao;

    @Test
    void get() {
    }

    @Test
    void remove() {
    }

    @Test
    void add() {
    }
}