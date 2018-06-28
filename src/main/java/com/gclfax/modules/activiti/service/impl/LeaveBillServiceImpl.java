package com.gclfax.modules.activiti.service.impl;

import com.gclfax.common.utils.Query;
import com.gclfax.modules.activiti.dao.LeaveBillDao;
import com.gclfax.modules.activiti.domain.LeaveBill;
import com.gclfax.modules.activiti.service.ILeaveBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LeaveBillServiceImpl implements ILeaveBillService {

	@Autowired
	private LeaveBillDao leaveBillDao;

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
}
