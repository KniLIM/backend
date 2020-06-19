package com.knilim.account.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.knilim.account.dao.AccountRepository;
import com.knilim.account.util.Response;
import com.knilim.data.model.Friendship;
import com.knilim.data.model.Group;
import com.knilim.data.model.User;
import com.knilim.data.utils.DeviceUtil;
import com.knilim.data.utils.Tuple;
import com.knilim.service.*;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Reference
    private OfflineService offlineService;

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @PostMapping("/account/signup")
    public Response signUp(@RequestBody String json) {
        try {
            User user = JSONObject.parseObject(json, User.class);
            if (user.getEmail() == null) return Util.signUPError(Error.NoEmail);
            if (user.getPassWord() == null) return Util.signUPError(Error.NoPassword);
            if (user.getPhone() == null) return Util.signUPError(Error.NoPhone);
            if (user.getNickName() == null) return Util.signUPError(Error.NoNickName);
            if (user.getSex() == null) return Util.signUPError(Error.NoSex);

            String id = UUID.randomUUID().toString();
            user.setId(id);

            // 处理user中的空字段
            if(user.getLocation() == null) user.setLocation("China");
            if(user.getAvatar() == null) user.setAvatar("http://cdn.loheagn.com/2020-06-03-avator.jpg");
            if(user.getBirthday() == null) user.setBirthday("2020-01-26");
            if(user.getSignature() == null) user.setSignature("");


            if (!accountRepository.insert(user)) return Util.signUPError(Error.CanNotInsert);
            else return Util.signSuccess(id);
        } catch (Exception e) {
            return Util.ServerError(Error.ServerError, e.getMessage());
        }
    }


    @PostMapping("/account/login")
    public Response login(@RequestBody String json) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            String account = jsonObject.getString("account");
            String password = jsonObject.getString("password");
            String device = jsonObject.getString("device");
            if (account == null) return Util.loginError(Error.NoAccount);
            if (password == null) return Util.loginError(Error.NoPassword);

            // 验证登录信息
            User user;
            if (account.contains("@")) user = accountRepository.getUserByEmail(account);
            else user = accountRepository.getUserByPhone(account);
            if(user == null) return Util.loginError(Error.NoSuchAccount);
            if(!user.getPassWord().equals(password)) return Util.loginError(Error.PasswordError);
            String userId = user.getId();
            JSONObject resultUser = JSONObject.parseObject(JSONObject.toJSONString(user));
            resultUser.remove("passWord");

            // 选取合适的session server
            Tuple<String, Integer> ipPort = forwardService.getAvailableSession();

            // 写入中心在线数据库
            String token = UUID.randomUUID().toString();
            onlineService.addOnlineDevice(userId, DeviceUtil.fromString(device), token, ipPort.getFirst(), ipPort.getSecond());

            // 获取好友信息和群组信息
            List<Group> groups = groupService.getGroupsByUserId(userId);
            List<Friendship> friendships = relationshipService.getFriendsByUserId(userId);
            List<JSONObject> friends = friendships.stream().map(friendship -> {
                JSONObject result = JSONObject.parseObject(JSONObject.toJSONString(friendship));
                result.put("avatar", accountRepository.searchById(friendship.getFriend()).getAvatar());
                return result;
            }).collect(Collectors.toList());

            // 拉取离线消息
            List<Byte[]>  offlineMessages = offlineService.getOfflineMsgs(user.getId());

            return Util.loginSuccess(resultUser, friends, groups, ipPort, offlineMessages, token);
        } catch (Exception e) {
            return Util.ServerError(Error.ServerError, e.getMessage());
        }
    }
  
    @PatchMapping("/account/{id}/modify")
    public Response modify(@PathVariable(value="id") String id,@RequestBody String json) {
        try {
            User user = accountRepository.searchById(id);
            if (user == null) return Util.modifyError(Error.NoUser);
            User newUser = JSONObject.parseObject(json, User.class);

            // 获取更改字段
            Util.compareNewUser(user, newUser);

            // 判断邮箱或者手机号是否重复
            if(newUser.getEmail() != null) {
                User fakeUser = accountRepository.getUserByEmail(user.getEmail());
                if (fakeUser != null && !fakeUser.getId().equals(id))
                    return Util.modifyError(Error.RedundantEmail);
            }
            if(newUser.getPhone() != null) {
                User fakeUser = accountRepository.getUserByPhone(user.getPhone());
                if (fakeUser != null && !fakeUser.getId().equals(id))
                    return Util.modifyError(Error.RedundantPhone);
            }

            if (!accountRepository.updateUserInformation(user)) return Util.modifyError(Error.CanNotUpdate);
            return Util.modifySuccess(id);
        } catch (Exception e) {
            return Util.ServerError(Error.ServerError, e.getMessage());
        }
    }

    @PutMapping("/account/{id}/changepassword")
    public Response change(@PathVariable(value="id") String id,@RequestBody String json) {
        try {
        JSONObject obj = JSON.parseObject(json);
        if(!accountRepository.changePassword(id,obj.getString("oldPassword"),obj.getString("newPassword"))) return Util.modifyError(Error.CanNotChange);
        return Util.modifySuccess(id);
        } catch (Exception e) {
            return Util.ServerError(Error.ServerError, e.getMessage());
        }
    }

    @GetMapping("/account/{id}")
    public Response searchById(@PathVariable(value="id") String id){
        try {
            if (id == null || id.equals("")) return Util.searchError(Error.NoUser);
            User user = accountRepository.searchById(id);
            if (user == null) return Util.searchError(Error.NoUser);
            return Util.searchSuccess(user);
        } catch (Exception e) {
            return Util.ServerError(Error.ServerError, e.getMessage());
        }
    }

    @PostMapping("/account/search")
    public Response searchByKeyword(@RequestBody String json){
        try {
        JSONObject jsonObject = JSONObject.parseObject(json);
        String keyword = jsonObject.getString("keyword");
        if(keyword == null || keyword.equals("")) return Util.searchError(Error.NoUser);
        User user = accountRepository.searchByKeyword(keyword);
        if(user == null) return Util.searchError(Error.NoUser);
        return Util.searchSuccess(user);
        } catch (Exception e) {
            return Util.ServerError(Error.ServerError, e.getMessage());
        }
    }

}

enum Error {
    NoEmail("no email"), NoPassword("no password"), NoPhone("no phone"), NoNickName("no nickName"), NoSex("缺少性别选项") , CanNotInsert("新建账户失败,请检查email与phone是否重复"),
    NoAccount("missing email or phone"), NoSuchAccount("no such account"),
    RedundantEmail("this email is already occupied"), RedundantPhone("phone is already occupied"), CanNotUpdate("更新账户信息失败,请检查email与phone是否重复"),
    CanNotChange("旧密码输入错误"),PasswordError("账号或密码输入错误"),NoUser("没有匹配的用户"), ServerError("服务器内部错误");
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
    static Response loginSuccess(JSONObject user, List<JSONObject> friends, List<Group> groups, Tuple<String,Integer> ipPort, List<Byte[]> messages, String token) {
        return new Response(true,new Tuple<>("self",user),new Tuple<>("friends",friends),new Tuple<>("groups",groups),new Tuple<>("socket",ipPort), new Tuple<>("offlineMessages", messages), new Tuple<>("token", token));
    }
    static Response searchSuccess(User user) {return new Response(true,new Tuple<>("self",user));}
    static Response ServerError(Error error, String detail) {
        return new Response(false, new Tuple<>("msg", error.getMsg()), new Tuple<>("detail", detail));
    }

    static void compareNewUser(User user, User newUser) {
        if(newUser.getEmail() != null) user.setEmail(newUser.getEmail());
        if(newUser.getPhone() != null) user.setPhone(newUser.getPhone());
        if(newUser.getNickName() != null) user.setNickName(newUser.getNickName());
        if(newUser.getAvatar() != null) user.setAvatar(newUser.getAvatar());
        if(newUser.getSex() != null) user.setSex(newUser.getSex());
        if(newUser.getSignature() != null) user.setSignature(newUser.getSignature());
        if(newUser.getLocation() != null) user.setLocation(newUser.getLocation());
        if(newUser.getBirthday() != null) user.setBirthday(newUser.getBirthday());
    }
}
