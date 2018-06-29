package com.gclfax;

import com.gclfax.modules.activiti.service.ILeaveBillService;
import com.gclfax.modules.activiti.service.IWorkflowService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.List;

/**
 * Created by chenmy on 2018/6/20.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WorkFlowTest {
    @Autowired
    private IWorkflowService workflowService;
    @Autowired
    private ILeaveBillService leaveBillService;

    @Test
    public void deployByResourceFile(){
        workflowService.deployByResourceFile("PactAuditDemo.bpmn","合同审批流程");
    }
    /**
     * 发布流程
     * @return
     */
    @Test
    public void newdeploy() throws Exception {
        //获取页面传递的值
        //1：获取页面上传递的zip格式的文件，格式是File类型
        File file = new File("D:/leaveBill.zip");
        //文件名称
        String filename = "请假单";
        //完成部署
        workflowService.deployeByZip(new FileInputStream(file),filename);
    }
    /**
     * 部署管理首页显示
     * @return
     */
    @Test
    public void deployHome(){
        //1:查询部署对象信息，对应表（act_re_deployment）
        List<Deployment> depList = workflowService.findDeploymentList();
        //2:查询流程定义的信息，对应表（act_re_procdef）
        List<ProcessDefinition> pdList = workflowService.findProcessDefinitionList();
        for (Deployment d : depList) {
            System.out.println(d.getId()+"， 部署 ；"+d.getName());
        }
        for (ProcessDefinition p : pdList) {
            System.out.println(p.getId()+"  ==  "+p.getName());
        }
    }

    /**
     * 删除部署信息
     */
    @Test
    public void delDeployment(){
        //1：获取部署对象ID
        String deploymentId = "2501";
        //2：使用部署对象ID，删除流程定义
        workflowService.deleteProcessDefinitionByDeploymentId(deploymentId);
    }

    /**
     * 查看流程图
     * @throws Exception
     */
    @Test
    public void  viewImage() throws Exception{
        //1：获取页面传递的部署对象ID和资源图片名称
        //部署对象ID
        String deploymentId = "";
        //资源图片名称
        String imageName = "";
        //2：获取资源文件表（act_ge_bytearray）中资源图片输入流InputStream
        InputStream in = workflowService.findImageInputStream(deploymentId,imageName);
        //3：从response对象获取输出流
        OutputStream out = null;//ServletActionContext.getResponse().getOutputStream();
        //4：将输入流中的数据读取出来，写到输出流中
        for(int b=-1;(b=in.read())!=-1;){
            out.write(b);
        }
        out.close();
        in.close();
        //将图写到页面上，用输出流写
    }


}
