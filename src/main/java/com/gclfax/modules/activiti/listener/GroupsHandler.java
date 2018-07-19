package com.gclfax.modules.activiti.listener;

import com.gclfax.common.utils.SpringContextUtils;
import com.gclfax.modules.activiti.service.IUserGroupService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.identity.User;
import org.activiti.engine.task.IdentityLink;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Create by qs on 2018/7/18
 * email:qinsen@chinaredsun.com
 */
@Service
public class GroupsHandler implements TaskListener {

    private IUserGroupService userGroupServiceImpl;

    @Override
    public void notify(DelegateTask delegateTask) {
        String pi = delegateTask.getProcessInstanceId();
        String pdf = delegateTask.getProcessDefinitionId();
        Set<IdentityLink> set =  delegateTask.getCandidates() ;
        List<User> userList = new ArrayList<User>();
        for(IdentityLink il:set){
            if(il.getType().equals("candidate")){
                //TaskListener的实现类里面确实是拿不到Spring的bean的
                userGroupServiceImpl = (IUserGroupService)SpringContextUtils.getBean("userGroupService");
                //模板中设置的为组名称 il.getGroupId()
                userList = userGroupServiceImpl.getUserListByGroupName(il.getGroupId());
            }
        }

        if(CollectionUtils.isNotEmpty(userList)){
            for(User user :userList ){
                delegateTask.addCandidateUser(user.getFirstName());
            }
        }
    }
}
