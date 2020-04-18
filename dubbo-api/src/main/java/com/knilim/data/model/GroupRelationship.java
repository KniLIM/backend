package com.knilim.data.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

public class GroupRelationship implements Serializable {

    private String gid;

    private String uid;

    private String memo;  // nickname

    private Boolean isBlock;

    private Timestamp createdAt;

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

    public boolean is_block() {
        return isBlock;
    }

    public Boolean getBlock() {
        return isBlock;
    }

    public void setBlock(Boolean block) {
        isBlock = block;
    }

    public void setIs_block(boolean is_block) {
        this.isBlock = is_block;
    }

    public Timestamp getCreated_at() {
        return createdAt;
    }

    public void setCreated_at(Timestamp created_at) {
        this.createdAt = created_at;
    }
}
