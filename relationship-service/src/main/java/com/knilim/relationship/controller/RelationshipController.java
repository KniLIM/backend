package com.knilim.relationship.dao.impl;

import com.knilim.data.model.Friendship;
import com.knilim.data.model.Notification;
import com.knilim.data.utils.NotificationType;
import com.knilim.relationship.dao.RelationshipRepository;
import com.knilim.service.ForwardService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



@Repository
public class RelationshipRepositoryImpl implements RelationshipRepository {
    private JdbcTemplate jdbcTemplate;

    @Reference
    private ForwardService forwardService;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Friendship getFriendshipByUidAndFriend(String uid, String friend){
        String sql2 = String.format("select * from IM.friendship where uid = '%s' and friend = '%s'", uid, friend);
        return jdbcTemplate.queryForObject(sql2, new BeanPropertyRowMapper<>(Friendship.class));
    }

    @Override
    public boolean insert(String uid, String friend, String fName, Boolean state) {
        if(state){
            //好友表插正反两个
            String sql1 = String.format("insert into IM.friendship " +
                    "(uid, friend) values ('%s', '%s')", uid, friend);

            String sql2 = String.format("insert into IM.friendship " +
                    "(uid, friend) values ('%s', '%s')", friend, uid);

            //发送成功通知
            if(jdbcTemplate.update(sql1) == 1 && jdbcTemplate.update(sql2) == 1){
                forwardService.addNotification(friend,
                    new Notification(
                            uid, friend, NotificationType.N_FRIEND_ADD_RESULT,
                            "yes," + fName,
                            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date().getTime()))
                 );
            return true;
            } else return false;
        }else {
            //发送失败通知
            forwardService.addNotification(friend,
                    new Notification(
                            uid, friend, NotificationType.N_FRIEND_ADD_RESULT,
                            "no," + fName,
                            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date().getTime()))
            );
            return true;
        }
    }

    @Override
    public boolean delete(String uid, String friend) {
        Friendship friendship = getFriendshipByUidAndFriend(friend, uid);

        String sql1 = String.format("delete from IM.friendship where uid = '%s' and friend = '%s'", uid, friend);
        String sql2 = String.format("delete from IM.friendship where uid = '%s' and friend = '%s'", friend, uid);
        if(jdbcTemplate.update(sql1) == 1 && jdbcTemplate.update(sql2) == 1){
            //发送删除好友通知
            forwardService.addNotification(friend,
                    new Notification(
                            friend, uid, NotificationType.N_FRIEND_DELETE_RESULT,
                            friendship.getNickname(),
                            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date().getTime()))
            );
            return true;
        }else return false;
    }

    @Override
    public Friendship update(String uid, String friend, String nickname, Boolean isTop, Boolean isBlack) {
        if(uid == null || friend == null) return null;
        String plugin = "";
        if (nickname != null) plugin += String.format("nickname = '%s'", nickname);
        if (isTop != null) plugin += String.format("isTop = '%s'", isTop);
        if (isBlack != null) plugin += String.format("isBlack = '%s'", isBlack);
        plugin = plugin.substring(1);
        String sql1 = String.format("update IM.friendship set %s where uid = '%s' and friend = '%s'", plugin, uid, friend);
        try {
            if (jdbcTemplate.update(sql1) != 1) return null;
            String sql2 = String.format("select * from IM.friendship where uid = '%s' and friend = '%s'", uid, friend);
            return jdbcTemplate.queryForObject(sql2, new BeanPropertyRowMapper<>(Friendship.class));
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public Friendship updateNickname(String uid, String friend, String nickname) {
        if(uid == null || friend == null) return null;
        String sql1 = String.format("update IM.friendship set nickname = '%s' where uid = '%s' and friend = '%s'", nickname, uid, friend);
        try {
            if (jdbcTemplate.update(sql1) != 1) return null;
            String sql2 = String.format("select * from IM.friendship where uid = '%s' and friend = '%s'", uid, friend);
            return jdbcTemplate.queryForObject(sql2, new BeanPropertyRowMapper<>(Friendship.class));
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public Friendship updateIsTop(String uid, String friend, Boolean isTop) {
        if(uid == null || friend == null) return null;
        String sql1 = String.format("update IM.friendship set is_top = '%s' where uid = '%s' and friend = '%s'", isTop, uid, friend);
        try {
            if (jdbcTemplate.update(sql1) != 1) return null;
            String sql2 = String.format("select * from IM.friendship where uid = '%s' and friend = '%s'", uid, friend);
            return jdbcTemplate.queryForObject(sql2, new BeanPropertyRowMapper<>(Friendship.class));
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public Friendship updateIsBlack(String uid, String friend, Boolean isBlack) {
        if(uid == null || friend == null) return null;
        String sql1 = String.format("update IM.friendship set is_black = '%s' where uid = '%s' and friend = '%s'", isBlack, uid, friend);
        try {
            if (jdbcTemplate.update(sql1) != 1) return null;
            String sql2 = String.format("select * from IM.friendship where uid = '%s' and friend = '%s'", uid, friend);
            return jdbcTemplate.queryForObject(sql2, new BeanPropertyRowMapper<>(Friendship.class));
        } catch (DataAccessException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<tempFriend> getFriendsByUserId(String uid) {
        try {
            return jdbcTemplate.query("select IM.friendship.uid as uid, friend, is_black, is_top, avatar, IM.friendship.created_at as created_at, IM.friendship.nickname as f_nickname, IM.user.nickname as u_nickname from IM.friendship join IM.user on IM.friendship.uid = IM.user.id where uid = ?",
                    new Object[]{uid},
                    (RowMapper) (rs, rowNum) -> {
                        tempFriend friendship  = new tempFriend();
                        friendship.setUid(rs.getString("uid"));
                        friendship.setFriend(rs.getString("friend"));
                        friendship.setNickname(rs.getString("f_nickname") != null ? rs.getString("f_nickname"): rs.getString("u_nickname"));
                        friendship.setIsBlack(rs.getBoolean("is_black") != null ? rs.getBoolean("is_black") : false);
                        friendship.setIsTop(rs.getBoolean("is_top") != null ? rs.getBoolean("is_top") : false);
                        friendship.setCreatedAt(rs.getString("created_at"));
                        friendship.setAvatar(rs.getString("avatar"));
                        return friendship;
                    });
        }catch (DataAccessException e){
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Friendship> getFriendsByUserIdRPC(String uid) {
        try {
            return jdbcTemplate.query("select IM.friendship.uid as uid, friend, is_black, is_top, IM.friendship.created_at as created_at, IM.friendship.nickname as f_nickname, IM.user.nickname as u_nickname from IM.friendship join IM.user on IM.friendship.uid = IM.user.id where uid = ?",
                    new Object[]{uid},
                    (RowMapper) (rs, rowNum) -> {
                        tempFriend friendship  = new tempFriend();
                        friendship.setUid(rs.getString("uid"));
                        friendship.setFriend(rs.getString("friend"));
                        friendship.setNickname(rs.getString("f_nickname") != null ? rs.getString("f_nickname"): rs.getString("u_nickname"));
                        friendship.setIsBlack(rs.getBoolean("is_black") != null ? rs.getBoolean("is_black") : false);
                        friendship.setIsTop(rs.getBoolean("is_top") != null ? rs.getBoolean("is_top") : false);
                        friendship.setCreatedAt(rs.getString("created_at"));
                        return friendship;
                    });
        }catch (DataAccessException e){
            return null;
        }
    }

    @Override
    public void addApplication(String friendId, String useId, String uName, String instruction){
        forwardService.addNotification(friendId,
                new Notification(
                        friendId, useId, NotificationType.N_FRIEND_ADD_APPLICATION,
                        String.format("%s,%s", uName, instruction),
                        new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date().getTime()))
        );
    }



    
}
