package com.knilim.account.controller;

import com.alibaba.fastjson.JSONObject;
import com.knilim.account.dao.AccountRepository;
import com.knilim.account.util.Response;
import com.knilim.data.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class AccountController {
    private AccountRepository accountRepository;

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @PostMapping("/account/signup")
    public Response signUp(@RequestBody String json) {
        User user = JSONObject.parseObject(json, User.class);
        if(user.getEmail() == null) return Util.signUPError(Error.NoEmail);
        if(user.getPassWord() == null) return Util.signUPError(Error.NoPassword);
        if(user.getPhone() == null) return Util.signUPError(Error.NoPhone);
        if(user.getNickName() == null) return Util.signUPError(Error.NoNickName);

        String id = UUID.randomUUID().toString();
        user.setId(id);
        if(!accountRepository.insert(user)) return Util.signUPError(Error.CanNotInsert);
        else return Util.signSuccess(id);
    }

    @PostMapping("/account/login")
    public Response login(@RequestBody String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        String account = jsonObject.getString("account");
        String password = jsonObject.getString("password");
        if(account == null) return Util.loginError(Error.NoAccount);
        if(password == null) return Util.loginError(Error.NoPassword);

        // todo 验证登录信息, 选取合适的session server, 写入中心在线数据库
        return null;
    }


}

enum Error {
    NoEmail("no email"), NoPassword("no password"), NoPhone("no phone"), NoNickName("no nickName"), CanNotInsert("新建账户失败,请检查email与phone是否重复"),
    NoAccount("missing email or phone"), NoSuchAccount("no such account");
    Error(String msg){
        this.msg = msg;
    }
    String msg;

    public String getMsg() {
        return msg;
    }
}

class Util {
    static Response signUPError(Error error) {
        return new Response(false, "msg", error.getMsg());
    }
    static Response loginError(Error error) {
        return signUPError(error);
    }
    static Response signSuccess(String id) {
        return new Response(true, "user_id", id);
    }
}
