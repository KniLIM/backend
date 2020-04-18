package com.knilim.account.dao.impl;

import com.knilim.account.dao.AccountRepository;
import com.knilim.data.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcAccountRepository implements AccountRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public boolean insert(User user) {
        String sql = String.format("insert into IM.user (id, email, phone, password, nickname) values ('%s', '%s', '%s','%s','%s')", user.getId(), user.getEmail(), user.getPhone(), user.getPassWord(), user.getNickName());
        return jdbcTemplate.update(sql) == 1;
    }
}
