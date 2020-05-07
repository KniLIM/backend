package com.knilim.group;

import com.knilim.data.model.Group;
import com.knilim.data.utils.Tuple;
import com.knilim.data.model.UserTmp;
import com.knilim.service.GroupService;
import com.knilim.service.OfflineService;
import com.knilim.service.OnlineService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    private JdbcTemplate jdbcTemplate;

    @Reference
    private OfflineService offlineService;

    @Reference
    private OnlineService onlineService;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Group getGroupByGroupId(String groupId) throws DataAccessException {
        String sql = String.format("select * from IM.group where id = '%s'", groupId);
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Group.class));
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Group> getGroupsByUserId(String userId) {
        // 首先根据群组关系表得到该用户所有群的id
        String sql1 = String.format("select gid from IM.groupship where uid = '%s'", userId);
        try {
            List<String> gids = jdbcTemplate.query(sql1, new BeanPropertyRowMapper<>(String.class));
            //然后根据所有群id，在群组表里获得这些群组的对象并返回
            return jdbcTemplate.query(
                    "select * from IM.group where id = ?",
                    gids.toArray(),
                    (rs, rowNum) ->
                            new Group(
                                    rs.getString("id"),
                                    rs.getString("owner"),
                                    rs.getString("name"),
                                    rs.getString("avatar"),
                                    rs.getString("signature"),
                                    rs.getString("announcement"),
                                    new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format
                                            (rs.getTimestamp("created_at"))
                            )
            );
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<UserTmp> getMembersByGroupId(String groupId) {
        // 直接联表查询groupship表和user表即可
        return jdbcTemplate.query(
                "select * from IM.groupship, IM.user where uid = id and gid = ?",
                new Object[]{groupId},
                (rs, rowNum) ->
                        new UserTmp(
                                rs.getString("id"),
                                rs.getString("nickname"),
                                rs.getString("avator"),
                                rs.getString("memo"),
                                rs.getBoolean("is_admin")
                        )
        );
    }

    @Override
    public List<String> sendGroupMsg(String groupId, Byte[] msg) {
        // 首先获取该群所有用户
        List<String> users = jdbcTemplate.query(
                "select uid from IM.groupship where gid = ?",
                new Object[]{groupId}, (rs, rowNum) -> rs.getString("uid")
        );
        List<String> onlineUsers = new ArrayList<>();
        for (String user : users) {
            // 如果用户在线，则添加该用户到在线用户列表用以返回
            if (onlineService.getDevicesByUserId(user) != null) onlineUsers.add(user);

            // 如果用户不在线，则对其进行离线消息的转发
            else offlineService.addOfflineMsg(user, msg);
        }
        return onlineUsers;
    }

    @Override
    public List<Tuple<Group, List<UserTmp>>> getGroupsAndMembersByUserId(String userId) {
        // 首先获得所有群对象
        List<Group> groups = jdbcTemplate.query(
                "select * from IM.groupship where uid = ?",
                new Object[]{userId},
                (rs, rowNum) ->
                        new Group(
                                rs.getString("id"),
                                rs.getString("owner"),
                                rs.getString("name"),
                                rs.getString("avatar"),
                                rs.getString("signature"),
                                rs.getString("announcement"),
                                new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format
                                        (rs.getTimestamp("created_at"))
                        )
        );
        List<Tuple<Group, List<UserTmp>>> res = new ArrayList<>();
        for (Group group : groups) {
            // 直接联表查询groupship表和user表即可
            List<UserTmp> users = jdbcTemplate.query(
                    "select * from IM.groupship, IM.user where uid = id and gid = ?",
                    new Object[]{group.getId()},
                    (rs, rowNum) ->
                            new UserTmp(
                                    rs.getString("id"),
                                    rs.getString("nickname"),
                                    rs.getString("avator"),
                                    rs.getString("memo"),
                                    rs.getBoolean("is_admin")
                            )
            );
            res.add(new Tuple<>(group, users));
        }
        return res;
    }
}