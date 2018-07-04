package com.gclfax.modules.activiti.service.impl;

import com.gclfax.common.utils.ErrorInfo;
import com.gclfax.common.utils.Query;
import com.gclfax.modules.activiti.dao.BillDao;
import com.gclfax.modules.activiti.domain.Bill;
import com.gclfax.modules.activiti.service.IBaseProcessService;
import com.gclfax.modules.activiti.service.IBillService;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BillServiceImpl implements IBillService,IBaseProcessService<Bill> {

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
	public void saveStartProcess(Long id, String userid) {
		Bill bill = billDao.queryObject(id);
		bill.setState(1);
		billDao.update(bill);
		
		String processKey = bill.getClass().getSimpleName();
		String key = Bill.class.getName();  //获取业务类的完整路径
		Map<String, Object> variables = new HashMap<String, Object>();

		variables.put("userid", userid);// 表示惟一用户

		// 格式：Bill.id的形式（使用流程变量）
		String objId = key + "." + id;
		variables.put("objId", objId);
		// 6：使用流程定义的key，启动流程实例，同时设置流程变量，同时向正在执行的执行对象表中的字段BUSINESS_KEY添加业务数据，同时让流程关联业务
		runtimeService.startProcessInstanceByKey(processKey, objId, variables);
	}

	@Override
	public void saveStartProcessMessage(Long id, String userid) {
		Bill bill = billDao.queryObject(id);
		bill.setState(1);
		billDao.update(bill);

		String messageName = "main-site";
		String key = Bill.class.getName();  //获取业务类的完整路径
		Map<String, Object> variables = new HashMap<String, Object>();

		variables.put("userid", userid);// 表示惟一用户

		// 格式：Bill.id的形式（使用流程变量）
		String objId = key + "." + id;
		variables.put("objId", objId);

		// 6：使用消息名称启动流程
		runtimeService.startProcessInstanceByMessage(messageName,objId, variables);
	}
	
	@Override
	public List<Bill> findBillList() {
		Map<String,Object> map = new HashMap<>();
		List<Bill> list = billDao.queryList(map);
		return list;
	}
	
	@Override
	public void saveBill(Bill bill) {
		//获取请假单ID
		Long id = bill.getId();
		/**新增保存*/
		if(id==null){
			//1：从Session中获取当前用户对象，将Bill对象中user与Session中获取的用户对象进行关联
//			bill.setUserId(11L);//建立管理关系
			//2：保存请假单表，添加一条数据
			billDao.save(bill);
		}
		/**更新保存*/
		else{
			//1：执行update的操作，完成更新
			billDao.update(bill);
		}
		
	}
	
	@Override
	public Bill findBillById(Long id,ErrorInfo errorInfo) {
		Bill bill = billDao.queryObject(id);
		if(bill==null){
			errorInfo.code = -1;
		}
		return bill;
	}
	
	@Override
	public void deleteBillById(Long id) {
		billDao.delete(id);
	}

	@Override
	public List<Bill> queryList(Query query) {
		return billDao.queryList(query);
	}

	@Override
	public int queryTotal(Query query) {
		return billDao.queryTotal(query);
	}

	@Override
	public Bill queryObjectByBusinessKey(Class T, Object id) {
		Bill bill = billDao.queryObject(id);
		return bill;
	}
}
