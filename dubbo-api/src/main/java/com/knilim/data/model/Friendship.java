package com.knilim.data.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

public class Friendship implements Serializable {

    private UUID uid;

    private UUID friend;

    private String nickname;

    private Boolean is_top;

    private Boolean is_black;

    private Timestamp created_at;

    public Friendship(UUID uid, UUID friend, String nickname) {
        this.uid = uid;
        this.friend = friend;
        this.nickname = nickname;
    }

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
    }

    public UUID getFriend() {
        return friend;
    }

    public void setFriend(UUID friend) {
        this.friend = friend;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Boolean getIs_top() {
        return is_top;
    }

    public void setIs_top(Boolean is_top) {
        this.is_top = is_top;
    }

    public Boolean getIs_black() {
        return is_black;
    }

    public void setIs_black(Boolean is_black) {
        this.is_black = is_black;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
}
