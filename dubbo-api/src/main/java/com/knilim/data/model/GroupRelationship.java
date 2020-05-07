package com.knilim.data.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

public class GroupRelationship implements Serializable {

    private String gid;

    private String uid;

    private String memo;  // nickname

    private Boolean isBlock;

    private String createdAt;

    private String userAvatar;

    private Boolean isAdmin;

    /**
     * 这个构造方法不要删!!!有用的!
     *
     * @author loheagn
     */
    public GroupRelationship() {
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Boolean getBlock() {
        return isBlock;
    }

    public void setBlock(Boolean block) {
        isBlock = block;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }
}
