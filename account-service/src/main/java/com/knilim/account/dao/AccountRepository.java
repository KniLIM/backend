package com.knilim.account.dao;

import com.knilim.data.model.User;

public interface AccountRepository {

    /**
     * 新建一个账户
     * @param user  账户信息
     */
    boolean insert(User user) ;
}
