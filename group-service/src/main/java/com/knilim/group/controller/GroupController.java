package com.knilim.group.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.knilim.data.model.Group;
import com.knilim.group.dao.GroupRepository;
import com.knilim.group.utils.Error;
import com.knilim.group.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class GroupController {
    private GroupRepository groupRepository;

    @Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @PostMapping("/group/")
    public Response createGroup(@RequestBody String json) {
        Group group = JSONObject.parseObject(json, Group.class);
        if (group.getOwner() == null)
            return new Response(false, "error_msg", Error.NoUserId.getMsg());
        if (group.getName() == null)
            return new Response(false, "error_msg", Error.NoGroupName.getMsg());
        group.setId(UUID.randomUUID().toString());
        return groupRepository.insert(group) ?
                new Response(true, "result", group) :
                new Response(false, "error_msg", Error.InsertFailed.getMsg());
    }

    @DeleteMapping("/group/{id}")
    public Response deleteGroup(@PathVariable(value = "id") String groupId) {
        if (groupId == null || groupId.equals(""))
            return new Response(false, "error_msg", Error.NoGroupId.getMsg());
        return groupRepository.delete(groupId) ?
                new Response(true, "result", null) :
                new Response(false, "error_msg", Error.DeleteFailed.getMsg());
    }

    @GetMapping("/group/{id}")
    public Response getGroupInfo(@PathVariable(value = "id") String groupId) {
        if (groupId == null || groupId.equals(""))
            return new Response(false, "error_msg", Error.NoGroupId.getMsg());
        Group group = groupRepository.getInfo(groupId);
        return group != null ?
                new Response(true, "result", group) :
                new Response(false, "error_msg", Error.GetInfoFailed.getMsg());
    }

    @PatchMapping("/group/{id}")
    public Response updateGroup(@PathVariable(value = "id") String groupId,
                                @RequestBody String json) {
        JSONObject params = JSONObject.parseObject(json);
        String name = params.getString("name");
        String avatar = params.getString("avatar");
        String signature = params.getString("signature");
        String announcement = params.getString("announcement");
        Group group = groupRepository.update(groupId, name, avatar, signature, announcement);
        return group != null ?
                new Response(true, "result", group) :
                new Response(false, "error_msg", Error.UpdateFailed.getMsg());
    }
}

