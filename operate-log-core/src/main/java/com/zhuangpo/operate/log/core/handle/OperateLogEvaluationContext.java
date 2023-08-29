package com.zhuangpo.operate.log.core.handle;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.Map;

/**
 *  SPEL 上下文对象
 * 
 * @author xub
 * @since 2023/8/28 下午3:17
 */
public class OperateLogEvaluationContext extends MethodBasedEvaluationContext {

    public OperateLogEvaluationContext(Object rootObject, Method method, Object[] arguments,
                                       ParameterNameDiscoverer parameterNameDiscoverer,
                                       Object retObj,Map<String, String> optContext) {
        super(rootObject, method, arguments, parameterNameDiscoverer);
        setVariable("_retObj", retObj);
        setVariable("_context", optContext);
    }
}
