package com.zhuangpo.operate.log.core.custom;

import org.springframework.stereotype.Component;

/**
 *  通过id获取用户名
 * 
 * @author xub
 * @since 2023/8/30 下午3:44
 */
@Component
public class GetUserNameByUserId implements CustomFunction {
    
    @Override
    public boolean executeBefore() {
        return true;
    }

    @Override
    public String functionName() {
        
        return "getUserNameByUserId";
    }

    @Override
    public String apply(Object value) {
        return "张老三";
    }
}
