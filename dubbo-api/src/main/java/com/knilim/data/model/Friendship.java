package com.knilim.data.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Friendship implements Serializable {

    private String uid;

    private String friend;

    private String nickname;

    private Boolean isTop;

    private Boolean isBlack;

    private Timestamp createdAt;

    /**
     * 这个构造方法不要删!!!有用的!
     * @author loheagn
     */
    public Friendship(){}

    public Friendship(String uid, String friend, String nickname) {
        this.uid = uid;
        this.friend = friend;
        this.nickname = nickname;
    }

    public Friendship(String uid, String friend, String nickname, Boolean isTop, Boolean isBlack, Timestamp createdAt) {
        this.uid = uid;
        this.friend = friend;
        this.nickname = nickname;
        this.isTop = isTop;
        this.isBlack = isBlack;
        this.createdAt = createdAt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Boolean getIsTop() {
        return isTop;
    }

    public void setIsTop(Boolean isTop) {
        this.isTop = isTop;
    }

    public Boolean getIsBlack() {
        return isBlack;
    }

    public void setIsBlack(Boolean isBlack) {
        this.isBlack = isBlack;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
