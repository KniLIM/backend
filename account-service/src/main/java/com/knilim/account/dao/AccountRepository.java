package com.knilim.account.dao;

import com.knilim.data.model.User;

public interface AccountRepository {

    /**
     * 新建一个账户
     * @param user  账户信息
     */
    boolean insert(User user) ;

    /**
     * 根据email账号查找用户
     * @param email 邮箱账号
     * @return  查出来的User对象, 查找失败时, 返回空
     */
    User getUserByEmail(String email);

    /**
     * 根据手机号查找用户
     * @param phone 手机号
     * @return 查出来的User对象, 查找失败时, 返回空
     */
    User getUserByPhone(String phone);
}
