package com.knilim.data.model;

import java.io.Serializable;

/**
 * @author Faymine
 * 只用于group的getMembers和RPC的方法返回，临时的包含所需返回数据的冗余的user的model
 * 仅用于返回！！！不是真正的user！！勿删！！！
 * 仅用于返回！！！不是真正的user！！勿删！！！
 * 仅用于返回！！！不是真正的user！！勿删！！！
 */
public class UserTmp implements Serializable {
    private String id;
    private String nickName;
    private String avatar;
    private String memo;
    private Boolean isAdmin;

    public UserTmp(String id, String nickName, String avatar, String memo, Boolean isAdmin) {
        this.id = id;
        this.nickName = nickName;
        this.avatar = avatar;
        this.memo = memo;
        this.isAdmin = isAdmin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }
}
