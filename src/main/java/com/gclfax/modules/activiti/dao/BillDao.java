package com.gclfax.modules.activiti.dao;

import org.apache.ibatis.annotations.Mapper;

import com.gclfax.modules.activiti.domain.Bill;
import com.gclfax.modules.sys.dao.BaseDao;

/**
 * Created by chenmy on 2018/6/20.
 */
@Mapper
public interface BillDao  extends BaseDao<Bill> {
	
}
