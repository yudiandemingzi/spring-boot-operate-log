package com.zhuangpo.operate.log.core.aop;


import com.google.common.collect.Lists;
import com.zhuangpo.operate.log.core.handle.OperateLogExpressionEvaluator;
import com.zhuangpo.operate.log.core.pojo.OperateLogDTO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 日志切面
 *
 * @author xub
 * @since 2023/8/4 上午11:24
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    private OperateLogExpressionEvaluator expressionEvaluator = new OperateLogExpressionEvaluator();

    /**
     * 定义切点
     */
    @Pointcut("@annotation(com.zhuangpo.operate.log.core.aop.OperateLog)")
    public void pointcutLog() {
    }

    /**
     * 在原方法成功执行之后，再调用此通知。
     *
     * @param joinPoint
     */
    @Around("pointcutLog()")
    public void afterReturning(ProceedingJoinPoint joinPoint) {
        insertLog(joinPoint);
    }


    /**
     * 进行插入日志
     *
     * @param joinPoint
     */
    private void insertLog(ProceedingJoinPoint joinPoint) {
        // 获取切点方法上的注解
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();
        Object target = joinPoint.getTarget();
        Class<?> targetClass = AopUtils.getTargetClass(target);
        // 1 定义解析器
        OperateLog annotation = method.getAnnotation(OperateLog.class);
        OperateLogDTO logDTO = changeLogAnnotation(annotation);
        Object proceed = null;
        Throwable throwable = null;
        try {
            proceed = joinPoint.proceed();
        } catch (Throwable e) {
            throwable = e;
        }
        this.records(logDTO, targetClass, method, args, proceed, throwable);
    }


    private void records(OperateLogDTO logDTO, Class<?> targetClass, Method method, Object[] args, Object retObj, Throwable throwable) {
        try {

            Map<String, String> optContext = new HashMap<>();
            this.process(logDTO, targetClass, method, args, retObj, throwable == null ? null : throwable.getMessage(), optContext);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    private OperateLogDTO process(OperateLogDTO logDTO, Class<?> targetClass, Method method, Object[] args, Object retObj, String errorMsg, Map<String, String> optContext) {
       List<String> templates = Lists.newArrayList(logDTO.getOperator(),logDTO.getBizNo(),logDTO.getOperateContent());
        Map<String, String> process = process(templates, targetClass, method, args, retObj, errorMsg, optContext);
        logDTO.setOperator(process.get(logDTO.getOperator()));
        logDTO.setBizNo(process.get(logDTO.getBizNo()));
        logDTO.setOperateName(process.get(logDTO.getOperateName()));
        logDTO.setOperateContent(process.get(logDTO.getOperateContent()));
        logDTO.setCondition(process.get(logDTO.getCondition()));
        log.info("查看最终日志 =  {},", logDTO);
        return logDTO;
    }

    private Map<String, String> process(Collection<String> templates, Class<?> targetClass, Method method, Object[] args, Object retObj, String errorMsg, Map<String, String> optContext) {
        Map<String, String> expressionValues = new HashMap<>(16);
        EvaluationContext evaluationContext = expressionEvaluator.createEvaluationContext(targetClass, method, args, retObj, errorMsg, optContext);
        for (String tpl : templates) {
            if (tpl == null || tpl.isEmpty()) {
                expressionValues.put(tpl, tpl);
                continue;
            }
            AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(method, targetClass);
            try {
                String value = expressionEvaluator.parseExpression(evaluationContext, annotatedElementKey, tpl);
                expressionValues.put(tpl, value);
            } catch (Exception e) {
                expressionValues.put(tpl, tpl);
                log.error("解析操作日志SpEL【" + tpl + "】错误，" + e.getMessage());
            }
        }
        return expressionValues;
    }


    /**
     * OperateLog注解转成对应实体
     *
     * @param log 注解属性
     * @return 实体属性
     */
    private OperateLogDTO changeLogAnnotation(OperateLog log) {
        OperateLogDTO operateLogDTO = new OperateLogDTO();
        operateLogDTO.setOperator(log.operator());
        operateLogDTO.setType(log.type());
        operateLogDTO.setBizNo(log.bizNo());
        operateLogDTO.setOperateName(log.operateName());
        operateLogDTO.setOperateContent(log.operateContent());
        operateLogDTO.setCondition(log.condition());
        return operateLogDTO;
    }


}
