package com.knilim.account.dao.impl;

import com.knilim.account.dao.AccountRepository;
import com.knilim.data.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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

    @Override
    public User getUserByEmail(String email) {
        String sql = String.format("select * from user where email = '%s'", email);
        return queryForSingleUser(sql);
    }

    @Override
    public User getUserByPhone(String phone) {
        String sql = String.format("select * from user where phone = '%s'", phone);
        return queryForSingleUser(sql);
    }

    private User queryForSingleUser(String sql) {
        RowMapper<User> userRowMapper = new BeanPropertyRowMapper<>(User.class);
        try {
            return jdbcTemplate.queryForObject(sql, userRowMapper);
        } catch (Exception e) {
            return null;
        }
    }
}
