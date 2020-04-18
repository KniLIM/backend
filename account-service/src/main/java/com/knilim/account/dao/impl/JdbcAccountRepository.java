package com.knilim.account.dao.impl;

import com.knilim.account.dao.AccountRepository;
import com.knilim.data.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

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
    public boolean updateUserInformation(User user) {
        String sql = String.format("update IM.user set email='%s', phone='%s', nickname='%s', avator='%s', sex='%b', " +
                "signature='%s', location='%s', birthday='%s' where id='%s'", user.getEmail(), user.getPhone(), user.getNickName(),
                user.getAvatar(), user.isSex(), user.getSignature(), user.getLocation(), user.getBirthday(), user.getId());
        return jdbcTemplate.update(sql) == 1;
    }

    @Override
    public boolean updatePassword(User user, String password) {
        String sql = String.format("update IM.user set password='%s' where id='%s'",user.getPassWord(), user.getId());
        return jdbcTemplate.update(sql) == 1;
    }

    @Override
    public User searchById(String id) {
        String sql = String.format("select * from IM.user where id=%s",id);
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
        User user = jdbcTemplate.queryForObject(sql, rowMapper);
        return user;
    }

    @Override
    public User searchByKeyword(String keyword) {
        String sql = String.format("select * from IM.user where email='%s' or phone='%s' or nickname like '%%s%' ",keyword,keyword,keyword);
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
        User user = jdbcTemplate.queryForObject(sql, rowMapper);
        return user;
    }
}
