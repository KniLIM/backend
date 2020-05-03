package com.knilim.service;

import com.knilim.data.model.Friendship;

import java.util.List;

public interface RelationshipService {
    /**
     * 根据用户的user_id，返回该用户所在的好友列表（进行适当冗余）
     * @param userId 需要获取好友列表的该用户的id
     * @return 一个Friendship对象的列表，包括该用户的所有好友
     *          结果进行适当冗余，每个Friendship里包含的信息有friendId、nickname、is_top、 is_black
     *
     * e.g.
     * <p><pre>{@code
     *      String userId;
     *      List<Friendship> res = getFriendsByUserId(userId);
     * }</pre></p>
     *
     */
    List<Friendship> getFriendsByUserId(String userId);
}
