package com.knilim.group;

import com.knilim.data.model.Group;
import com.knilim.data.model.UserTmp;
import com.knilim.group.dao.GroupRepository;
import com.knilim.service.GroupService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Test Group Service Dao Impl")
@ExtendWith(SpringExtension.class)
class GroupServiceImplTest {
    @Resource
    private GroupService groupService;

    @Autowired
    private GroupRepository groupRepository;

    @Test
    void testAutowired() {
        assertNotNull(groupRepository);
    }

//    @Test
//    void createSomeUUID(){
//        for(int i = 1;i <= 4; ++i){
//            System.out.println(UUID.randomUUID().toString());
//        }
//    }

    void showGroup(Group res) {
        System.out.println(
                "name: " + res.getName() + "\n" +
                        "avatar: " + res.getAvatar() + "\n" +
                        "signature: " + res.getSignature() + "\n" +
                        "announcement: " + res.getAnnouncement() + "\n" +
                        "createAt: " + res.getCreated_at());
    }

    @Test
    void testInsertData() {
        String gid = UUID.randomUUID().toString();
        String uid = "02a8d545-5b2b-40da-93dd-c9e20e0d21fb";
        String name = "卫星泽亚";
        String avatar = "xxx";
        String signature = "yyy";
        String announcement = "zzz";
        String createAt = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date().getTime());
        Group group = new Group(gid, uid, name, avatar, signature, announcement, createAt);
        assertTrue(groupRepository.insert(group));
    }

    @Test
    void testGetInfo() {
        Group res = groupRepository.getInfo("7c756683-2143-4d23-98e0-a5e697ce8135");
        assertNotNull(res);
        showGroup(res);
    }

    @Test
    void testUpdate() {
        String gid = "7c756683-2143-4d23-98e0-a5e697ce8135", name, avatar, signature, announcement;
        Group res;

        // 修改avatar和announcement
        name = null;
        avatar = "新头像哦";
        signature = null;
        announcement = "注意啦注意啦";
        res = groupRepository.update(gid, name, avatar, signature, announcement);
        assertNotNull(res);
        showGroup(res);

        System.out.println("————————————华丽的分割线—————————————————");

        // 修改name和signature
        name = "哉亚集团";
        avatar = null;
        signature = "Presented by ZAIA";
        announcement = null;
        res = groupRepository.update(gid, name, avatar, signature, announcement);
        assertNotNull(res);
        showGroup(res);
    }

//    @Test
//    void testHandleParticipation() {
//        String groupId, userId, state = "yes";
//
//        groupId = "0b3df9d6-8574-4862-90fe-0aa68bb5c2fa";
//        userId = "3be4e484-7226-4931-a759-7b36f40500c8";
//        assertTrue(groupRepository.handleParticipation(groupId, userId, state));
//
//        groupId = "0b3df9d6-8574-4862-90fe-0aa68bb5c2fa";
//        userId = "04370f72-bac2-44a8-888e-55693cfa74bf";
//        assertTrue(groupRepository.handleParticipation(groupId, userId, state));
//    }

    @Test
    void testGetMember() {
        String groupId = "0b3df9d6-8574-4862-90fe-0aa68bb5c2fa";
        List<UserTmp> res = groupRepository.getMembers(groupId);
        assertNotNull(res);
        for (UserTmp user : res)
            System.out.println(user.getNickName());
    }

    @Test
    void testGetGroupByUserId() {
        String userId = "3be4e484-7226-4931-a759-7b36f40500c8";
        List<Group> res = groupRepository.getGroupsByUserId(userId);
        assertNotNull(res);
        for (Group group : res) {
            showGroup(group);
            System.out.println("—————————————————————————————");
        }
    }

    @Test
    void testGetGroupByKeyword() {
        String keyword = "卫星";
        List<Group> res = groupRepository.getGroupsByKeyword(keyword);
        assertNotNull(res);
        for (Group group : res)
            System.out.println(group.getName());

        System.out.println("————————————华丽的分割线—————————————————");

        keyword = "yyy";
        res = groupRepository.getGroupsByKeyword(keyword);
        assertNotNull(res);
        for (Group group : res)
            System.out.println(group.getName() + " " + group.getAvatar());
    }

    @Test
    void testMemo() {
        String groupId = "0b3df9d6-8574-4862-90fe-0aa68bb5c2fa";
        String userId = "3be4e484-7226-4931-a759-7b36f40500c8";
        String newNickname = "Zero One";

        assertTrue(groupRepository.memo(groupId, userId, newNickname));
    }
}