package com.knilim.group.controller;

import com.alibaba.fastjson.JSONObject;
import com.knilim.data.model.Group;
import com.knilim.group.dao.GroupRepository;
import com.knilim.group.utils.Error;
import com.knilim.group.utils.Response;
import com.knilim.data.model.UserTmp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        if(group.getAvatar() == null) {
            group.setAvatar("http://cdn.loheagn.com/2020-06-12-Snipaste_2020-06-12_17-51-01.png");
        }
        if(group.getSignature() == null) {
            group.setAvatar("群聊天");
        }
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

    @GetMapping("/group/")
    public Response getGroupList(@RequestParam("keyword") String keyword) {
          List<Group> groups = groupRepository.getGroupsByKeyword(keyword);
          return groups != null ?
                  new Response(true, "result", groups) :
                  new Response(false, "error_msg", Error.GetGroupListByKeywordFailed.getMsg());
    }

    @GetMapping("/group/{id}/member")
    public Response getMembers(@PathVariable(value = "id") String id) {
        List<UserTmp> users = groupRepository.getMembers(id);
        return users != null ?
                new Response(true, "result", users) :
                new Response(false, "error_msg", Error.GetMembersFailed.getMsg());
    }

    @PostMapping("/group/{id}/participation")
    public Response createApplication(@PathVariable(value = "id") String groupId,
                                      @RequestBody String json) {
        JSONObject params = JSONObject.parseObject(json);
        String userId = params.getString("user_id");
        String comment = params.getString("comment");
        return groupRepository.participation(groupId, userId, comment) ?
                new Response(true, "result", null) :
                new Response(false, "error_msg", Error.CreateApplicationFailed.getMsg());
    }

    @PatchMapping("/group/{id}/participation")
    public Response handleApplication(@PathVariable(value = "id") String groupId,
                                      @RequestBody String json) {
        JSONObject params = JSONObject.parseObject(json);
        String userId = params.getString("user_id");
        String state = params.getString("state");
        return groupRepository.handleParticipation(groupId, userId, state) ?
                new Response(true, "result", null) :
                new Response(false, "error_msg", Error.HandleApplicationFailed.getMsg());
    }

    @PostMapping("/group/{id}/exit")
    public Response exitGroup(@PathVariable(value = "id") String groupId,
                              @RequestBody String json) {
        String userId = JSONObject.parseObject(json).getString("user_id");
        return groupRepository.exit(groupId, userId) ?
                new Response(true, "result", null) :
                new Response(false, "error_msg", Error.ExitGroupFailed.getMsg());
    }

    @PostMapping("/group/{id}/expel?params")
    public Response expelGroup(@PathVariable(value = "id") String groupId,
                               @RequestBody String json) {
        String userId = JSONObject.parseObject(json).getString("user_id");
        return groupRepository.expel(groupId, userId) ?
                new Response(true, "result", null) :
                new Response(false, "error_msg", Error.ExpelGroupFailed.getMsg());
    }

    @PostMapping("/group/{id}/nickname")
    public Response changeNickname(@PathVariable(value = "id") String groupId,
                                   @RequestBody String json) {
        JSONObject params = JSONObject.parseObject(json);
        String userId = params.getString("user_id");
        String newNickname = params.getString("new_nickname");
        return groupRepository.memo(groupId, userId, newNickname) ?
                new Response(true, "result", null) :
                new Response(false, "error_msg", Error.ChangeNicknameFailed.getMsg());
    }
}
