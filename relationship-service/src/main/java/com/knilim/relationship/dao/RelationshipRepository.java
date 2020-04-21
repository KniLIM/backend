package com.knilim.relationship.dao;

import com.knilim.data.model.Friendship;

import java.util.List;

public interface RelationshipRepository {

    /**
     * 新建一个好友关系表
     * @param uid 添加方
     * @param friend 被添加方
     * @return 是否成功
     */
    boolean insert(String uid, String friend);

    /**
     * 删除一个好友关系
     * @param uid 删除方id
     * @param friend 被删除方id
     * @return 是否成功
     */
    boolean delete(String uid, String friend);

    /**
     * 更新群信息
     * @param uid 用户id
     * @param friend 好友id
     * @param nickname 更新备注
     * @param isTop 更新置顶状态
     * @param isBlack 更新拉黑状态
     * @return 一个Friendship对象，包含该Friendship最新的所有信息
     */
    Friendship update(String uid, String friend, String nickname, Boolean isTop, Boolean isBlack);

    /**
     * 根据用户id返回好友列表
     * @param uid 用户id
     * @return 该用户的好友列表
     */
    List<Friendship> getFriendsByUserId(String uid);

}
