package com.zhuangpo.operate.log.core.custom;


/**
 *  定义默认函数
 * 
 * @author xub
 * @since 2023/9/1 上午9:26
 */
public class DefaultCustomFunction implements CustomFunction {
 

    @Override
    public String functionName() {
        return "defaultName";
    }

    @Override
    public String apply(Object value) {
        return null;
    }
}
