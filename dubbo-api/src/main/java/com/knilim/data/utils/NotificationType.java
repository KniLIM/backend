package com.knilim.data.utils;

public enum NotificationType {
    N_FRIEND_ADD_APPLICATION,       // 加好友申请 申请人 -> 添加人
    N_FRIEND_ADD_RESULT,            // 加好友结果 添加人 -> 申请人
    N_FRIEND_DELETE_RESULT,         // 删好友结果 申请人 -> 删除人
    N_GROUP_JOIN_APPLICATION,       // 加群聊申请 申请人 -> 群管理
    N_GROUP_JOIN_RESULT,            // 加群聊结果 群聊 -> 申请人
    N_GROUP_WITHDRAW_RESULT,        // 退群聊结果 申请人 -> 群管理
    N_GROUP_KICKOFF_RESULT,         // 踢人结果 群聊 -> 被踢人
}
