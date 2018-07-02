package com.gclfax.modules.activiti.service.impl;

import com.gclfax.common.utils.Query;
import com.gclfax.common.utils.UserContextUtils;
import com.gclfax.modules.activiti.dao.BillDao;
import com.gclfax.modules.activiti.domain.Bill;
import com.gclfax.modules.activiti.domain.LeaveBill;
import com.gclfax.modules.activiti.service.IBillService;
import com.gclfax.modules.sys.entity.SysUserEntity;

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
public class BillServiceImpl implements IBillService {

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
		Bill leaveBill = billDao.queryObject(id);
		leaveBill.setState(1);
		billDao.update(leaveBill);
		
		String key = leaveBill.getClass().getSimpleName();
		Map<String, Object> variables = new HashMap<String, Object>();

		variables.put("userid", userid);// 表示惟一用户

		// 格式：Bill.id的形式（使用流程变量）
		String objId = key + "." + id;
		variables.put("objId", objId);
		// 6：使用流程定义的key，启动流程实例，同时设置流程变量，同时向正在执行的执行对象表中的字段BUSINESS_KEY添加业务数据，同时让流程关联业务
		runtimeService.startProcessInstanceByKey(key, objId, variables);
	}
	

	/**查询自己的请假单的信息*/
	@Override
	public List<Bill> findBillList() {
		Map<String,Object> map = new HashMap<>();
		List<Bill> list = billDao.queryList(map);
		return list;
	}
	
	/**保存请假单*/
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
	
	/**使用请假单ID，查询请假单的对象*/
	@Override
	public Bill findBillById(Long id) {
		Bill bill = billDao.queryObject(id);
		return bill;
	}
	
	/**使用请假单ID，删除请假单*/
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
}
