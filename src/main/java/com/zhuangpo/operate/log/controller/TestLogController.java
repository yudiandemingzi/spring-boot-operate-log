package com.zhuangpo.operate.log.controller;

import com.zhuangpo.operate.log.annotation.OperateLog;
import com.zhuangpo.operate.log.enums.OperateEnum;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestLogController {
    
    @RequestMapping(value = "/queryUser")
    @OperateLog(type= OperateEnum.SELECT,operateName="用户查询",operateContent="用户查询数据")
    public void queryUser(@RequestParam("userId") String userId) {
        System.out.println("userId = " + userId);
    }

}
