package com.knilim.group.utils;

public enum Error {
    NoUserId("no userId"),
    NoGroupName("no group name"),
    InsertFailed("Insert Failed");

    String msg;

    Error(String msg){
        this.msg = msg;
    }

    public String getMsg(){
        return msg;
    }
}
