package com.zhuangpo.operate.log.core.custom;

/**
 *  自定义函数接口
 * 
 * @author xub
 * @since 2023/8/30 下午3:42
 */
public interface CustomFunction {

    /**
     * 自定义函数名
     *
     * @return 自定义函数名
     */
    String functionName();

    /**
     * 最终执行的方法
     *
     * @param param 参数
     * @return 执行结果
     */
    String apply(Object param);
}
