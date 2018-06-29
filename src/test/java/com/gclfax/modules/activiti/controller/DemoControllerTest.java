package com.gclfax.modules.activiti.controller;

import com.gclfax.modules.activiti.domain.LeaveBill;
import com.gclfax.modules.activiti.service.ILeaveBillService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;
@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoControllerTest {
    private RepositoryService repositoryService;   
    @Autowired    
    private RuntimeService runtimeService;    
    @Autowired    
    private TaskService taskService;    
    @Autowired
    private IdentityService identityService;
	@Autowired
	private ILeaveBillService leaveBillService;

	@Test
	public void testLeaveBill(){
		LeaveBill bill = new LeaveBill();
		bill.setDays(3);
		bill.setContent("回家");
		bill.setLeaveDate(new Date());
		bill.setState(0);
		bill.setRemark("带特产！！");

		leaveBillService.saveLeaveBill(bill);
	}

	@Test
	public void testDeployTest(){
		//根据bpmn文件部署流程
		Deployment deployment = repositoryService.createDeployment().addClasspathResource("processes/PactAuditDemo.bpmn").deploy();
		//获取流程定义
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();

		//在部署流程定义和启动流程实例的中间，设置组任务的办理人，向Activity表中存放组和用户的信息
		GroupEntity group1 = new GroupEntity();
		group1.setName("员工");
		GroupEntity group2 = new GroupEntity();
		group2.setName("经理");
		identityService.saveGroup(group1);//建立组
		identityService.saveGroup(group2);//建立组
		identityService.saveUser(new UserEntity("小张"));
		identityService.saveUser(new UserEntity("李总"));
		identityService.createMembership("小张", "部门经理");//建立组和用户关系
		identityService.createMembership("李总", "部门经理");//建立组和用户关系
	}













	@Test
	public void testFirstDemo() {
        Task task=taskService.createTaskQuery().taskId("22508").singleResult();  
        System.out.println("执行前1，任务名称："+task.getName());  
        //taskService.complete("32512");
        //taskService.claim("37505", "王五");
	}

	@Test
	public void testStartUserTask() {
		Map<String, Object> variables = new HashMap<String, Object>();
    	variables.put("userID", "AAA");
    	
        //启动流程定义，返回流程实例  
		String processDefinitionKey = "gxs_leave";
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(processDefinitionKey,variables);
        String processId = pi.getId();  
        System.out.println("流程创建成功，当前流程实例ID："+processId+" ，当前实例name:"+pi.getName());  
        
        Task task=taskService.createTaskQuery().processInstanceId(processId).singleResult();  
        System.out.println("第一次执行前，任务名称："+task.getName());  
        taskService.complete(task.getId());  
	}

	@Test
	public void testQueryUserTask() {
		fail("Not yet implemented");
	}

	@Test
	public void testDoFinishUserTask() {
		
	}

}
