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

    /**  
     * 修改用户信息
     * @param user
     * @return
     */
    boolean updateUserInformation(User user) ;

    /**
     * 修改密码
     * @param id
     * @param oldPassword
     * @param newPassword
     * @return
     */
    boolean changePassword(String id,String oldPassword, String newPassword) ;

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

    /**
     * 邮箱或电话是否被占用
     * @param keyword
     * @return
     */
    boolean exsists(String keyword) ;

    /**
     * 检查密码
     * @param account
     * @param password
     * @return
     */
    boolean checkPassword(String account, String password);

}
