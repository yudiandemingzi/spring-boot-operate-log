package com.zhuangpo.operate.log.core.handle;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  用于计算和缓存SpEL表达式的共享实用程序类
 * 
 * @author xub
 * @since 2023/8/29 下午2:53
 */
public class OperateLogExpressionEvaluator extends CachedExpressionEvaluator {

    private Map<CachedExpressionEvaluator.ExpressionKey, Expression> expressionCache = new ConcurrentHashMap<>();
    private Map<AnnotatedElementKey, Method> targetMethodCache = new ConcurrentHashMap<>();


    public EvaluationContext createEvaluationContext(Class<?> targetClass, Method method, Object[] args) {
        Method targetMethod = getTargetMethod(targetClass, method);
        return new MethodBasedEvaluationContext(null, targetMethod, args, getParameterNameDiscoverer());
    }


    public String parseExpression(EvaluationContext evaluationContext, AnnotatedElementKey methodKey, String conditionExpression) {
        return getExpression(this.expressionCache, methodKey, conditionExpression).getValue(evaluationContext, String.class);
    }


    private Method getTargetMethod(Class<?> targetClass, Method method) {
        AnnotatedElementKey methodKey = new AnnotatedElementKey(method, targetClass);
        Method targetMethod = this.targetMethodCache.get(methodKey);
        if (targetMethod == null) {
            targetMethod = AopUtils.getMostSpecificMethod(method, targetClass);
            this.targetMethodCache.put(methodKey, targetMethod);
        }
        return targetMethod;
    }
}
