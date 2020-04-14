package com.knilim.data.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "offline")
public class Offline {

    @Id
    @Field("user_id")
    private UUID userId;

    @Field("msgs")
    private List<Byte[]> msgs;

    public Offline(UUID userId) {
        this.userId = userId;
        this.msgs = new ArrayList<>();
    }

    public List<Byte[]> getMsgs() {
        return msgs;
    }

    public void addMsg(Byte[] data) {
        this.msgs.add(data);
    }
}
