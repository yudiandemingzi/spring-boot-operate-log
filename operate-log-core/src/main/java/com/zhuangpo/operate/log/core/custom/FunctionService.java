package com.zhuangpo.operate.log.core.custom;


/**
 *  这里多定义一层，也就是想抽离一些代码
 * 
 * @author xub
 * @since 2023/9/1 上午9:29
 */
public interface FunctionService {

    /**
     * 执行函数
     *
     * @param functionName 函数名
     * @param value        参数
     * @return 执行结果
     */
    String apply(String functionName, Object value);

    /**
     * 是否在拦截的方法执行前执行
     *
     * @param functionName 函数名
     * @return
     */
    boolean executeBefore(String functionName);
}
