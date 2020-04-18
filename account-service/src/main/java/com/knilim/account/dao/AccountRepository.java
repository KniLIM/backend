package com.knilim.account.dao;

import com.knilim.data.model.User;

public interface AccountRepository {

    /**
     * 新建一个账户
     * @param user  账户信息
     */
    boolean insert(User user) ;

    /**
     * 修改用户信息
     * @param user
     * @return
     */
    boolean updateUserInformation(User user) ;

    /**
     * 修改密码
     * @param user
     * @return
     */
    boolean updatePassword(User user, String password) ;

    /**
     * 根据id返回用户信息
     * @param id
     * @return
     */
    User searchById(String id) ;

    /**
     * 根据关键字返回用户信息
     * @param keyword
     * @return
     */
    User searchByKeyword(String keyword) ;
}
