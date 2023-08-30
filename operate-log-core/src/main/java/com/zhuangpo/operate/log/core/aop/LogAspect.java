package com.zhuangpo.operate.log.core.aop;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhuangpo.operate.log.core.handle.OperateLogExpressionEvaluator;
import com.zhuangpo.operate.log.core.pojo.OperateLogDTO;
import com.zhuangpo.operate.log.core.service.RecordLogService;
import com.zhuangpo.operate.log.core.service.UserService;
import com.zhuangpo.operate.log.core.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
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

    @Autowired
    private RecordLogService recordLogService;

    @Autowired
    private UserService userService;

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
        OperateLogDTO operateLogDTO = this.recordLog(annotation, joinPoint);
        //如果是系统异常那就直接抛出异常也不不需要记录日志，但如果哦是业务异常，那就用记录这个日志是否成功
        Object proceed = joinPoint.proceed();
        JSONObject jsonObject = JSONUtil.parseObj(proceed);
        Integer code = (Integer) jsonObject.get("code");
        String message = (String) jsonObject.get("message");
        if (Objects.equals(ResultCode.SUCCESS.getCode(), code)) {
            operateLogDTO.setStatus(Boolean.TRUE);
        } else {
            operateLogDTO.setStatus(Boolean.FALSE);
            operateLogDTO.setErrMsg(message);
        }
        CompletableFuture.runAsync(() ->
                recordLogService.insertLog(operateLogDTO)
        );
    }

    /**
     * 生成最终日志
     *
     * @param annotation 注解
     * @return joinPoint 切面
     */
    private OperateLogDTO recordLog(OperateLog annotation, ProceedingJoinPoint joinPoint) {
        //获取存在Spel表达式的属性
        List<String> templates = Lists.newArrayList(annotation.operator(), annotation.operateName(), annotation.bizNo(), annotation.operateContent());
        templates = templates.stream().filter(e -> StringUtils.isNotBlank(e)).collect(Collectors.toList());
        Map<String, String> process = this.process(templates, joinPoint);
        OperateLogDTO logDTO = new OperateLogDTO();
        logDTO.setType(annotation.type().getName());
        logDTO.setOperator(process.get(annotation.operator()));
        logDTO.setBizNo(process.get(annotation.bizNo()));
        logDTO.setOperateName(process.get(annotation.operateName()));
        logDTO.setOperateContent(process.get(annotation.operateContent()));
        return logDTO;
    }

    /**
     * key 为字段未SPEL解析属性, value为已解析的属性，如果解析失败依旧是未解析属性
     * <p>
     * 这里巧妙在 spelValues.put(tpl, tpl);后期如果解析失败那获取的是未解析成功或者不需要解析的属性
     *
     * @param templates 需要解析的属性
     * @param joinPoint 切面
     * @return 已经解析过的map
     */
    private Map<String, String> process(List<String> templates,
                                        ProceedingJoinPoint joinPoint
    ) {
        Map<String, String> spelValues = Maps.newHashMap();
        // 获取切点方法上的注解
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();
        Object target = joinPoint.getTarget();
        Class<?> targetClass = AopUtils.getTargetClass(target);
        EvaluationContext evaluationContext = expressionEvaluator.createEvaluationContext(targetClass, method, args);
        for (String tpl : templates) {
            spelValues.put(tpl, tpl);
            try {
                AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(method, targetClass);
                String value = expressionEvaluator.parseExpression(evaluationContext, annotatedElementKey, tpl);
                spelValues.put(tpl, value);
            } catch (Exception e) {
                log.info("解析操作日志SpEL出错 ={}", e.getMessage());
            }
        }
        return spelValues;
    }
}
