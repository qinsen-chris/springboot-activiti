package com.gclfax.modules.activiti.service.impl;

import com.gclfax.modules.activiti.service.IUserGroupService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Create by qs on 2018/7/18
 * email:qinsen@chinaredsun.com
 */
@Service("userGroupService")
public class UserGroupServiceImpl implements IUserGroupService {
    @Autowired
    private IdentityService identityService;
    /**
     * 根据用户组名称 查询组内用户列表
     *  模板中设置Candidate groups 可以规定格式 加上 group:fh 或者 role:fh ，实现中可以分别查询组内用户或者角色下用户
     *
     * @param groupName
     * @return
     */
    @Override
    public List<User> getUserListByGroupName(String groupName) {
        //TODO 用户组自定义，不要使用activiti的Group
        Group group = identityService.createGroupQuery().groupName(groupName).singleResult();
        System.out.println("-------------------group:"+group.getId()+"   "+group.getName());

        //根据groupId 查询组用户
        List<User> userList = identityService.createUserQuery().memberOfGroup(group.getId()).list();
        System.out.println("-------------------userList:"+userList);

        return userList;
    }
}
