package com.gclfax.modules.activiti.controller;

import com.gclfax.common.utils.PageUtils;
import com.gclfax.common.utils.Query;
import com.gclfax.common.utils.R;
import com.gclfax.modules.activiti.domain.Bill;
import com.gclfax.modules.activiti.service.IBillService;
import com.gclfax.modules.sys.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by chenmy on 2018/6/20.
 */
@RestController
@RequestMapping("/bill")
public class BillController extends AbstractController {

    @Autowired
    private IBillService billService;

    @RequestMapping("/save")
    public R save(@RequestBody Bill bill){
        bill.setUserId(getUserId());
        billService.saveBill(bill);
        return R.ok();
    }

    /**
     * 模板字典列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        //查询列表数据
        Query query = new Query(params);
        List<Bill> queryList = billService.queryList(query);
        int total = billService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(queryList, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }
    
 // 启动流程
    @RequestMapping("/startProcess")
    public R startProcess(@RequestBody Map<String, Object> params){
    	Long id = Long.valueOf((params.get("id")+""));
    	String userId = (String) params.get("userId");
        //更新请假状态，启动流程实例，让启动的流程实例关联业务
    	billService.saveStartProcess(id,userId);
        return R.ok();
    }

}
