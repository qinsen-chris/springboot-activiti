package com.gclfax.modules.oss.dao;

import org.apache.ibatis.annotations.Mapper;

import com.gclfax.modules.oss.entity.SysOssEntity;
import com.gclfax.modules.sys.dao.BaseDao;

/**
 * 文件上传
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-25 12:13:26
 */
@Mapper
public interface SysOssDao extends BaseDao<SysOssEntity> {
	
}
