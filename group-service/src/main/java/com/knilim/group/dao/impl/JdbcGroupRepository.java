package com.knilim.group.dao.impl;

import com.knilim.data.model.Group;
import com.knilim.data.model.Notification;
import com.knilim.data.model.User;
import com.knilim.data.utils.NotificationType;
import com.knilim.group.dao.GroupRepository;
import com.knilim.data.model.UserTmp;
import com.knilim.service.ForwardService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Repository
public class JdbcGroupRepository implements GroupRepository {
    private JdbcTemplate jdbcTemplate;

    @Reference
    private ForwardService forwardService;

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
                "(uid, gid, is_admin) values ('%s', '%s', '%d')", group.getOwner(), group.getId(), 1);
        return jdbcTemplate.update(sql1) == 1 && jdbcTemplate.update(sql2) == 1;
    }

    @Override
    public boolean delete(String groupId) {
        // 向该群所有成员发送推送通知
        List<String> users = jdbcTemplate.query(
                "select uid from IM.groupship where gid = ? and is_admin = 0",
                new Object[]{groupId}, (rs, rowNum) -> rs.getString("uid")
        );
        Group group = jdbcTemplate.queryForObject(
                "select * from IM.group where id = ?",
                new Object[]{groupId},
                new BeanPropertyRowMapper<>(Group.class)
        );
        assert group != null;
        for (String user : users) {
            forwardService.addNotification(user,
                    new Notification(
                            groupId, group.getOwner(), NotificationType.N_GROUP_DELETE,
                            group.getName(),
                            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date().getTime()))
            );
        }
        String sql = String.format("delete from IM.group where id = '%s'", groupId);
        return jdbcTemplate.update(sql) == 1;
    }

    @Override
    public Group getInfo(String groupId) throws DataAccessException {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from IM.group as g, IM.user as u " +
                            "where g.owner = u.id and g.id = ?",
                    new Object[]{groupId},
                    (rs, rowNum) ->
                            new Group(
                                    rs.getString("g.id"),
                                    rs.getString("u.nickname"),
                                    rs.getString("g.name"),
                                    rs.getString("g.avatar"),
                                    rs.getString("g.signature"),
                                    rs.getString("g.announcement"),
                                    new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format
                                            (rs.getTimestamp("created_at"))
                            )
            );
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public Group update(String groupId, String name, String avatar, String signature, String announcement)
            throws DataAccessException {
        // 首先修改群信息
        String plugin = "";
        if (name != null) plugin += String.format(", name = '%s'", name);
        if (avatar != null) plugin += String.format(", avatar = '%s'", avatar);
        if (signature != null) plugin += String.format(", signature = '%s'", signature);
        if (announcement != null) plugin += String.format(", announcement = '%s'", announcement);
        plugin = plugin.substring(1);
        String sql1 = String.format("update IM.group set %s where id = '%s'", plugin, groupId);
        if (jdbcTemplate.update(sql1) != 1) return null;
        // 获取该群最新的所有信息
        try {
            return jdbcTemplate.queryForObject(
                    "select * from IM.group as g, IM.user as u " +
                            "where g.owner = u.id and g.id = ?",
                    new Object[]{groupId},
                    (rs, rowNum) ->
                            new Group(
                                    rs.getString("g.id"),
                                    rs.getString("u.nickname"),
                                    rs.getString("g.name"),
                                    rs.getString("g.avatar"),
                                    rs.getString("g.signature"),
                                    rs.getString("g.announcement"),
                                    new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format
                                            (rs.getTimestamp("created_at"))
                            )
            );
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Group> getGroupsByUserId(String userId) throws DataAccessException {
        try {
            return jdbcTemplate.query(
                    "select * from IM.group as a, IM.groupship as b where a.id = b.gid and b.uid = ?",
                    new Object[]{userId},
                    (rs, rowNum) ->
                            new Group(
                                    rs.getString("a.id"),
                                    rs.getString("a.owner"),
                                    rs.getString("a.name"),
                                    rs.getString("a.avatar"),
                                    rs.getString("a.signature"),
                                    rs.getString("a.announcement"),
                                    new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format
                                            (rs.getTimestamp("a.created_at"))
                            )
            );
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Group> getGroupsByKeyword(String Keyword) {
        return jdbcTemplate.query(
                "select * from IM.group as g, IM.user as u " +
                        "where g.owner = u.id and (name like ? or g.signature like ?)",
                new Object[]{"%" + Keyword + "%", "%" + Keyword + "%"},
                (rs, rowNum) ->
                        new Group(
                                rs.getString("g.id"),
                                rs.getString("u.nickname"),
                                rs.getString("g.name"),
                                rs.getString("g.avatar"),
                                rs.getString("g.signature"),
                                rs.getString("g.announcement"),
                                new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format
                                        (rs.getTimestamp("created_at"))
                        )
        );
    }

    @Override
    public List<UserTmp> getMembers(String groupId) {
        // 直接联表查询groupship表和user表即可
        return jdbcTemplate.query(
                "select * from IM.groupship, IM.user where uid = id and gid = ?",
                new Object[]{groupId},
                (rs, rowNum) ->
                        new UserTmp(
                                rs.getString("id"),
                                rs.getString("nickname"),
                                rs.getString("avatar"),
                                rs.getString("memo"),
                                rs.getBoolean("is_admin")
                        )
        );
    }

    @Override
    public boolean participation(String groupId, String userId, String comment) {
        // 首先获取该群组信息和该用户信息
        Group group = jdbcTemplate.queryForObject(
                "select * from IM.group where id = ?",
                new Object[]{groupId},
                new BeanPropertyRowMapper<>(Group.class)
        );
        User user = jdbcTemplate.queryForObject(
                "select * from IM.user where id = ?",
                new Object[]{groupId},
                new BeanPropertyRowMapper<>(User.class)
        );
        // 向群主发送消息推送
        assert group != null && user != null;
        forwardService.addNotification(group.getOwner(),
                new Notification(
                        group.getOwner(), userId, NotificationType.N_GROUP_JOIN_APPLICATION,
                        String.format("%s,%s,%s,%s",user.getNickName(),group.getName(),comment,group.getId()),
                        new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date().getTime()))
        );
        return false;
    }

    @Override
    public boolean handleParticipation(String groupId, String userId, String state) {
        // 根据state不同发送不同的推送通知
        String groupName = jdbcTemplate.queryForObject(
                "select `name` from IM.group where id = ?",
                new Object[]{groupId},
                new BeanPropertyRowMapper<>(String.class)
        );
        if (state.equals("yes")) {
            // 向该用户发送加群成功通知
            forwardService.addNotification(userId,
                    new Notification(
                            groupId, userId, NotificationType.N_GROUP_JOIN_RESULT,
                            "yes," + groupName,
                            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date().getTime()))
            );
            // 将该用户添加到群组关系表
            return jdbcTemplate.update(
                    "insert into IM.groupship (uid, gid, is_admin) values ('?', '?', '?')",
                    userId, groupId, 0
            ) == 1;
        } else if (state.equals("no")) {
            // 向该用户发送加群失败通知
            forwardService.addNotification(userId,
                    new Notification(
                            groupId, userId, NotificationType.N_GROUP_JOIN_RESULT,
                            "no," + groupName,
                            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date().getTime()))
            );
            return true;
        } else
            return false;
    }

    @Override
    public boolean exit(String groupId, String userId) {
        // 向群主发送推送消息
        Group group = jdbcTemplate.queryForObject(
                "select * from IM.group where id = ?",
                new Object[]{groupId},
                new BeanPropertyRowMapper<>(Group.class)
        );
        User user = jdbcTemplate.queryForObject(
                "select * from IM.user where id = ?",
                new Object[]{userId},
                new BeanPropertyRowMapper<>(User.class)
        );
        assert group != null && user != null;
        forwardService.addNotification(group.getOwner(),
                new Notification(
                        group.getOwner(), userId, NotificationType.N_GROUP_WITHDRAW_RESULT,
                        user.getNickName() + "," + group.getName(),
                        new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date().getTime()))
        );
        return jdbcTemplate.update(
                "delete from IM.groupship where gid = ? and uid = ?",
                groupId, userId) == 1;
    }

    @Override
    public boolean expel(String groupId, String userId) {
        // 向该群成员发送推送消息
        Group group = jdbcTemplate.queryForObject(
                "select * from IM.group where id = ?",
                new Object[]{groupId},
                new BeanPropertyRowMapper<>(Group.class)
        );
        assert group != null;
        forwardService.addNotification(userId,
                new Notification(
                        userId, group.getOwner(), NotificationType.N_GROUP_KICKOFF_RESULT,
                        group.getName(),
                        new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date().getTime()))
        );
        return jdbcTemplate.update(
                "delete from IM.groupship where gid = ? and uid = ?",
                groupId, userId) == 1;
    }

    @Override
    public boolean memo(String groupId, String userId, String newNickname) {
        return jdbcTemplate.update(
                "update IM.groupship set memo = ? where gid = ? and uid = ?",
                newNickname, groupId, userId) == 1;
    }
}
