package com.knilim.group.dao;

import com.knilim.data.model.Group;

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
}
