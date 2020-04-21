package com.knilim.group.utils;

public enum Error {
    NoUserId("no userId"),
    NoGroupName("no group name"),
    InsertFailed("insert failed"),
    NoGroupId("no groupId"),
    DeleteFailed("delete failed"),
    GetInfoFailed("get info failed"),
    UpdateFailed("update failed"),
    GetGroupListByUserIdFailed("get group list by userId failed"),
    GetGroupListByKeywordFailed("get group list by keyword failed"),
    GetMembersFailed("get members failed"),
    CreateApplicationFailed("create application failed"),
    HandleApplicationFailed("handle application failed"),
    ExitGroupFailed("exit group failed"),
    ExpelGroupFailed("expel group failed"),
    ChangeNicknameFailed("change nickname failed");

    String msg;

    Error(String msg){
        this.msg = msg;
    }

    public String getMsg(){
        return msg;
    }
}
