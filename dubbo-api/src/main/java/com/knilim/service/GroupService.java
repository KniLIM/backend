package com.knilim.service;

import java.util.List;
import com.knilim.data.model.Group;
import com.knilim.data.model.User;
import com.knilim.data.utils.Tuple;

/**
 *  群组服务接口
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
     *      String groupId = UUID.randomUUID().toString();
     *      Group groupMsg = getGroupByGroupId(groupId);
     * }</pre></p>
     *
     */
    Group getGroupByGroupId(String groupId);

    /**
     * 根据用户的user_id，返回该用户所在的群列表（进行适当冗余）
     * @param userId 需要获取所在群列表的该用户的id
     * @return 一个Group对象的列表，包括该用户所在的所有群
     *          结果进行适当冗余，每个Group里包含的信息只有groupId、name、avatar
     *
     * e.g.
     * <p><pre>{@code
     *      String userId = UUID.randomUUID().toString();
     *      ArrayList<Group> res = getGroupsByUserId(userId);
     * }</pre></p>
     *
     */
    List<Group> getGroupsByUserId(String userId);

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
     *      String groupId = UUID.randomUUID().toString();
     *      Byte[] msg = {};
     *      ArrayList<String> onlineUser = sendGroupMsg(groupId, msg);
     * }</pre></p>
     *
     */
    List<String> sendGroupMsg(String groupId, Byte[] msg);

    /**
     * 根据userId得到该用户所有的群列表，然后根据这些群id得到所有群的成员，并一起返回，结果进行适当冗余。
     * @param userId 需要获取群列表的userId
     * @return  一个Tuple的List，first是群组对象，second是一个User的List
     *
     * e.g.
     * <p><pre>{@code
     *      String userId = UUID.randomUUID().toString();
     *      ArrayList<Tuple<Group, ArrayList<User>>> res = getGroupsAndMembersByUserId(String userId);
     * }
     *
     */
    List<Tuple<Group, List<User>>> getGroupsAndMembersByUserId(String userId);
}
