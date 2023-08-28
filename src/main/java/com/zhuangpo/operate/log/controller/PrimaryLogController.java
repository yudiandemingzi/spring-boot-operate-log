package com.zhuangpo.operate.log.controller;

import com.zhuangpo.operate.log.annotation.PrimaryLog;

import org.springframework.web.bind.annotation.*;

@RestController
public class PrimaryLogController {

    @GetMapping(value = "/queryOrder")
    @PrimaryLog(type = "查询", operateName = "订单查询", operateContent = "用户进行了订单查询操作")
    public void queryOrder(String orderNo) {
    }
}
