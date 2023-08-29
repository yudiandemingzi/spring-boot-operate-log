package com.zhuangpo.operate.log.core.handle;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OperateLogExpressionEvaluator extends CachedExpressionEvaluator {

    private Map<CachedExpressionEvaluator.ExpressionKey, Expression> expressionCache = new ConcurrentHashMap<>();
    private Map<AnnotatedElementKey, Method> targetMethodCache = new ConcurrentHashMap<>();


    public EvaluationContext createEvaluationContext(Class<?> targetClass, Method method, Object[] args,
                                                     Object retObj, Map<String, String> optContext) {
        Method targetMethod = getTargetMethod(targetClass, method);
        return new OperateLogEvaluationContext(null, targetMethod, args, getParameterNameDiscoverer(), retObj, optContext);
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
