package com.knilim.group.dao.impl;

import com.knilim.data.model.Group;
import com.knilim.group.dao.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcGroupRepository implements GroupRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean insert(Group group) {
        // 群组表新建群
        String sql1 = String.format("insert into IM.group " +
                        "(id, owner, name, avatar, signature) values ('%s', '%s', '%s', '%s', '%s')",
                group.getId(), group.getOwner(), group.getName(), group.getAvatar(), group.getSignature());
        // 群组关系表添加关系
        String sql2 = String.format("insert into IM.groupship" +
                "(uid, gid, ) values ('%s, '%s')", group.getOwner(), group.getId());
        return jdbcTemplate.update(sql1) == 1 && jdbcTemplate.update(sql2) == 1;
    }

    @Override
    public boolean delete(String groupId) {
        String sql = String.format("delete from IM.group where id = '%s'", groupId);
        return jdbcTemplate.update(sql) == 1;
    }

    @Override
    public Group getInfo(String groupId) throws DataAccessException {
        String sql = String.format("select * from IM.group where id = '%s'", groupId);
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Group.class));
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public Group update(String groupId, String name, String avatar, String signature, String announcement)
            throws DataAccessException{
        // 首先修改群信息
        String plugin = "";
        if (name != null) plugin += String.format(", name = '%s'", name);
        if (avatar != null) plugin += String.format(", avatar = '%s'", avatar);
        if (signature != null) plugin += String.format(", signature = '%s'", signature);
        if (announcement != null) plugin += String.format(", announcement = '%s'", announcement);
        plugin = plugin.substring(1);
        String sql1 = String.format("update table IM.group set %s where id = '%s'", plugin, groupId);
        if(jdbcTemplate.update(sql1) != 1) return null;
        // 获取该群最新的所有信息
        try {
            String sql2 = String.format("select * from IM.group where id = '%s'", groupId);
            return jdbcTemplate.queryForObject(sql2, new BeanPropertyRowMapper<>(Group.class));
        } catch (DataAccessException e) {
            return null;
        }
    }
}
