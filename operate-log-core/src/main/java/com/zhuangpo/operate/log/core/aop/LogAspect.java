package com.zhuangpo.operate.log.core.aop;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhuangpo.operate.log.core.handle.OperateLogExpressionEvaluator;
import com.zhuangpo.operate.log.core.pojo.OperateLogDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public void afterReturning(ProceedingJoinPoint joinPoint) throws Throwable {
        insertLog(joinPoint);
    }


    /**
     * 进行插入日志
     *
     * @param joinPoint
     */
    private void insertLog(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取切点方法上的注解
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        OperateLog annotation = method.getAnnotation(OperateLog.class);
        this.records(annotation, joinPoint);
    }


    private OperateLogDTO records(OperateLog annotation, ProceedingJoinPoint joinPoint) throws Throwable {
        HashMap<String, String> optContext = Maps.newHashMap();
        List<String> templates = Lists.newArrayList(annotation.operator(), annotation.bizNo(), annotation.operateContent());
        templates = templates.stream().filter(e -> StringUtils.isNotBlank(e)).collect(Collectors.toList());
        Map<String, String> process = this.process(templates, joinPoint, optContext);
        OperateLogDTO logDTO = new OperateLogDTO();
        logDTO.setType(annotation.type().getName());
        logDTO.setOperator(process.get(annotation.operator()));
        logDTO.setBizNo(process.get(annotation.bizNo()));
        logDTO.setOperateName(annotation.operateName());
        logDTO.setOperateContent(process.get(annotation.operateContent()));
        log.info("查看最终日志 =  {},", logDTO);
        return logDTO;
    }

    /**
     * @param
     * @return
     */
    private Map<String, String> process(Collection<String> templates,
                                        ProceedingJoinPoint joinPoint,
                                        Map<String, String> optContext) throws Throwable {
        Map<String, String> expressionValues = new HashMap<>(16);
        // 获取切点方法上的注解
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();
        Object target = joinPoint.getTarget();
        Class<?> targetClass = AopUtils.getTargetClass(target);
        Object proceed = joinPoint.proceed();
        EvaluationContext evaluationContext = expressionEvaluator.createEvaluationContext(targetClass, method, args, proceed, optContext);
        for (String tpl : templates) {
            expressionValues.put(tpl, tpl);
            AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(method, targetClass);
            try {
                String value = expressionEvaluator.parseExpression(evaluationContext, annotatedElementKey, tpl);
                expressionValues.put(tpl, value);
            } catch (Exception e) {
                log.info("解析操作日志SpEL出错 ={}", e.getMessage());
            }
        }
        return expressionValues;
    }
}
