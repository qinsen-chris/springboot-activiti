package com.gclfax.modules.activiti.listener;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 定时任务到时候，设置当前用户为vip ,设置下一任务处理人
 */
@Service
public class ManagerTaskHandler implements TaskListener {
	@Autowired
	private IdentityService identityService;

	@Override
	public void notify(DelegateTask delegateTask) {

		/*List<User> userList = identityService.createUserQuery().memberOfGroup("1").list();*/
		//delegateTask.setVariable("userList",userList);

/*		for (User u :userList){
			delegateTask.addCandidateUser(u.getFirstName());
		}*/
		delegateTask.addCandidateUser("fh1");
		delegateTask.addCandidateUser("fh2");

	}

}
