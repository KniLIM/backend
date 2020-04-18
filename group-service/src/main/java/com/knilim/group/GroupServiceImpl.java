package com.knilim.group;

import com.knilim.data.model.Group;
import com.knilim.data.model.User;
import com.knilim.data.utils.Tuple;
import com.knilim.service.GroupService;

import java.util.List;

public class GroupServiceImpl implements GroupService {

    @Override
    public Group getGroupByGroupId(String groupId) {
        return null;
    }

    @Override
    public List<Group> getGroupsByUserId(String userId) {
        return null;
    }

    @Override
    public List<String> sendGroupMsg(String groupId, Byte[] msg) {
        return null;
    }

    @Override
    public List<Tuple<Group, List<User>>> getGroupsAndMembersByUserId(String userId) {
        return null;
    }
}
