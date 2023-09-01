package com.zhuangpo.operate.log.core.custom.impl;

import com.zhuangpo.operate.log.core.custom.CustomFunction;
import com.zhuangpo.operate.log.core.enums.FunctionNameEnum;
import org.springframework.stereotype.Component;

/**
 * 通过id获取用户名
 *
 * @author xub
 * @since 2023/8/30 下午3:44
 */
@Component
public class UserNameFunction implements CustomFunction {

    @Override
    public String functionName() {
        return FunctionNameEnum.GET_USERNAME_BY_USERID.getName();
    }

    @Override
    public String apply(Object value) {
        return "张老三";
    }
}
