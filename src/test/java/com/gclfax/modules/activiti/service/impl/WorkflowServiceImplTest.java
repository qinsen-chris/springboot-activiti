package com.gclfax.modules.activiti.service.impl;

import com.gclfax.modules.activiti.service.IWorkflowService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
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
    }

    @Test
    public void saveStartProcess() throws Exception {
    }

    @Test
    public void startProcessInstanceByMessage() throws Exception {
        String messageName = "dongbeiya";
        String businessKey = "88";
        Map<String,Object> variables = new HashMap<String,Object>();
        workflowService.startProcessInstanceByMessage(messageName,businessKey,variables);
    }

    @Test
    public void findTaskListByName() throws Exception {
    }

    @Test
    public void findTaskFormKeyByTaskId() throws Exception {
    }

    @Test
    public void findLeaveBillByTaskId() throws Exception {
    }

    @Test
    public void findOutComeListByTaskId() throws Exception {
    }

    @Test
    public void saveSubmitTask() throws Exception {
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