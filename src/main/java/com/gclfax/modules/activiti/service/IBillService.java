package com.gclfax.modules.activiti.service;

import com.gclfax.common.utils.ErrorInfo;
import com.gclfax.common.utils.Query;
import com.gclfax.modules.activiti.domain.Bill;

import java.util.List;


public interface IBillService {

	List<Bill> findBillList();

	void saveBill(Bill bill);

	Bill findBillById(Long id,ErrorInfo errorInfo);

	void deleteBillById(Long id);

    List<Bill> queryList(Query query);

	int queryTotal(Query query);

	void saveStartProcess(Long id, String userId);

	void saveStartProcessMessage(Long id, String userId);
}
