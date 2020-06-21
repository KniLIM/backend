package com.knilim.relationship.controller;

import com.knilim.data.model.Friendship;
import com.knilim.relationship.dao.RelationshipRepository;
import com.knilim.relationship.utils.Response;
import com.knilim.relationship.utils.Error;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSONObject;
import com.knilim.relationship.dao.impl.tempFriend;


import java.util.List;

@RestController
public class RelationshipController {
    private RelationshipRepository relationshipRepository;

    @Autowired
    public void setRelationshipRepository(RelationshipRepository relationshipRepository) {
        this.relationshipRepository = relationshipRepository;
    }

    @PostMapping("/friend/application")
    public Response createApplication(@RequestBody String json){
        JSONObject params = JSONObject.parseObject(json);
        String useId = params.getString("user_id");
        String friendId = params.getString("friend_id");
        String uName = params.getString("u_name");
        String instruction = params.getString("instruction");

        relationshipRepository.addApplication(friendId, useId, uName, instruction);

        return new Response(true, "result", null);

    }

    @PatchMapping("/friend/application")
    public Response createRelationship(@RequestBody String json){
        JSONObject params = JSONObject.parseObject(json);
        String useId = params.getString("user_id");
        String friendId = params.getString("friend_id");
        String fName = params.getString("f_name");
        Boolean state = params.getBoolean("state");
        return relationshipRepository.insert(useId, friendId, fName, state)?
                    new Response(true, "result", null):
                    new Response(false, "error_msg", Error.InsertFailed.getMsg());
    }

    @DeleteMapping("/friend")
    public Response deleteRelationship(@RequestBody String json){
        JSONObject params = JSONObject.parseObject(json);
        String useId = params.getString("user_id");
        String friendId = params.getString("friend_id");
        if(relationshipRepository.delete(useId, friendId)){
            return new Response(true, "result", null);
        }
        return new Response(false, "error_msg", Error.DeleteFailed.getMsg());
    }

    @PatchMapping("/friend/nickname")
    public Response patchNickname(@RequestBody String json){
        JSONObject params = JSONObject.parseObject(json);
        String useId = params.getString("user_id");
        String friendId = params.getString("friend_id");
        String nickname = params.getString("nickname");
        Friendship friendship = relationshipRepository.updateNickname(useId, friendId, nickname);
        return friendship != null ?
                new Response(true, "result", friendship) :
                new Response(false, "error_msg", Error.UpdateFailed.getMsg());
    }

    @PatchMapping("/friend/top")
    public Response patchIsTop(@RequestBody String json){
        JSONObject params = JSONObject.parseObject(json);
        String useId = params.getString("user_id");
        String friendId = params.getString("friend_id");
        Boolean isTop = params.getBoolean("is_top");
        Friendship friendship = relationshipRepository.updateIsTop(useId, friendId, isTop);
        return friendship != null ?
                new Response(true, "result", friendship) :
                new Response(false, "error_msg", Error.UpdateFailed.getMsg());
    }

    @PatchMapping("/friend/black")
    public Response patchIsBlack(@RequestBody String json){
        JSONObject params = JSONObject.parseObject(json);
        String useId = params.getString("user_id");
        String friendId = params.getString("friend_id");
        Boolean isBlack = params.getBoolean("is_black");
        Friendship friendship = relationshipRepository.updateIsBlack(useId, friendId, isBlack);
        return friendship != null ?
                new Response(true, "result", friendship) :
                new Response(false, "error_msg", Error.UpdateFailed.getMsg());
    }

    @GetMapping("/friend/{id}")
    public Response getFriendList(@PathVariable(value = "id") String userId) {
            List<tempFriend> friends = relationshipRepository.getFriendsByUserId(userId);
            return friends != null ?
                    new Response(true, "result", friends) :
                    new Response(false, "error_msg", Error.GetFriendListFailed.getMsg());
    }
}

