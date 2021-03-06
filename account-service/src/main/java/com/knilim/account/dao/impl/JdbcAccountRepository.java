package com.knilim.account.dao.impl;

import com.knilim.account.dao.AccountRepository;
import com.knilim.data.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcAccountRepository implements AccountRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public boolean insert(User user) {
        String sql = String.format("insert into IM.user (id, email, phone, password, nickname, sex, birthday, avatar, location, signature) values ('%s', '%s', '%s','%s','%s', %b, '%s', '%s', '%s', '%s')", user.getId(), user.getEmail(), user.getPhone(), user.getPassWord(), user.getNickName(), user.getSex(), user.getBirthday(), user.getAvatar(), user.getLocation(), user.getSignature());
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
  
    public boolean updateUserInformation(User user) {
        String sql = String.format("update IM.user set email='%s', phone='%s', nickname='%s', avatar='%s', sex=%b, " +
                "signature='%s', location='%s', birthday='%s' where id='%s'", user.getEmail(), user.getPhone(), user.getNickName(),
                user.getAvatar(), user.getSex(), user.getSignature(), user.getLocation(), user.getBirthday(), user.getId());
        return jdbcTemplate.update(sql) == 1;
    }

    @Override
    public boolean changePassword(String id, String oldPassword, String newPassword) {
        String vSql = String.format("select password from IM.user where id = '%s'",id);
        String getPassword = jdbcTemplate.queryForObject(vSql,String.class);
        if(!getPassword.equals(oldPassword)) return false;
        String sql = String.format("update IM.user set password='%s' where id='%s'",newPassword, id);
        return jdbcTemplate.update(sql) == 1;
    }

    @Override
    public User searchById(String id) {
        try {
            String sql = String.format("select * from IM.user where id='%s'", id);
            RowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
            return jdbcTemplate.queryForObject(sql, rowMapper);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<User> searchByKeyword(String keyword) {
        String sql = String.format("select * from IM.user where email='%s' or phone='%s' or nickname like '%%%s%%' ",keyword,keyword,keyword);
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
        User user;
        try {
            return jdbcTemplate.query(sql, rowMapper);
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public boolean exsists(String keyword) {
        String sql = String.format("select * from IM.user where email='%s' or phone='%s'",keyword,keyword);
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
        User user;
        try {
            user = jdbcTemplate.queryForObject(sql, rowMapper);
        } catch (DataAccessException e) {
            user = null;
        }
        return user != null;
    }

    @Override
    public boolean checkPassword(String account, String password) {
        String sql = String.format("Select password from IM.user where phone = '%s' or email = '%s'",account,account);
        String getPassword = jdbcTemplate.queryForObject(sql,String.class);
        return password.equals(getPassword);
    }
}
