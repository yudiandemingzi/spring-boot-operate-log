package com.zhuangpo.operate.log.core.service.impl;


import com.zhuangpo.operate.log.core.pojo.OperateLogDTO;
import com.zhuangpo.operate.log.core.service.RecordLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 获取操作人信息
 *
 * @author xub
 * @since 2023/8/2 上午9:30
 */
@Slf4j
@Service
public class RecordLogServiceImpl implements RecordLogService {


    @Override
    public void insertLog(OperateLogDTO logDTO) {
        log.info("日志操作记录------ {}", logDTO);
    }
}
