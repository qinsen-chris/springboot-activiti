package com.gclfax.modules.activiti.controller;

import com.gclfax.modules.activiti.domain.LeaveBill;
import com.gclfax.modules.activiti.service.ILeaveBillService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
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
