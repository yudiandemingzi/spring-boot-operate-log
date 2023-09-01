package com.zhuangpo.operate.log.core.handle;

import cn.hutool.core.util.StrUtil;
import com.zhuangpo.operate.log.core.custom.FunctionService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;

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
                //正则匹配方法
                Matcher matcher = PATTERN_METHOD.matcher(template);
                while (matcher.find()) {
                    //获取方法名称 例:getUserNameByUserId{#userId}
                    String funcName = matcher.group(1);
                    //正则匹配属性
                    Matcher patternAttribute = PATTERN_ATTRIBUTE.matcher(funcName);
                    String paramName = null;
                    if (patternAttribute.find()) {
                        //获取属性SPEL 例: #userId
                        paramName = patternAttribute.group(1);
                    }
                    //变成getUserNameByUserId
                    funcName = StrUtil.subBefore(funcName, "{", Boolean.FALSE);
                    if (customFunctionService.executeBefore(funcName)) {
                        //获取属性值
                        Object value = cachedExpressionEvaluator.parseExpression(paramName, elementKey, evaluationContext);
                        //获取方法返回值
                        String apply = customFunctionService.apply(funcName, value == null ? null : value.toString());
                        //替换后存入map
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
            try {
                AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(method, targetClass);
                value = cachedExpressionEvaluator.parseExpression(evaluationContext, annotatedElementKey, value);
                map.put(key, value);
            } catch (Exception e) {
                log.info("解析操作日志SpEL出错 ={}", e.getMessage());
            }
        }
        return map;
    }
}
