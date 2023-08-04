package com.zhuangpo.operate.log.service;


/**
 *  获取操作人信息
 * 
 * @author xub
 * @since 2023/8/2 上午9:30
 */
public interface OperatorService {

    /**
     * 获取当前操作者
     *
     * @return
     */
    String getOperatorName();
}
