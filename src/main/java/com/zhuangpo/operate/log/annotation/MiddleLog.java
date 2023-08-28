package com.zhuangpo.operate.log.annotation;

import java.lang.annotation.*;

/**
 * 中级日志
 *
 * @author xub
 * @since 2023/8/26 下午3:54
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MiddleLog {

    /**
     * 操作方式
     */
    String type() default "";

    /**
     * 操作事件
     */
    String operateName() default "";

    /**
     * 详细操作日志
     */
    String operateContent() default "";
}
