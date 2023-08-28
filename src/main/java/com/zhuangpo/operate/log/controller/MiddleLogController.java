package com.zhuangpo.operate.log.controller;

import com.zhuangpo.operate.log.annotation.MiddleLog;
import com.zhuangpo.operate.log.model.UserDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MiddleLogController {

    @GetMapping(value = "/queryUser")
    @MiddleLog(type = "查询", operateName = "用户查询", operateContent = "通过用户名{{#orderNo}}查询用户信息 ")
    public void queryUser(String userName) {
    }

    @PostMapping (value = "/saveUser")
    @MiddleLog(type = "新增", operateName = "新增用户", operateContent = "新增加了一个用户,用户名为{{#userDto.userName}}")
    public String createUser(@RequestBody UserDTO dto) {
        return "成功";
    }
}
