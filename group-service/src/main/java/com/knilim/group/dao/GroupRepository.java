package com.knilim.group.dao;

import com.knilim.data.model.Group;

public interface GroupRepository {

    /**
     * 新建一个群
     * @param group 群信息
     * @return 是否成功
     */
    boolean insert(Group group);
}
