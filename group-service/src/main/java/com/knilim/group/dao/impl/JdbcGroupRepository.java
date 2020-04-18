package com.knilim.group.dao.impl;

import com.knilim.data.model.Group;
import com.knilim.group.dao.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        String sql1 = String.format("insert into IM.group " +
                "(id, owner, name, avatar, signature) values ('%s', '%s', '%s', '%s', '%s')",
                group.getId(), group.getOwner(), group.getName(), group.getAvatar(), group.getSignature());
        String sql2 = String.format("insert into IM.groupship" +
                "(uid, gid, ) values ('%s, '%s')", group.getOwner(), group.getId());
        return jdbcTemplate.update(sql1) == 1 && jdbcTemplate.update(sql2) == 1;
    }
}
