package com.knilim.group.dao;

import com.knilim.data.model.Group;
import com.knilim.data.model.UserTmp;

import java.util.List;

public interface GroupRepository {

    /**
     * 新建一个群
     * @param group 群信息
     * @return 是否成功
     */
    boolean insert(Group group);

    /**
     * 删除一个群
     * @param groupId 群id
     * @return 是否成功
     */
    boolean delete(String groupId);

    /**
     * 得到一个群的信息
     * @param groupId 群id
     * @return 一个Group对象，包含该group所有信息
     */
    Group getInfo(String groupId);

    /**
     * 更新群信息
     * @param groupId 群id
     * @param name 更新名字
     * @param avatar 更新头像
     * @param signature 更新简介
     * @param announcement 更新公告
     * @return 一个Group对象，包含该group最新的所有信息
     */
    Group update(String groupId, String name, String avatar, String signature, String announcement);

    /**
     * 根据用户id返回群列表
     * @param userId 用户id
     * @return 该用户的群列表
     */
    List<Group> getGroupsByUserId(String userId) ;

    /**
     * 根据关键词返回群列表
     * @param Keyword 关键词
     * @return 符和关键词的群列表
     */
    List<Group> getGroupsByKeyword(String Keyword);

    /**
     * 根据群id，返回该群的所有成员
     * @param groupId 群id
     * @return 群成员列表
     */
    List<UserTmp> getMembers(String groupId);

    /**
     * 发送加群通知，并向该群群主发送推送通知
     * @param groupId 群id
     * @param userId 用户id
     * @param comment 加群内容/原因/说明
     * @return 是否成功
     */
    boolean participation(String groupId, String userId, String comment);

    /**
     * 群主处理加群申请，并向申请人发送相应推送通知
     * @param groupId 群id
     * @param userId 申请人id
     * @param state 群主处理的状态，包括"yes"和"no"
     * @return 该操作是否成功
     */
    boolean handleParticipation(String groupId, String userId, String state);

    /**
     * 退出群
     * @param groupId 群id
     * @param userId 要退群的userId
     * @return 该操作是否成功
     */
    boolean exit(String groupId, String userId);

    /**
     * 踢出群
     * @param groupId 群id
     * @param userId 要踢出的userId
     * @return 该操作是否成功
     */
    boolean expel(String groupId, String userId);

    /**
     * 修改群昵称
     * @param groupId 群id
     * @param userId 用户id
     * @param newNickname 新的昵称
     * @return 该操作是否成功
     */
    boolean memo(String groupId, String userId, String newNickname);
}
