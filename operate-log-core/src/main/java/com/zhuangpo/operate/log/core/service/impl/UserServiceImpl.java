package com.zhuangpo.operate.log.core.service.impl;


import com.zhuangpo.operate.log.core.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 获取操作人信息
 *
 * @author xub
 * @since 2023/8/2 上午9:30
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Override
    public String getUserName() {
        //这里模拟从redis或者sisson中获取用户信息
        return "张三";
    }
}
