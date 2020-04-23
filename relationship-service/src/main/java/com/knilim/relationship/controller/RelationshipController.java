package com.knilim.relationship.controller;

import com.knilim.data.model.Friendship;
import com.knilim.relationship.dao.RelationshipRepository;
import com.knilim.relationship.utils.Response;
import com.knilim.relationship.utils.Error;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

@RestController
public class RelationshipController {
    private RelationshipRepository relationshipRepository;

    public void setRelationshipRepository(RelationshipRepository relationshipRepository) {
        this.relationshipRepository = relationshipRepository;
    }

    @PatchMapping("/friend/application")
    public Response createRelationship(@RequestBody String json){
        JSONObject params = JSONObject.parseObject(json);
        String useId = params.getString("use_id");
        String friendId = params.getString("friend_id");
        Boolean state = params.getBoolean("state");
        if(state){
            return relationshipRepository.insert(useId, friendId)?
                    new Response(true, "result", null):
                    new Response(false, "error_msg", Error.InsertFailed.getMsg());
        }else{
            return new Response(true, "result", null);
        }
    }

    @DeleteMapping("/friend/")
    public Response deleteRelationship(@RequestBody String json){
        JSONObject params = JSONObject.parseObject(json);
        String useId = params.getString("use_id");
        String friendId = params.getString("friend_id");
        if(relationshipRepository.delete(useId, friendId)){
            return new Response(true, "result", null);
        }
        return new Response(false, "error_msg", Error.DeleteFailed.getMsg());
    }

    @PatchMapping("/friend/nickname")
    public Response patchNickname(@RequestBody String json){
        JSONObject params = JSONObject.parseObject(json);
        String useId = params.getString("use_id");
        String friendId = params.getString("friend_id");
        String nickname = params.getString("nickname");
        Friendship friendship = relationshipRepository.update(useId, friendId, nickname, null, null);
        return friendship != null ?
                new Response(true, "result", friendship) :
                new Response(false, "error_msg", Error.UpdateFailed.getMsg());
    }

    @PatchMapping("/friend/top")
    public Response patchIsTop(@RequestBody String json){
        JSONObject params = JSONObject.parseObject(json);
        String useId = params.getString("use_id");
        String friendId = params.getString("friend_id");
        Boolean isTop = params.getBoolean("is_top");
        Friendship friendship = relationshipRepository.update(useId, friendId, null, isTop, null);
        return friendship != null ?
                new Response(true, "result", friendship) :
                new Response(false, "error_msg", Error.UpdateFailed.getMsg());
    }

    @PatchMapping("/friend/black")
    public Response patchIsBlack(@RequestBody String json){
        JSONObject params = JSONObject.parseObject(json);
        String useId = params.getString("use_id");
        String friendId = params.getString("friend_id");
        Boolean isBlack = params.getBoolean("is_black");
        Friendship friendship = relationshipRepository.update(useId, friendId, null, null, isBlack);
        return friendship != null ?
                new Response(true, "result", friendship) :
                new Response(false, "error_msg", Error.UpdateFailed.getMsg());
    }

    @GetMapping("/friend/")
    public Response getFriendList(@PathVariable(value = "id") String userId) {
            List<Friendship> friends = relationshipRepository.getFriendsByUserId(userId);
            return friends != null ?
                    new Response(true, "result", friends) :
                    new Response(false, "error_msg", Error.GetFriendListFailed.getMsg());
    }
}

