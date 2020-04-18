package com.knilim.account.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.annotations.JsonAdapter;
import com.knilim.account.dao.AccountRepository;
import com.knilim.account.util.Response;
import com.knilim.data.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/account/{id}")
    public Response modify(@PathVariable(value="id") String id,@RequestBody String json) {
        User user = JSONObject.parseObject(json, User.class);
        user.setId(id);
        if(accountRepository.exsists(user.getEmail())) return Util.modifyError(Error.RedundantEmail);
        if(accountRepository.exsists(user.getPhone())) return Util.modifyError(Error.RedundantPhone);

        if(!accountRepository.updateUserInformation(user)) return Util.modifyError(Error.CanNotUpdate);
        return Util.modifySuccess(id);
    }

    @PutMapping("/account/{id}/changepassword")
    public Response change(@PathVariable(value="id") String id,@RequestBody String json) {
        JSONObject obj = JSON.parseObject(json);
        if(!accountRepository.changePassword(id,obj.getString("oldPassword"),obj.getString("newPassword"))) return Util.modifyError(Error.CanNotChange);
        return Util.modifySuccess(id);
    }


}

enum Error {
    NoEmail("no email"), NoPassword("no password"), NoPhone("no phone"), NoNickName("no nickName"), CanNotInsert("新建账户失败,请检查email与phone是否重复"),
    RedundantEmail("this email is already occupied"), RedundantPhone("phone is already occupied"), CanNotUpdate("更新账户信息失败,请检查email与phone是否重复"),
    CanNotChange("旧密码输入错误");
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
    static Response signSuccess(String id) {
        return new Response(true, "user_id", id);
    }
    static Response modifyError(Error error) {
        return new Response(false, "msg", error.getMsg());
    }
    static Response modifySuccess(String id) {
        return new Response(true, "user_id", id);
    }
}
