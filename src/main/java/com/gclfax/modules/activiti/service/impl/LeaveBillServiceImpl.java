package com.gclfax.modules.activiti.service.impl;

import com.gclfax.common.utils.Query;
import com.gclfax.modules.activiti.dao.LeaveBillDao;
import com.gclfax.modules.activiti.domain.LeaveBill;
import com.gclfax.modules.activiti.service.IBaseProcessService;
import com.gclfax.modules.activiti.service.ILeaveBillService;
import org.activiti.engine.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LeaveBillServiceImpl implements ILeaveBillService,IBaseProcessService<LeaveBill> {

	@Autowired
	private LeaveBillDao leaveBillDao;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;

	/**查询自己的请假单的信息*/
	@Override
	public List<LeaveBill> findLeaveBillList() {
		Map<String,Object> map = new HashMap<>();
		List<LeaveBill> list = leaveBillDao.queryList(map);
		return list;
	}
	
	/**保存请假单*/
	@Override
	public void saveLeaveBill(LeaveBill leaveBill) {
		//获取请假单ID
		Long id = leaveBill.getId();
		/**新增保存*/
		if(id==null){
			//1：从Session中获取当前用户对象，将LeaveBill对象中user与Session中获取的用户对象进行关联
			leaveBill.setUserId(11L);//建立管理关系
			//2：保存请假单表，添加一条数据
			leaveBillDao.save(leaveBill);
		}
		/**更新保存*/
		else{
			//1：执行update的操作，完成更新
			leaveBillDao.update(leaveBill);
		}
		
	}
	
	/**使用请假单ID，查询请假单的对象*/
	@Override
	public LeaveBill findLeaveBillById(Long id) {
		LeaveBill bill = leaveBillDao.queryObject(id);
		return bill;
	}
	
	/**使用请假单ID，删除请假单*/
	@Override
	public void deleteLeaveBillById(Long id) {
		leaveBillDao.delete(id);
	}

	@Override
	public List<LeaveBill> queryList(Query query) {
		return leaveBillDao.queryList(query);
	}

	@Override
	public int queryTotal(Query query) {
		return leaveBillDao.queryTotal(query);
	}

	/**一：使用任务ID，查找请假单ID，从而获取请假单信息*/
	@Override
	public LeaveBill findLeaveBillByTaskId(String taskId) {
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
		String id = "";
		if(StringUtils.isNotBlank(buniness_key)){
			//截取字符串，取buniness_key小数点的第2个值
			id = buniness_key.split("\\.")[1];
		}
		//查询请假单对象
		//使用hql语句：from LeaveBill o where o.id=1
		LeaveBill leaveBill = leaveBillDao.queryObject(Long.parseLong(id));
		return leaveBill;
	}

	@Override
	public LeaveBill queryObjectByBusinessKey(Class LeaveBill, Object id) {
		return leaveBillDao.queryObject(id);
	}
}
