package com.zhuangpo.operate.log.controller;

import com.zhuangpo.operate.log.annotation.OperateLog;
import com.zhuangpo.operate.log.enums.OperateEnum;
import com.zhuangpo.operate.log.model.LogDO;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestLogController {
    
    @RequestMapping(value = "/queryUser")
    @OperateLog(type= OperateEnum.SELECT,operateName="用户查询",operateContent="用户查询数据")
    public void queryUser(@RequestParam("userId") String userId) {
        System.out.println("userId = " + userId);
    }

    @PostMapping(value = "/saveUser")
    @OperateLog(type= OperateEnum.SELECT,operateName="用户查询",operateContent="用户查询数据")
    
    public void saveUser(@RequestBody LogDO logDO) {
        
    }

}
