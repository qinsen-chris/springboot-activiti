package com.gclfax.modules.activiti.service;

import com.gclfax.modules.activiti.domain.LeaveBill;
import com.gclfax.modules.activiti.vo.WorkflowBean;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

import java.io.InputStream;
import java.util.List;
import java.util.Map;



public interface IWorkflowService {

	/**
	 * 按文件部署流程
	 * @param resourceFilename
	 * @param processName
	 */
	void deployByResourceFile(String resourceFilename,String processName);

	void deployeByZip(InputStream inputStream, String filename);

	List<Deployment> findDeploymentList();

	List<ProcessDefinition> findProcessDefinitionList();

	InputStream findImageInputStream(String deploymentId, String imageName);

	void deleteProcessDefinitionByDeploymentId(String deploymentId);

	void saveStartProcess(Long id);

	/**
	 * 消息开始事件启动流程
	 * @param messageName
	 * @param businessKey
	 * @param variables
	 */
	void startProcessInstanceByMessage(String messageName,String businessKey,Map<String,Object> variables);

	List<Task> findTaskListByName(String name);

	String findTaskFormKeyByTaskId(String taskId);

	List<String> findOutComeListByTaskId(String taskId);

	void saveSubmitTask(WorkflowBean workflowBean);

	List<Comment> findCommentByTaskId(String taskId);

	List<Comment> findCommentByLeaveBillId(Long id);

	ProcessDefinition findProcessDefinitionByTaskId(String taskId);

	Map<String, Object> findCoordingByTask(String taskId);

	

}
