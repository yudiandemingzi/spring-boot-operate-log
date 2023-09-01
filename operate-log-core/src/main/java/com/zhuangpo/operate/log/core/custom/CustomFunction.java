package com.zhuangpo.operate.log.core.custom;

/**
 *  自定义函数接口
 * 
 * @author xub
 * @since 2023/8/30 下午3:42
 */
public interface CustomFunction {

    /**
     * 目标方法执行前 执行自定义函数
     *
     * @return 是否执行前的函数
     */
    boolean executeBefore();

    /**
     * 自定义函数名
     *
     * @return 自定义函数名
     */
    String functionName();

    /**
     * 自定义函数
     *
     * @param param 参数
     * @return 执行结果
     */
    String apply(Object param);
}
