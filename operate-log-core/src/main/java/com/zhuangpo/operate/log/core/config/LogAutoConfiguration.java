package com.zhuangpo.operate.log.core.config;


import com.zhuangpo.operate.log.core.aop.OperateLogAspect;
import com.zhuangpo.operate.log.core.custom.CustomFunction;
import com.zhuangpo.operate.log.core.custom.FunctionService;
import com.zhuangpo.operate.log.core.custom.impl.DefaultFunctionServiceImpl;
import com.zhuangpo.operate.log.core.handle.CustomFunctionFactory;
import com.zhuangpo.operate.log.core.handle.LogSpelProcess;
import com.zhuangpo.operate.log.core.service.RecordLogService;
import com.zhuangpo.operate.log.core.service.UserService;
import com.zhuangpo.operate.log.core.service.impl.RecordLogServiceImpl;
import com.zhuangpo.operate.log.core.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * 配置类
 *
 * @author xub
 * @since 2023/9/1 上午9:29
 */
@ComponentScan("com.zhuangpo.operate.log.core")
@Configuration
public class LogAutoConfiguration {


    @Bean
    @Order(3)
    public CustomFunctionFactory CustomFunctionRegistrar(@Autowired List<CustomFunction> iCustomFunctionList) {
        return new CustomFunctionFactory(iCustomFunctionList);
    }

    @Bean
    @Order(4)
    public FunctionService customFunctionService(CustomFunctionFactory customFunctionFactory) {
        return new DefaultFunctionServiceImpl(customFunctionFactory);
    }

    @Bean
    @Order(5)
    public LogSpelProcess logSpelProcess(FunctionService functionService) {
        return new LogSpelProcess(functionService);
    }

    @Bean
    public RecordLogService recordLogService() {
        return new RecordLogServiceImpl();
    }

    @Bean
    public UserService userService() {
        return new UserServiceImpl();
    }

    @Bean
    @Order(6)
    public OperateLogAspect operateLogAspect(LogSpelProcess logSpelProcess, UserService userService, RecordLogService recordLogService) {
        return new OperateLogAspect(logSpelProcess, recordLogService, userService);
    }

}
