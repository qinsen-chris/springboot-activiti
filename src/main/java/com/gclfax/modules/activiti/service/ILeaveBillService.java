package com.gclfax.modules.activiti.service;

import com.gclfax.common.utils.Query;
import com.gclfax.modules.activiti.domain.LeaveBill;

import java.util.List;


public interface ILeaveBillService{

	List<LeaveBill> findLeaveBillList();

	void saveLeaveBill(LeaveBill leaveBill);

	LeaveBill findLeaveBillById(Long id);

	void deleteLeaveBillById(Long id);

    List<LeaveBill> queryList(Query query);

	int queryTotal(Query query);

	LeaveBill findLeaveBillByTaskId(String taskId);

}
