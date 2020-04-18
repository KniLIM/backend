package com.knilim.group.Controller;

import com.alibaba.fastjson.JSONObject;
import com.knilim.data.model.Group;
import com.knilim.group.dao.GroupRepository;
import com.knilim.group.utils.Error;
import com.knilim.group.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class GroupController {
    private GroupRepository groupRepository;

    @Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @PostMapping("/group/")
    public Response createGroup(@RequestBody String json){
        Group group = JSONObject.parseObject(json, Group.class);
        if(group.getOwner() == null)
            return new Response(false, "error_msg", Error.NoUserId.getMsg());
        if(group.getName() == null)
            return new Response(false, "error_msg", Error.NoGroupName.getMsg());
        group.setId(UUID.randomUUID().toString());
        return groupRepository.insert(group) ?
                new Response(true, "result", group) :
                new Response(false,"error_msg", Error.InsertFailed.getMsg());
    }
}
