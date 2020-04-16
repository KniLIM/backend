package com.knilim.service;

import java.util.List;
import java.util.UUID;
import com.knilim.data.model.Group;
import com.knilim.data.model.User;

/**
 *  群组服务RPC接口
 */
public interface GroupService {
    /**
     *
     * 根据群的id返回该群的所有信息（java对象）
     * @param groupId 需要获取的群的id
     * @return 一个Group的model对象，包括所需要的该id对应的群信息
     *
     * e.g.
     * <p><pre>{@code
     *      UUID groupId = UUID.randomUUID();
     *      Group groupMsg = getGroupByGroupId(groupId);
     * }</pre></p>
     *
     */
    Group getGroupByGroupId(UUID groupId);

    /**
     * 根据用户的user_id，返回该用户所在的群列表（进行适当冗余）
     * @param userId 需要获取所在群列表的该用户的id
     * @return 一个Group对象的列表，包括该用户所在的所有群
     *          结果进行适当冗余，每个Group里包含的信息只有groupId、name、avatar
     *
     * e.g.
     * <p><pre>{@code
     *      UUID userId = UUID.randomUUID();
     *      ArrayList<Group> res = getGroupsByUserId(userId);
     * }</pre></p>
     *
     */
    List<Group> getGroupsByUserId(UUID userId);

    /**
     * 根据群的id返回该群的所有用户列表（进行适当冗余）
     * @param groupId 需要获取用户列表的群id
     * @return 一个User对象的列表，包括该群的所有用户
     *          结果进行适当冗余，每个User里包含的信息只有userId、memo、avatar
     *
     * e.g.
     * <p><pre>{
     *      UUID groupOId = UUID.randomUUID();
     *      ArrayList<User> res = getMembersByGroupId(groupId);
     * }</pre></p>
     */
    List<User> getMembersByGroupId(UUID groupId);

    /**
     * 根据groupId得到所有群成员，然后查找在线数据库，将离线的用户和在线的用户分为两个列表。
     * 对于离线的用户，操作离线消息数据库对它们进行消息的离线存储。
     * 然后对于在线的用户，将在线用户的列表返回给session server进行消息的转发处理。
     *
     * @param groupId 需要进行消息转发的群聊的id
     * @param msg 需要进行转发的消息字节序列
     *
     * @return 一个当前该群里所有在线用户的UUID列表
     *
     * e.g.
     * <p><pre>{@code
     *      UUID groupId = UUID.randomUUID();
     *      Byte[] msg = {};
     *      ArrayList<UUID> onlineUser = sendGroupMsg(groupId, msg);
     * }</pre></p>
     *
     */
    List<UUID> sendGroupMsg(UUID groupId, Byte[] msg);
}
