package com.gclfax.modules.activiti.service.impl;

import com.gclfax.modules.activiti.dao.BillDao;
import com.gclfax.modules.activiti.domain.Bill;
import com.gclfax.modules.activiti.service.IWorkflowService;
import com.gclfax.modules.activiti.vo.WorkflowBean;
import org.activiti.engine.*;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

@Service
public class WorkflowServiceImpl implements IWorkflowService {
	/**请假申请Dao*/
	@Autowired
	private BillDao billDao;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private FormService formService;
	@Autowired
	private HistoryService historyService;

	@Override
	public void deployByResourceFile(String resourceFilename, String processName) {
		repositoryService.createDeployment().addClasspathResource("processes/"+resourceFilename).name(processName).deploy();
	}

	/**部署流程定义*/
	@Override
	public void deployeByZip(InputStream inputStream, String filename) {
		try {
			//2：将File类型的文件转化成ZipInputStream流
			ZipInputStream zipInputStream = new ZipInputStream(inputStream);
			repositoryService.createDeployment()//创建部署对象
							.name(filename)//添加部署名称
							.addZipInputStream(zipInputStream)//
							.deploy();//完成部署
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**查询部署对象信息，对应表（act_re_deployment）*/
	@Override
	public List<Deployment> findDeploymentList() {
		List<Deployment> list = repositoryService.createDeploymentQuery()//创建部署对象查询
							.orderByDeploymenTime().asc()//
							.list();
		return list;
	}
	
	/**查询流程定义的信息，对应表（act_re_procdef）*/
	@Override
	public List<ProcessDefinition> findProcessDefinitionList() {
		List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()//创建流程定义查询
							.orderByProcessDefinitionVersion().asc()//
							.list();
		return list;
	}
	
	/**使用部署对象ID和资源图片名称，获取图片的输入流*/
	@Override
	public InputStream findImageInputStream(String deploymentId,
			String imageName) {
		return repositoryService.getResourceAsStream(deploymentId, imageName);
	}

	@Override
	public OutputStream viewTaskImage(String taskId,HttpServletResponse response) {
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)//使用任务ID查询
				.singleResult();
		String processDefId= task.getProcessDefinitionId();
		ProcessDefinition pd =  repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefId).singleResult();
		String deploymentId = pd.getDeploymentId();
		//资源图片名称
		String imageName = pd.getDiagramResourceName();
		//2：获取资源文件表（act_ge_bytearray）中资源图片输入流InputStream
		InputStream in = repositoryService.getResourceAsStream(deploymentId, imageName);
		//3：从response对象获取输出流
		//response.setContentType("image/png");

		try {
			OutputStream out = response.getOutputStream();
			//4：将输入流中的数据读取出来，写到输出流中
			for(int b=-1;(b=in.read())!=-1;){
				out.write(b);
			}
			out.close();
			in.close();
			return  out;
		}catch (Exception e){
			e.printStackTrace();
		}finally {

		}
		return null;
	}

	@Override
	public OutputStream viewTaskImageCurr(String taskId, HttpServletResponse response) {
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)//使用任务ID查询
				.singleResult();
		String processDefId= task.getProcessDefinitionId();
		ProcessDefinition pd =  repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefId).singleResult();
		String deploymentId = pd.getDeploymentId();
		//资源图片名称
		String imageName = pd.getDiagramResourceName();
		//2：获取资源文件表（act_ge_bytearray）中资源图片输入流InputStream
		InputStream in = repositoryService.getResourceAsStream(deploymentId, imageName);
		findCoordingByTask(taskId);

		return null;
	}

	/**使用部署对象ID，删除流程定义*/
	@Override
	public void deleteProcessDefinitionByDeploymentId(String deploymentId) {
		repositoryService.deleteDeployment(deploymentId, true);
	}

	@Override
	public void startProcessInstanceByMessage(String messageName,String businessKey,Map<String,Object> variables) {
		runtimeService.startProcessInstanceByMessage(messageName,businessKey,variables);
	}
	
	/**2：使用当前用户名查询正在执行的任务表，获取当前任务的集合List<Task>*/
	@Override
	public List<Task> findTaskListByName(String name) {
		if(StringUtils.isEmpty(name)){
			name = "admin";
		}
		List<Task> list = taskService.createTaskQuery()//
					.taskCandidateOrAssigned(name)//指定个人任务查询
					.orderByTaskCreateTime().asc()//
					.list();
		return list;
	}
	
	/**使用任务ID，获取当前任务节点中对应的Form key中的连接的值*/
	@Override
	public String findTaskFormKeyByTaskId(String taskId) {
		TaskFormData formData = formService.getTaskFormData(taskId);
		//获取Form key的值
		String url = formData.getFormKey();
		return url;
	}

	/**
	 * 根据taskId获取流程中的BusinessKey
	 *
	 * @param taskId
	 * @return
	 */
	@Override
	public String[] findBusinessKeyByTaskId(String taskId) {
		//1：使用任务ID，查询任务对象Task
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)//使用任务ID查询
				.singleResult();
		//2：使用任务对象Task获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		//3：使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
				.processInstanceId(processInstanceId)//使用流程实例ID查询
				.singleResult();
		//4：使用流程实例对象获取BUSINESS_KEY
		String buniness_key = pi.getBusinessKey();
		//5：获取BUSINESS_KEY对应的主键ID，使用主键ID，查询请假单对象（LeaveBill.1）

		if(StringUtils.isNotBlank(buniness_key)){
			//return buniness_key.split("\\.");

			String[] s = new String[2];
			s[0] = buniness_key.substring(0,buniness_key.lastIndexOf("."));
			s[1] = buniness_key.substring(buniness_key.lastIndexOf(".")+1);
			return  s;
		}
		return null;
	}

	/**二：已知任务ID，查询ProcessDefinitionEntiy对象，从而获取当前任务完成之后的连线名称，并放置到List<String>集合中*/
	@Override
	public List<String> findOutComeListByTaskId(String taskId) {
		//返回存放连线的名称集合
		List<String> list = new ArrayList<String>();
		//1:使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery()//
					.taskId(taskId)//使用任务ID查询
					.singleResult();
		//2：获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		//3：查询ProcessDefinitionEntiy对象
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);

		//使用任务对象Task获取流程实例ID,这里有问题，如果是子流程的话，获取ProcessInstanceId，该流程实例已经不活动。
		//String processInstanceId = task.getProcessInstanceId();
		//使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
		//ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		//获取当前活动的id
		// String activityId = pi.getActivityId();

		String executionId = task.getExecutionId();
		Execution ee = runtimeService.createExecutionQuery().executionId(executionId).singleResult();
		String activityId =  ee.getActivityId() ;

		//4：获取当前的活动
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
		//5：获取当前活动完成之后连线的名称
		List<PvmTransition> pvmList = activityImpl.getOutgoingTransitions();
		if(pvmList!=null && pvmList.size()>0){
			for(PvmTransition pvm:pvmList){
				String name = (String) pvm.getProperty("name");
				if(StringUtils.isNotBlank(name)){
					list.add(name);
				}
				else{
					list.add("默认提交");
				}
			}
		}
		return list;
	}
	
	/**指定连线的名称完成任务*/
	@Override
	public void saveSubmitTask(WorkflowBean workflowBean) {
		//获取任务ID
		String taskId = workflowBean.getTaskId();
		//获取连线的名称
		String outcome = workflowBean.getOutcome();
		//批注信息
		String message = workflowBean.getComment();
		//获取ID
		Long id = workflowBean.getId();
		//任务办理人
		String userid = workflowBean.getUserid();

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
		if(StringUtils.isNotEmpty(workflowBean.getResult())){
			variables.put("result", workflowBean.getResult());
		}

		//如果有指定任务执行人
		if(StringUtils.isNotEmpty(userid)){
			variables.put("userid",userid);
		}

		//3：使用任务ID，完成当前人的个人任务，同时流程变量
		taskService.complete(taskId, variables);

		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
						.processInstanceId(processInstanceId)//使用流程实例ID查询
						.singleResult();
		//流程结束了
		if(pi==null){
			//更新请假单表的状态从1变成2（审核中-->审核完成）
			Bill bill = billDao.queryObject(id);
			bill.setState(6);
			billDao.update(bill);
		}
	}
	
	/**获取批注信息，传递的是当前任务ID，获取历史任务ID对应的批注*/
	@Override
	public List<Comment> findCommentByTaskId(String taskId) {
		List<Comment> list = new ArrayList<Comment>();
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		//获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();

		list = taskService.getProcessInstanceComments(processInstanceId);
		return list;
	}
	
	/**1：获取任务ID，获取任务对象，使用任务对象获取流程定义ID，查询流程定义对象*/
	@Override
	public ProcessDefinition findProcessDefinitionByTaskId(String taskId) {
		//使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		//获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		//查询流程定义的对象
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()//创建流程定义查询对象，对应表act_re_procdef 
					.processDefinitionId(processDefinitionId)//使用流程定义ID查询
					.singleResult();
		return pd;
	}
	
	/**
	 * 二：查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中
		 map集合的key：表示坐标x,y,width,height
		 map集合的value：表示坐标对应的值
	 */
	@Override
	public Map<String, Object> findCoordingByTask(String taskId) {
		//存放坐标
		Map<String, Object> map = new HashMap<String,Object>();
		//使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery()//
					.taskId(taskId)//使用任务ID查询
					.singleResult();
		//获取流程定义的ID
		String processDefinitionId = task.getProcessDefinitionId();
		//获取流程定义的实体对象（对应.bpmn文件中的数据）
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processDefinitionId);
/*		//流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		//使用流程实例ID，查询正在执行的执行对象表，获取当前活动对应的流程实例对象
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//创建流程实例查询
					.processInstanceId(processInstanceId)//使用流程实例ID查询
					.singleResult();
		//获取当前活动的ID
		String activityId = pi.getActivityId();
		//获取当前活动对象
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);//活动ID*/

		String executionId = task.getExecutionId();
		Execution ee = runtimeService.createExecutionQuery().executionId(executionId).singleResult();
		String activityId =  ee.getActivityId() ;

		//4：获取当前的活动
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
		//获取坐标
		map.put("x", activityImpl.getX());
		map.put("y", activityImpl.getY());
		map.put("width", activityImpl.getWidth());
		map.put("height", activityImpl.getHeight());
		return map;
	}
}
