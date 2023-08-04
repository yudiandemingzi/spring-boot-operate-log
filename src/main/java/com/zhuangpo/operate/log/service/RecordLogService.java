package com.zhuangpo.operate.log.service;

import com.zhuangpo.operate.log.model.LogDO;

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
     * @param logDO 日志信息
     * @return Void
     */
    void insertLog(LogDO logDO);
}
