package com.gclfax.modules.activiti.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.persistence.entity.UserEntity;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gclfax.common.utils.PageUtils;
import com.gclfax.common.utils.R;
import com.gclfax.modules.activiti.vo.UserTaskVo;
import com.gclfax.modules.sys.controller.AbstractController;


@RestController
@RequestMapping("/activiti")  
public class DemoController  extends AbstractController{  
    @Autowired    
    private RepositoryService repositoryService;   
    @Autowired    
    private RuntimeService runtimeService;    
    @Autowired    
    private TaskService taskService;    
    @Autowired
    private IdentityService identityService;

	@RequestMapping("/deployTest")
	@RequiresPermissions("pactDict:save")
	public void deployTest() {

		//根据bpmn文件部署流程
		Deployment deployment = repositoryService.createDeployment().addClasspathResource("processes/myProcess1.bpmn").deploy();
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


      
    @RequestMapping("/firstDemo")  
    @RequiresPermissions("pactDict:save")
    public void firstDemo() {  
  
        //根据bpmn文件部署流程  
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("processes/myProcess1.bpmn").deploy();  
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

        //启动流程定义，返回流程实例  
        ProcessInstance pi = runtimeService.startProcessInstanceById(processDefinition.getId());  
        String processId = pi.getId();  
        System.out.println("流程创建成功，当前流程实例ID："+processId);  
          
        Task task=taskService.createTaskQuery().processInstanceId(processId).singleResult();  
        System.out.println("第一次执行前，任务名称："+task.getName());  
        taskService.complete(task.getId());  
  
/*        task = taskService.createTaskQuery().processInstanceId(processId).singleResult();  
        System.out.println("第二次执行前，任务名称："+task.getName());  
        taskService.complete(task.getId());  
  
        task = taskService.createTaskQuery().processInstanceId(processId).singleResult();  
        System.out.println("task为null，任务执行完毕："+task);  */
    }
    
    @RequestMapping("/startTask")
    public R startUserTask(){
    	//根据bpmn文件部署流程  
        repositoryService.createDeployment().addClasspathResource("processes/MyProcess2.bpmn").addClasspathResource("processes/MyProcess2.png").deploy();  
        //获取流程定义  
        //ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();  

    	Map<String, Object> variables = new HashMap<String, Object>();
    	variables.put("userID", "张三");
    	
        //启动流程定义，返回流程实例  
		//String processDefinitionKey = "gxs_leave";
    	String processDefinitionKey = "test2";
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(processDefinitionKey,variables);
        String processId = pi.getId();  
        System.out.println("流程创建成功，当前流程实例ID："+processId+" ，当前实例name:"+pi.getName());  
        
        Task task=taskService.createTaskQuery().processInstanceId(processId).singleResult();  
        System.out.println("第一次执行前，任务名称："+task.getName()); 
    	Map<String, Object> variables2 = new HashMap<String, Object>();
    	variables2.put("userId3", "李四");
        taskService.complete(task.getId(),variables2);  
  
        /*
        task = taskService.createTaskQuery().processInstanceId(processId).singleResult();  
        System.out.println("第二次执行前，任务名称："+task.getName());  
        taskService.complete(task.getId());  
  
        task = taskService.createTaskQuery().processInstanceId(processId).singleResult();  
        System.out.println("task为null，任务执行完毕："+task);  */
        return R.ok();
    }

    @RequestMapping("/queryTask")  
    public R queryUserTask(String name){
    	if(StringUtils.isEmpty(name)){
    		name = "小张";
    	}
    	List<Task> list = taskService.createTaskQuery().taskAssignee(name).list();
    	
    	List<UserTaskVo> userTaskList = new ArrayList<UserTaskVo>();
    	for(Task task : list){
    		System.out.println("task id："+task.getId());
    		System.out.println("task name："+task.getName());
    		System.out.println("task assignee："+task.getAssignee());
    		System.out.println("task executionId："+task.getExecutionId());
    		UserTaskVo vo = new UserTaskVo();
    		vo.setId(task.getId());
    		vo.setName(task.getName());
    		vo.setAssignee(task.getAssignee());
    		vo.setExecutionId(task.getExecutionId());
    		vo.setCreateTime(task.getCreateTime());
    		userTaskList.add(vo);
    	}
    	
    	int total = userTaskList.size();
    	//TODO activiti如何进行分页查询
    	PageUtils pageUtil = new PageUtils(userTaskList, total, 10, 1);
    	return R.ok().put("page", pageUtil);
    	
    }
    
    @RequestMapping("/doFinishUserTask")  
    public R doFinishUserTask(@RequestBody String id){

    	Task task= taskService.createTaskQuery().taskId(id).singleResult();
    	System.out.println("第一次执行前，任务名称："+task);  
    	//Task task= taskService.createTaskQuery().processInstanceId(taskId).singleResult();
    	List<Task> list = taskService.createTaskQuery().taskAssignee("小张").list();
    	/*for(Task task :list){
    		//taskService.addComment(taskId, task.getProcessInstanceId(), "请假三天");
    		if(task.getId().equals(taskId)){
    			taskService.complete(taskId);
    		}
    	}*/
    	
    	//taskService.complete(taskId);
    	
    	return R.ok();
    }
    
    @RequestMapping("/viewTaskImage")  
    public R viewTaskImage(@RequestBody String id){

		/**一：查看流程图*/
		//1：获取任务ID，获取任务对象，使用任务对象获取流程定义ID，查询流程定义对象
//		ProcessDefinition pd = workflowService.findProcessDefinitionByTaskId(id);
		//使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery()//
					.taskId(id)//使用任务ID查询
					.singleResult();
		//获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		//查询流程定义的对象
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()//创建流程定义查询对象，对应表act_re_procdef 
					.processDefinitionId(processDefinitionId)//使用流程定义ID查询
					.singleResult();
		
		//workflowAction_viewImage?deploymentId=<s:property value='#deploymentId'/>&imageName=<s:property value='#imageName'/>
		//ValueContext.putValueContext("deploymentId", pd.getDeploymentId());
		//ValueContext.putValueContext("imageName", pd.getDiagramResourceName());
		/**二：查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中*/
		//Map<String, Object> map = workflowService.findCoordingByTask(taskId);
		
		//存放坐标
		Map<String, Object> map = new HashMap<String,Object>();

		//获取流程定义的实体对象（对应.bpmn文件中的数据）
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processDefinitionId);
		//流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		//使用流程实例ID，查询正在执行的执行对象表，获取当前活动对应的流程实例对象
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//创建流程实例查询
					.processInstanceId(processInstanceId)//使用流程实例ID查询
					.singleResult();
		//获取当前活动的ID
		String activityId = pi.getActivityId();
		//获取当前活动对象
		/*ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);//活动ID
		//获取坐标
		map.put("x", activityImpl.getX());
		map.put("y", activityImpl.getY());
		map.put("width", activityImpl.getWidth());
		map.put("height", activityImpl.getHeight());
		ValueContext.putValueContext("acs", map);*/
    	
    	return R.ok();
    }
}
