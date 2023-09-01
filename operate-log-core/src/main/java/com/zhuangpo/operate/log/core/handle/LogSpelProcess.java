package com.zhuangpo.operate.log.core.handle;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.zhuangpo.operate.log.core.custom.FunctionService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对日志进行SPEL处理
 *
 * @author xub
 * @since 2023/9/1 上午9:22
 */
@Slf4j
@Service
public class LogSpelProcess {

    private static final Pattern PATTERN_ATTRIBUTE = Pattern.compile("\\{(.*?)}");

    private static final Pattern PATTERN_METHOD = Pattern.compile("\\[(.*?)]");

    private FunctionService customFunctionService;

    private final OperateLogExpressionEvaluator cachedExpressionEvaluator = new OperateLogExpressionEvaluator();

    public LogSpelProcess(FunctionService customFunctionService) {
        this.customFunctionService = customFunctionService;
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
    public HashMap<String, String> processBeforeExec(List<String> templates, ProceedingJoinPoint joinPoint) {
        // 获取切点方法上的注解
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();
        Object target = joinPoint.getTarget();
        Class<?> targetClass = AopUtils.getTargetClass(target);
        HashMap<String, String> map = new HashMap<>();
        AnnotatedElementKey elementKey = new AnnotatedElementKey(method, targetClass);
        EvaluationContext evaluationContext = cachedExpressionEvaluator.createEvaluationContext(targetClass, method, args);
        for (String template : templates) {
            map.put(template, template);
            //这里定义规则 如果存在[,就代表需要执行函数方法
            if (template.contains("[")) {
                Matcher matcher = PATTERN_METHOD.matcher(template);
                while (matcher.find()) {
                    // getUserNameByUserId{#userId}
                    String funcName = matcher.group(1);
                    Matcher patternAttribute = PATTERN_ATTRIBUTE.matcher(funcName);
                    String paramName = null;
                    if (patternAttribute.find()) {
                        paramName = patternAttribute.group(1);
                    }
                    funcName = StrUtil.subBefore(funcName, "{", Boolean.FALSE);
                    if (customFunctionService.executeBefore(funcName)) {
                        Object value = cachedExpressionEvaluator.parseExpression(paramName, elementKey, evaluationContext);
                        String apply = customFunctionService.apply(funcName, value == null ? null : value.toString());
                        map.put(getFunctionMapKey(funcName, paramName), apply);
                        String str = matcher.replaceAll(apply);
                        map.put(template, str);
                    }
                }
            }

            //一定要先处理完方法 在去处理属性
            String str = map.get(template);
            if (str.contains("{")) {
                Matcher matcher = PATTERN_ATTRIBUTE.matcher(str);
                StringBuffer parsedStr = new StringBuffer();
                //匹配到字符串中的 {*{*}}
                while (matcher.find()) {
                    String paramName = matcher.group(1);
                    Object value = cachedExpressionEvaluator.parseExpression(paramName, elementKey, evaluationContext);
                    String s = matcher.replaceAll(String.valueOf(value));
                    map.put(template, s);
                }
            }
        }

        return map;
    }


    /**
     * 这个解析是针对三目运算,如果没有三木运算，那就不需要走到这里
     *
     * @param map       需要解析的属性
     * @param joinPoint 切面
     * @return 已经解析过的map
     */
    public HashMap<String, String> ternaryProcess(HashMap<String, String> map,
                                                  ProceedingJoinPoint joinPoint
    ) {
        HashMap<String, String> spelValues = Maps.newHashMap();
        // 获取切点方法上的注解
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();
        Object target = joinPoint.getTarget();
        Class<?> targetClass = AopUtils.getTargetClass(target);
        EvaluationContext evaluationContext = cachedExpressionEvaluator.createEvaluationContext(targetClass, method, args);
        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            if (!key.contains("?") && !key.contains(":")) {
                continue;
            }
            String value = entry.getValue();
            spelValues.put(key, value);
            try {
                AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(method, targetClass);
                value = cachedExpressionEvaluator.parseExpression(evaluationContext, annotatedElementKey, value);
                spelValues.put(key, value);
            } catch (Exception e) {
                log.info("解析操作日志SpEL出错 ={}", e.getMessage());
            }
        }
        return spelValues;
    }


    /**
     * 获取前置函数映射的 key
     *
     * @param funcName
     * @param param
     * @return
     */
    private String getFunctionMapKey(String funcName, String param) {
        return funcName + param;
    }

}
