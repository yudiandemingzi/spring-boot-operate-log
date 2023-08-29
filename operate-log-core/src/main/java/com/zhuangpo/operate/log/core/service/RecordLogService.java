package com.zhuangpo.operate.log.core.service;


import com.zhuangpo.operate.log.core.pojo.OperateLogDTO;

/**
 * 插入日志
 *
 * @author xub
 * @since 2023/8/4 上午11:09
 */
public interface RecordLogService {

    /**
     * 插入日志
     *
     * @param logDTO 日志信息
     * @return Void
     */
    void insertLog(OperateLogDTO logDTO);
}
