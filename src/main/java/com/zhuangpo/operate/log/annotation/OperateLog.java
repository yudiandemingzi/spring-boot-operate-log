package com.zhuangpo.operate.log.annotation;

import com.zhuangpo.operate.log.enums.OperateEnum;

import java.lang.annotation.*;

/**
 * 日志记录注解
 *
 * @author xub
 * @since 2023/8/2 上午9:22
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface OperateLog {

    /**
     * 操作者
     */
    String operator() default "";

    /**
     * 操作方式
     */
    OperateEnum type() default OperateEnum.UNKNOWN;

    /**
     * 关联的业务id(订单号、业务编号)
     */
    String bizNo() default "";

    /**
     * 操作事件名称
     */
    String operateName() default "";

    /**
     * 比较详细的一条操作日志 比如某某订单已经发货
     */
    String operateContent() default "";

    /**
     * 记录条件 默认 true
     */
    String condition() default "";

}
