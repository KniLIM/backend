package com.knilim.relationship.utils;

public enum Error {
    InsertFailed("insert failed"),
    DeleteFailed("delete failed"),
    UpdateFailed("update failed"),
    GetFriendListFailed("get friend list failed");

    String msg;

    Error(String msg){
        this.msg = msg;
    }

    public String getMsg(){
        return msg;
    }
}
