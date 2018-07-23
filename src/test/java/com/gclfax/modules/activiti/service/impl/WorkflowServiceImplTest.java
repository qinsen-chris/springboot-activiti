package com.gclfax.modules.activiti.service.impl;

import com.gclfax.modules.activiti.domain.Bill;
import com.gclfax.modules.activiti.service.IWorkflowService;
import com.gclfax.modules.activiti.vo.WorkflowBean;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Create by qs on ${date}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WorkflowServiceImplTest {
    @Autowired
    private IWorkflowService workflowService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private FormService formService;

    /**
     * 部署
     * @throws Exception
     */
    @Test
    public void deployByResourceFile() throws Exception {
        String resourceFilename = "formtest.bpmn";
        String processName = "测试form";
        repositoryService.createDeployment().addClasspathResource("processes/"+resourceFilename).name(processName).deploy();

    }

    /**
     * 发起流程
     * @throws Exception
     */
    @Test
    public void testStartProcess() throws Exception {
        Map<String, Object> variables = new HashMap<String, Object>();

        String key = Bill.class.getName();  //获取业务类的完整路径
        variables.put("userID", "bb");// 表示惟一用户

        // 格式：Bill.id的形式（使用流程变量）
        String objId = key + "." + 1;
        variables.put("objId", objId);
        // 6：使用流程定义的key，启动流程实例，同时设置流程变量，同时向正在执行的执行对象表中的字段BUSINESS_KEY添加业务数据，同时让流程关联业务
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("formtest", objId, variables);
        System.out.println("-------------------ProcessInstance id:" + pi.getId() +" -----processInstanceId" + pi.getProcessInstanceId());
    }

    /**
     * 提交流程
     * @throws Exception
     */
    @Test
    public void testSubmit() throws Exception{
        //获取任务ID
        String taskId = "127506";
        //获取连线的名称
        String outcome = "";
        //批注信息
        String message = "";

        //使用任务ID，查询任务对象，获取流程流程实例ID
        Task task = taskService.createTaskQuery()//
                .taskId(taskId)//使用任务ID查询
                .singleResult();
        //获取流程实例ID
        String processInstanceId = task.getProcessInstanceId();

        Authentication.setAuthenticatedUserId("当前用户名称");//当前用户名称
        taskService.addComment(taskId, processInstanceId, message);

        Map<String, Object> variables = new HashMap<String,Object>();
        //根据连线名称，提交任务
        if(outcome!=null && !outcome.equals("默认提交")){
            variables.put("outcome", outcome);
        }
        //根据业务执行结果，提交任务

        //如果有指定任务执行人
        variables.put("userid","lbbtd");

        //3：使用任务ID，完成当前人的个人任务，同时流程变量
        taskService.complete(taskId, variables);
    }

    /**
     * 测试用户组查询、用户查询、Form Key 查询、IdentityLink查询
     * @throws Exception
     */
    @Test
    public void testGroup() throws Exception {
        //根据group name 查询组
        Group group = identityService.createGroupQuery().groupName("fh").singleResult();
        System.out.println("-------------------group:"+group.getId()+"   "+group.getName());

        //根据groupId 查询组用户
        List<User> userList = identityService.createUserQuery().memberOfGroup("1").list();
        System.out.println("-------------------userList:"+userList);

        //获取人工节点中定义的formkey
        TaskFormData formData = formService.getTaskFormData("102539");
        List<FormProperty> formPropertyList = formData.getFormProperties();
        System.out.println("-------------------formkey:"+formData.getFormKey() + formPropertyList.size());

        //查询运行时流程人员表信息，可以在监听里面重新指定任务
        List<IdentityLink> list = taskService.getIdentityLinksForTask("102539");
        System.out.println("-------------------IdentityLink:"+ list.get(0));
        for (IdentityLink identityLink :list){
            if(identityLink.getType().equals("candidate")){
                System.out.println("-------------------group:" + identityLink.getGroupId());
            }
        }

        Task task = taskService.createTaskQuery().processDefinitionId("107501").singleResult();
        System.out.println("-------------------task:" + task.getId());
    }

    /**
     * 测试 FormProperty ，查询当前任务节点中定义的 FormProperty
     * @throws Exception
     */
    @Test
    public void testFormProperty() throws Exception{
        TaskFormData formData = formService.getTaskFormData("145006");
        List<FormProperty> formPropertyList = formData.getFormProperties();
        for(FormProperty fp : formPropertyList){
            System.out.println("-------------------FormProperty:" + fp.getId() + "---" +fp.getName());
        }
    }

    /**
     * 测试流程变量
     * @throws Exception
     */
    @Test
    public void testVariable() throws Exception{
        TaskFormData formData = formService.getTaskFormData("145006");
        List<FormProperty> formPropertyList = formData.getFormProperties();
        for(FormProperty fp : formPropertyList){
            System.out.println("-------------------FormProperty:" + fp.getId() + "---" +fp.getName());
        }


    }

    /**
     * 执行流 的查询，根据流程定义key,
     * @throws Exception
     */
    @Test
    public void testExecution() throws Exception {
        String processDefinitionKey = "formtest";
        List<Execution> executionList = runtimeService.createExecutionQuery().processDefinitionKey(processDefinitionKey).list();
        for (Execution execution : executionList){
            System.out.println("------------------execution:" + execution.getProcessInstanceId());
        }
        Assert.assertTrue(executionList.size()>0);
    }

    /**
     * formService 启动流程
     * @throws Exception
     */
    @Test
    public void testFormStartProcess() throws Exception{
        String processDefinitionId = "";
        String businessKey = "88";
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("userid","99");
        properties.put("username","100");
        ProcessInstance pi =  formService.submitStartFormData(processDefinitionId,businessKey,properties);
        System.out.println("-------------------ProcessInstance:" +pi.getName());
    }
    /**
     * 根据流程类型和用用户查询任务
     * @throws Exception
     */
    @Test
    public void testQueryTaskByCondition() throws Exception{
        //List<Task> taskList = taskService.createNativeTaskQuery().sql("").list();
        List<Task> taskList = taskService.createTaskQuery().processDefinitionKey("test2").taskCandidateOrAssigned("fh").list();
        System.out.println(taskList);
    }

    /**
     * 根据流程类型和用用户查询任务
     * @throws Exception
     */
    @Test
    public void testQueryTaskBySQL() throws Exception{

        String userId = "fh1";
        String sql = "select distinct res.* from act_ru_task res " +
                "left join act_ru_identitylink ide on ide.TASK_ID_ = res.ID_ " +
                "LEFT JOIN act_ru_execution re on re.ID_ = res.EXECUTION_ID_ " +
                "LEFT JOIN act_re_procdef rp on rp.ID_ = re.PROC_DEF_ID_ " +
                "where (res.ASSIGNEE_ = #{userid} or (res.ASSIGNEE_ is NULL and (ide.USER_ID_ = #{userid} or ide.GROUP_ID_ in (select mem.GROUP_ID_ from act_id_membership mem where mem.USER_ID_=#{userid} )) )) " +
                "ORDER BY res.CREATE_TIME_ DESC  " ;
        List<Task> taskList = taskService.createNativeTaskQuery().sql(sql).parameter("userid",userId).list();
        System.out.println(taskList);
    }


    @Test
    public void deployeTaskVariables() throws Exception {
        Map<String, Object> resultMap = taskService.getVariables("145006");
        System.out.println(resultMap);
    }

    @Test
    public void findDeploymentList() throws Exception {
    }

    @Test
    public void findProcessDefinitionList() throws Exception {
    }

    @Test
    public void findImageInputStream() throws Exception {
    }

    @Test
    public void deleteProcessDefinitionByDeploymentId() throws Exception {
        List<User> userList = identityService.createUserQuery().memberOfGroup("1").list();
        System.out.println(userList);
    }

    @Test
    public void saveStartProcess() throws Exception {
        //在部署流程定义和启动流程实例的中间，设置组任务的办理人，向Activity表中存放组和用户的信息
        GroupEntity group1 = new GroupEntity("1");
        group1.setName("fh");
        GroupEntity group2 = new GroupEntity("2");
        group2.setName("sh");
        identityService.saveGroup(group1);//建立组
        identityService.saveGroup(group2);//建立组
        UserEntity user1 = new UserEntity("u1");
        user1.setFirstName("fh1");
        UserEntity user2 = new UserEntity("u2");
        user2.setFirstName("fh2");
        identityService.saveUser(user1);
        identityService.saveUser(user2);
        identityService.createMembership("u1", "1");//建立组和用户关系
        identityService.createMembership("u2", "1");//建立组和用户关系
    }

    @Test
    public void startProcessInstanceByMessage() throws Exception {
        String messageName = "main-site";
        String businessKey = "com.gclfax.modules.activiti.domain.Bill.88";
        Map<String,Object> variables = new HashMap<String,Object>();
        variables.put("userid","wf");
        variables.put("usertype","vip");
        workflowService.startProcessInstanceByMessage(messageName,businessKey,variables);
    }

    @Test
    public void findTaskListByName() throws Exception {
        List<Task> list = taskService.createTaskQuery().taskCandidateOrAssigned("fh2").list();
        System.out.println(list);

    }

    @Test
    public void findTaskFormKeyByTaskId() throws Exception {

        Task task = taskService.createTaskQuery().taskId("75015").singleResult();
        String processDefId= task.getProcessDefinitionId();
        ProcessDefinition pd =  repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefId).singleResult();
        String deploymentId = pd.getDeploymentId();
        //资源图片名称
        String imageName = pd.getDiagramResourceName();
    }

    @Test
    public void findLeaveBillByTaskId() throws Exception {
    }

    @Test
    public void findOutComeListByTaskId() throws Exception {
    }

    @Test
    public void saveSubmitTask() throws Exception {
        WorkflowBean workflowBean = new WorkflowBean();
        workflowBean.setTaskId("7504");
        workflowBean.setComment("ttt");
        workflowService.saveSubmitTask(workflowBean);
    }

    @Test
    public void findCommentByTaskId() throws Exception {
    }

    @Test
    public void findCommentByLeaveBillId() throws Exception {
    }

    @Test
    public void findProcessDefinitionByTaskId() throws Exception {
    }

    @Test
    public void findCoordingByTask() throws Exception {
    }



}