package com.zhuangpo.operate.log.test.controller;

import com.zhuangpo.operate.log.core.aop.OperateLog;
import com.zhuangpo.operate.log.core.util.Result;
import com.zhuangpo.operate.log.test.pojo.UserDTO;
import org.springframework.web.bind.annotation.*;


@RestController
public class LogTestController {

    @GetMapping(value = "/queryUser")
    @OperateLog(bizNo = "{#userName}", operateName = "查询用户", operateContent = "通过 {#userName} 查询用户")
    public Result<String> queryUser(@RequestParam String userName) {
        return Result.success(userName);
    }

    @PostMapping(value = "/updateUser")
    @OperateLog(bizNo = "{#dto.userId}", operateName = "更新用户", operateContent = "将用户id为{#dto.userId} 的用户名更新为{#dto.userName}")
    public Result<Void> updateUser(@RequestBody UserDTO dto) {
        return Result.success();
    }

    @PostMapping(value = "/saveUser")
    @OperateLog(bizNo = "{#dto.userId}", operateName = "新增用户", operateContent = "新增用户名为{#dto.userName}")
    public Result<Void> saveUser(@RequestBody UserDTO dto) {
        return Result.failed("该用户名称已存在");
    }


    @GetMapping(value = "/deleteUser")
    @OperateLog(bizNo = "{#userId}", operateName = "删除用户",
            operateContent = "用户id为 {#userId} 用户名为 [getUserNameByUserId{#userId}] 已被删除")
    public Result<Void> deleteUser(Long userId) {
        return Result.success();
    }

    @PostMapping(value = "/saveOrUpdateUser")
    @OperateLog(operateName = "#dto.userId == null ? '新增用户':'更新用户'",
            operateContent = "#dto.userId == null ? '新增' + #dto.userName + '用户':'将用户id为' + #dto.userId + '的用户名更新为' + #dto.userName")
    public Result<Void> saveOrUpdateUser(@RequestBody UserDTO dto) {
        return Result.success();
    }
}
