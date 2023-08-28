package com.zhuangpo.operate.log.aop;


import com.zhuangpo.operate.log.annotation.PrimaryLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 初级日志切面
 *
 * @author xub
 * @since 2023/8/26 下午4:05
 */
@Slf4j
@Aspect
@Component
public class PrimaryLogAspect {

    /**
     * 定义切点
     */
    @Pointcut("@annotation(com.zhuangpo.operate.log.annotation.PrimaryLog)")
    public void pointcutLog() {
    }

    /**
     * 在原方法成功执行之后，再调用此通知。
     */
    @AfterReturning("pointcutLog()")
    public void afterReturning(JoinPoint joinPoint) {
        insertLog(joinPoint);
    }

    /**
     * 进行插入日志
     */
    private void insertLog(JoinPoint joinPoint) {
        // 获取切点方法上的注解
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        PrimaryLog annotation = method.getAnnotation(PrimaryLog.class);
        log.info("初级日志，当前用户进行了操作日志:操作者={},操作方式={},操作事件={},操作内容={}"
                , annotation.type(), annotation.operateName(), annotation.operateContent());
    }
}
