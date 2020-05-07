package com.knilim.data.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

public class Group implements Serializable {

    private String id;

    private String owner;

    private String name;

    private String avatar;

    private String signature;

    private String announcement;

    private String createdAt;

    /**
     * 这个构造方法不要删!!!有用的!
     * @author loheagn
     */
    public Group(){}

    public Group(String id, String owner, String name, String avatar,
                 String signature, String announcement, String createdAt) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.avatar = avatar;
        this.signature = signature;
        this.announcement = announcement;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public String getCreated_at() {
        return createdAt;
    }

    public void setCreated_at(String created_at) {
        this.createdAt = created_at;
    }
}
