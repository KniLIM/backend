package com.knilim.data.model;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String email;
    private String phone;
    private String passWord;
    private String nickName;
    private String avatar;
    private Boolean sex;
    private String signature;
    private String location;
    private String birthday;
    private String createdAt;

    /**
     * 这个构造方法不要删!!!有用的!
     * @autor loheagn
     */
    public User(){}

    public User(String id, String email, String phone, String passWord, String nickName, String avatar, Boolean sex, String signature, String location, String birthday, String createdAt) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.passWord = passWord;
        this.nickName = nickName;
        this.avatar = avatar;
        this.sex = sex;
        this.signature = signature;
        this.location = location;
        this.birthday = birthday;
        this.createdAt = createdAt;
    }

    public User(String id, String nickName, String avatar) {
        this.id = id;
        this.nickName = nickName;
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
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

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    /**
     * 为了之前代码的兼容性
     */
    public boolean isSex() {
        return sex;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", passWord='" + passWord + '\'' +
                ", nickName='" + nickName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", sex=" + sex +
                ", signature='" + signature + '\'' +
                ", location='" + location + '\'' +
                ", birthday='" + birthday + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
