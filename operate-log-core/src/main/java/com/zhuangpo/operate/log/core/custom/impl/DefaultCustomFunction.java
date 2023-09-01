package com.zhuangpo.operate.log.core.custom.impl;


import com.zhuangpo.operate.log.core.custom.CustomFunction;
import com.zhuangpo.operate.log.core.enums.FunctionNameEnum;
import org.springframework.stereotype.Component;

/**
 * 定义默认函数
 *
 * @author xub
 * @since 2023/9/1 上午9:26
 */
@Component
public class DefaultCustomFunction implements CustomFunction {


    @Override
    public String functionName() {
        return FunctionNameEnum.DEFAULT_NAME.getName();
    }

    @Override
    public String apply(Object value) {
        return null;
    }
}
