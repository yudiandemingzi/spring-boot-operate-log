package com.zhuangpo.operate.log.core.config;


import com.zhuangpo.operate.log.core.custom.*;
import com.zhuangpo.operate.log.core.service.RecordLogService;
import com.zhuangpo.operate.log.core.service.UserService;
import com.zhuangpo.operate.log.core.service.impl.RecordLogServiceImpl;
import com.zhuangpo.operate.log.core.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import java.util.List;

/**
 * 配置类
 *
 * @author xub
 * @since 2023/9/1 上午9:29
 */
@Configuration
public class LogAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(CustomFunction.class)
    @Role(BeanDefinition.ROLE_APPLICATION)
    public CustomFunction customFunction() {
        return new DefaultCustomFunction();
    }

    @Bean
    public CustomFunctionFactory CustomFunctionRegistrar(@Autowired List<CustomFunction> iCustomFunctionList) {
        return new CustomFunctionFactory(iCustomFunctionList);
    }

    @Bean
    public FunctionService customFunctionService(CustomFunctionFactory customFunctionFactory) {
        return new DefaultFunctionServiceImpl(customFunctionFactory);
    }
    
   @Bean
    public RecordLogService recordLogService() {
        return new RecordLogServiceImpl();
    }

    @Bean
    public UserService userService() {
        return new UserServiceImpl();
    }

}
