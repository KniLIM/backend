package com.knilim.account.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.annotations.JsonAdapter;
import com.knilim.account.dao.AccountRepository;
import com.knilim.account.util.Response;
import com.knilim.data.model.Friendship;
import com.knilim.data.model.Group;
import com.knilim.data.model.User;
import com.knilim.data.utils.Device;
import com.knilim.data.utils.DeviceUtil;
import com.knilim.data.utils.Tuple;
import com.knilim.service.ForwardService;
import com.knilim.service.GroupService;
import com.knilim.service.OnlineService;
import com.knilim.service.RelationshipService;
import jdk.internal.net.http.common.Pair;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class AccountController {
    private AccountRepository accountRepository;

    @Reference
    private OnlineService onlineService;

    @Reference
    private ForwardService forwardService;

    @Reference
    private GroupService groupService;

    @Reference
    private RelationshipService relationshipService;

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
        String device = jsonObject.getString("device");
        if(account == null) return Util.loginError(Error.NoAccount);
        if(password == null) return Util.loginError(Error.NoPassword);

        // todo 验证登录信息, 选取合适的session server, 写入中心在线数据库 楠哥看看这里是否满足要求
        if(!accountRepository.checkPassword(account,password)) return Util.loginError(Error.PasswordError);
        User user;
        if(account.contains("@")) user = accountRepository.getUserByEmail(account);
        else user = accountRepository.getUserByPhone(account);
        String userId = user.getId();
        Tuple<String,Integer> ipPort = forwardService.getAvailableSession();
        String token = UUID.randomUUID().toString();
        onlineService.addOnlineDevice(userId, DeviceUtil.fromString(device),token,ipPort.getFirst(),ipPort.getSecond());
        List<Group> groups = groupService.getGroupsByUserId(userId);
        ArrayList<Friendship> friends = relationshipService.getFriendsByUserId(userId);
        return Util.longinSuccess(user,friends,groups,ipPort);
    }
  
    @PatchMapping("/account/{id}/modify")
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

    @PostMapping("/account/{id}")
    public Response searchById(@PathVariable(value="id") String id){
        if(id == null || id.equals("")) return Util.searchError(Error.NoUser);
        User user = accountRepository.searchById(id);
        if(user == null) return Util.searchError(Error.NoUser);
        return Util.searchSuccess(user);
    }

    @PostMapping("/account/search")
    public Response searchByKeyword(@RequestBody String json){
        JSONObject jsonObject = JSONObject.parseObject(json);
        String keyword = jsonObject.getString("keyword");
        if(keyword == null || keyword.equals("")) return Util.searchError(Error.NoUser);
        User user = accountRepository.searchByKeyword(keyword);
        if(user == null) return Util.searchError(Error.NoUser);
        return Util.searchSuccess(user);
    }

}

enum Error {
    NoEmail("no email"), NoPassword("no password"), NoPhone("no phone"), NoNickName("no nickName"), CanNotInsert("新建账户失败,请检查email与phone是否重复"),
    NoAccount("missing email or phone"), NoSuchAccount("no such account"),
    RedundantEmail("this email is already occupied"), RedundantPhone("phone is already occupied"), CanNotUpdate("更新账户信息失败,请检查email与phone是否重复"),
    CanNotChange("旧密码输入错误"),PasswordError("账号或密码输入错误"),NoUser("没有匹配的用户");
    Error(String msg){
        this.msg = msg;
    }
    String msg;

    public String getMsg() {
        return msg;
    }
}

class Util {
    static Response signUPError(Error error) { return new Response(false, new Tuple<>("msg", error.getMsg())); }
    static Response loginError(Error error) {
        return signUPError(error);
    }
    static Response searchError(Error error) {
        return signUPError(error);
    }
    static Response signSuccess(String id) {
        return new Response(true, new Tuple<>("user_id", id));
    }
    static Response modifyError(Error error) {
        return new Response(false, new Tuple<>("msg", error.getMsg()));
    }
    static Response modifySuccess(String id) { return new Response(true, new Tuple<>("user_id", id)); }
    static Response longinSuccess(User user,ArrayList<Friendship> friends,List<Group> groups,Tuple<String,Integer> ipPort) {
        return new Response(true,new Tuple<>("self",user),new Tuple<>("friends",friends),new Tuple<>("groups",groups),new Tuple<>("socket",ipPort));
    }
    static Response searchSuccess(User user) {return new Response(true,new Tuple<>("self",user));}
}
