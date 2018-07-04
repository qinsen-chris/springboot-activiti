package com.gclfax.modules.activiti.controller;

import com.gclfax.common.exception.RRException;
import com.gclfax.common.utils.ErrorInfo;
import com.gclfax.common.utils.PageUtils;
import com.gclfax.common.utils.Query;
import com.gclfax.common.utils.R;
import com.gclfax.modules.activiti.domain.BaseProcess;
import com.gclfax.modules.activiti.domain.Bill;
import com.gclfax.modules.activiti.domain.LeaveBill;
import com.gclfax.modules.activiti.service.IBaseProcessService;
import com.gclfax.modules.activiti.service.IBillService;
import com.gclfax.modules.activiti.service.ILeaveBillService;
import com.gclfax.modules.activiti.service.IWorkflowService;
import com.gclfax.modules.activiti.vo.WorkflowBean;
import com.gclfax.modules.sys.controller.AbstractController;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * Created by chenmy on 2018/6/20.
 */
@RestController
@RequestMapping("/workFlow")
public class WorkFlowController extends AbstractController {

    @Autowired
    private IWorkflowService workflowService;
    @Autowired
    private IBaseProcessService<Bill> baseProcessService;
    @Autowired
    private IBillService billService;

    /**
     * 部署管理
     * @return
     */
    @RequestMapping("/deploymentList")
    public R deploymentList(@RequestParam Map<String, Object> params){
        //1:查询部署对象信息，对应表（act_re_deployment）
        List<Deployment> depList = workflowService.findDeploymentList();
        List<Map<String,Object>> jsonList = new ArrayList<>();
        for (Deployment deploy : depList) {
            Map<String,Object> json = new HashMap<>();
            json.put("id", deploy.getId());
            json.put("name", deploy.getName());
            json.put("deploymentTime", deploy.getDeploymentTime());
            json.put("category", deploy.getCategory());
            jsonList.add(json);
        }
        //查询列表数据
        Query query = new Query(params);
        PageUtils pageUtil = new PageUtils(jsonList, jsonList.size(), query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }
     /**
     * 流程管理
     * @return
     */
    @RequestMapping("/procDefList")
    public R processDefList(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);
        //2:查询流程定义的信息，对应表（act_re_procdef）
        List<ProcessDefinition> pdList = workflowService.findProcessDefinitionList();
        List<Map<String,Object>> jsonList = new ArrayList<>();

        for (ProcessDefinition p : pdList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("key", p.getKey());
            map.put("version", p.getVersion());
            map.put("resourceName", p.getResourceName());
            map.put("diagramResourceName", p.getDiagramResourceName());
            map.put("deploymentId", p.getDeploymentId());
            jsonList.add(map);
        }

        PageUtils pageUtil = new PageUtils(jsonList, jsonList.size(), query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    /**
     * 上传文件
     */
    @RequestMapping("/upload")
    public R upload(@RequestParam("file") MultipartFile file,@RequestParam("processName") String processName) throws Exception {
        if (file.isEmpty()) {
            throw new RRException("上传文件不能为空！");
        }
        if(StringUtils.isEmpty(processName)){
            throw new RRException("流程模板名称不能为空！");
        }
        workflowService.deployeByZip(file.getInputStream(),processName);
        return R.ok();
    }

    /**
     * 上传文件
     */
    @RequestMapping("/deployByFile")
    public R deployByFile(@RequestBody Map<String, Object> params)  {
        String fileName = (String) params.get("fileName");
        String processName = (String) params.get("processName");
        if (StringUtils.isEmpty(fileName)) {
            throw new RRException("源文件名称不能为空！");
        }
        if (StringUtils.isEmpty(processName)) {
            throw new RRException("流程模板名称不能为空！");
        }

        workflowService.deployByResourceFile(fileName,processName);
        return R.ok();
    }

    /**
     * 任务管理显示
     * @return
     */
    @RequestMapping("/taskList")
    public R listTask(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        //1：从Session中获取当前用户名
        //String name = getUser().getUsername();
        String name = (String)params.get("username");
        //2：使用当前用户名查询正在执行的任务表，获取当前任务的集合List<Task>
        List<Task> list = workflowService.findTaskListByName(name);
        List<Map<String,Object>> jsonList = new ArrayList<>();
        for (Task p : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("assignee", p.getAssignee());
            map.put("createTime", p.getCreateTime());
            jsonList.add(map);
        }
        PageUtils pageUtil = new PageUtils(jsonList, jsonList.size(), query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    /**
     *
     */
    @RequestMapping("/viewTask")
    public R viewTaskForm(@RequestParam("taskId")String taskId) throws ClassNotFoundException {
        R r = R.ok();
        //获取任务ID
        /**一：使用任务ID，查找请假单ID，从而获取请假单信息*/
        //LeaveBill leaveBill = leaveBillService.findLeaveBillByTaskId(taskId);
        String[] businessKey = workflowService.findBusinessKeyByTaskId(taskId);
        if(businessKey != null){
            String processType = businessKey[0];
            Class c = Class.forName(processType);
            BaseProcess processInfo  = baseProcessService.queryObjectByBusinessKey(c,businessKey[1]);
            r.put("processInfo",processInfo);
        }

        /**二：已知任务ID，查询ProcessDefinitionEntiy对象，从而获取当前任务完成之后的连线名称，并放置到List<String>集合中*/
        List<String> outcomeList = workflowService.findOutComeListByTaskId(taskId);
        /**三：查询所有历史审核人的审核信息，帮助当前人完成审核，返回List<Comment>*/
        List<Comment> comments = workflowService.findCommentByTaskId(taskId);
        List<Map<String,Object>> commentList = new ArrayList<>();
        for (Comment c : comments) {
            Map<String, Object> map = new HashMap<>();
            map.put("time", c.getTime());
            map.put("userId", c.getUserId());
            map.put("fullMessage", c.getFullMessage());
            commentList.add(map);
        }
        r.put("commentList", commentList);
        r.put("outcomeList",outcomeList);
        return r;
    }

    /**
     * 提交任务
     */
    @RequestMapping("/submitTask")
    public R submitTask(@RequestBody Map<String, Object> params){
        WorkflowBean workflowBean = new WorkflowBean();
        workflowBean.setId(Long.parseLong(params.get("id")+""));
        workflowBean.setComment((String) params.get("comment"));
        workflowBean.setTaskId((String) params.get("taskId"));
        workflowBean.setOutcome((String) params.get("outcome"));
        workflowBean.setUserid((String) params.get("userid"));

        ErrorInfo errorInfo=new ErrorInfo();

        //做业务
        Bill bill =  billService.findBillById(Long.parseLong(params.get("id")+""),errorInfo);
        if(errorInfo.code<0){
            workflowBean.setResult("fail");
        }else{
            workflowBean.setResult("success");
        }

        //提交流程
        workflowService.saveSubmitTask(workflowBean);
        return R.ok();
    }
}

