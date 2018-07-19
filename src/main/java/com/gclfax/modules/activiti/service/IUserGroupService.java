package com.gclfax.modules.activiti.service;

import org.activiti.engine.identity.User;

import java.util.List;

/**
 * Create by qs on 2018/7/18
 * email:qinsen@chinaredsun.com
 */
public interface IUserGroupService {

    /**
     * 根据用户组名称 查询组内用户列表
     * @param groupName
     * @return
     */
    List<User> getUserListByGroupName(String groupName);
}
