package com.zhuangpo.operate.log.core.service;


/**
 *  获取操作人信息
 * 
 * @author xub
 * @since 2023/8/2 上午9:30
 */
public interface UserService {

    /**
     * 获取当前操作者
     *
     * @return
     */
    String getUserName();
}
