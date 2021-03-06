package com.knilim.data.model;

import com.knilim.data.utils.NotificationType;

import java.io.Serializable;
import java.util.Objects;

public class Notification implements Serializable {

    private String rcvId;

    private String senderId;

    private NotificationType type;

    private String content;

    private String createAt;

    public Notification() {}

    public Notification(String rcvId, String senderId, NotificationType type, String content, String createAt) {
        this.rcvId = rcvId;
        this.senderId = senderId;
        this.type = type;
        this.content = content;
        this.createAt = createAt;
    }

    public String getRcvId() {
        return rcvId;
    }

    public void setRcvId(String rcvId) {
        this.rcvId = rcvId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        Notification that = (Notification) o;
        return Objects.equals(rcvId, that.rcvId) &&
                Objects.equals(senderId, that.senderId) &&
                type == that.type &&
                Objects.equals(content, that.content) &&
                Objects.equals(createAt, that.createAt);
    }
}
