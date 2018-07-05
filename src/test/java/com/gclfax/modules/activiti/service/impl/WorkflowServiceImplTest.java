package com.gclfax.modules.activiti.service.impl;

import com.gclfax.modules.activiti.domain.Bill;
import com.gclfax.modules.activiti.service.IWorkflowService;
import com.gclfax.modules.activiti.vo.WorkflowBean;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

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


    @Test
    public void deployByResourceFile() throws Exception {
    }

    @Test
    public void deployeByZip() throws Exception {
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