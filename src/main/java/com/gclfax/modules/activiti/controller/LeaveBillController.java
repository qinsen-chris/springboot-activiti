package com.gclfax.modules.activiti.controller;

import com.gclfax.common.utils.PageUtils;
import com.gclfax.common.utils.Query;
import com.gclfax.common.utils.R;
import com.gclfax.modules.activiti.domain.LeaveBill;
import com.gclfax.modules.activiti.service.ILeaveBillService;
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
@RequestMapping("/leaveBill")
public class LeaveBillController extends AbstractController {

    @Autowired
    private ILeaveBillService leaveBillService;

    @RequestMapping("/save")
    public R save(@RequestBody LeaveBill bill){
        bill.setUserId(getUserId());
        leaveBillService.saveLeaveBill(bill);
        return R.ok();
    }

    /**
     * 模板字典列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        //查询列表数据
        Query query = new Query(params);
        List<LeaveBill> queryList = leaveBillService.queryList(query);
        int total = leaveBillService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(queryList, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

}
