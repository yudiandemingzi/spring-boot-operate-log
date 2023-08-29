package com.zhuangpo.operate.log.core.aop;


import com.google.common.collect.Lists;
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
        Object[] args = joinPoint.getArgs();
        Object target = joinPoint.getTarget();
        Class<?> targetClass = AopUtils.getTargetClass(target);
        OperateLog annotation = method.getAnnotation(OperateLog.class);
        OperateLogDTO logDTO = this.changeLogAnnotation(annotation);
        Object proceed = joinPoint.proceed();
        this.records(logDTO, targetClass, method, args, proceed);
    }


    private void records(OperateLogDTO logDTO, Class<?> targetClass, Method method, Object[] args, Object retObj) {
        try {

            Map<String, String> optContext = new HashMap<>();
            this.process(logDTO, targetClass, method, args, retObj, optContext);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    private OperateLogDTO process(OperateLogDTO logDTO, Class<?> targetClass, Method method, Object[] args, Object retObj, Map<String, String> optContext) {
        List<String> templates = Lists.newArrayList(logDTO.getOperator(), logDTO.getBizNo(), logDTO.getOperateContent());
        templates = templates.stream().filter(e -> StringUtils.isNotBlank(e)).collect(Collectors.toList());
        Map<String, String> process = process(templates, targetClass, method, args, retObj, optContext);
        logDTO.setOperator(process.get(logDTO.getOperator()));
        logDTO.setBizNo(process.get(logDTO.getBizNo()));
        logDTO.setOperateName(process.get(logDTO.getOperateName()));
        logDTO.setOperateContent(process.get(logDTO.getOperateContent()));
        logDTO.setCondition(process.get(logDTO.getCondition()));
        log.info("查看最终日志 =  {},", logDTO);
        return logDTO;
    }

    /**
     * @param
     * @return
     */
    private Map<String, String> process(Collection<String> templates,
                                        Class<?> targetClass,
                                        Method method,
                                        Object[] args,
                                        Object retObj,
                                        Map<String, String> optContext) {
        Map<String, String> expressionValues = new HashMap<>(16);
        EvaluationContext evaluationContext = expressionEvaluator.createEvaluationContext(targetClass, method, args, retObj, optContext);
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
