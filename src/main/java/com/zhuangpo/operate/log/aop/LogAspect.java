package com.zhuangpo.operate.log.aop;


import com.zhuangpo.operate.log.annotation.OperateLog;
import com.zhuangpo.operate.log.model.LogDO;
import com.zhuangpo.operate.log.service.OperatorService;
import com.zhuangpo.operate.log.service.RecordLogService;
import com.zhuangpo.operate.log.util.EntityUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  日志切面
 * 
 * @author xub
 * @since 2023/8/4 上午11:24
 */
@Aspect
@Component
public class LogAspect {

    @Autowired
    private OperatorService operatorService; 
    
    @Autowired
    private RecordLogService recordLogService;

    /**
     * 定义切点
     */
    @Pointcut("@annotation(com.zhuangpo.operate.log.annotation.OperateLog)")
    public void pointcutLog() {
    }

    /**
     * 在原方法成功执行之后，再调用此通知。
     *
     * @param joinPoint
     */
    @AfterReturning("pointcutLog()")
    public void afterReturning(JoinPoint joinPoint) {
        insertLog(joinPoint);
    }


    /**
     * 进行插入日志
     *
     * @param joinPoint
     */
    private void insertLog(JoinPoint joinPoint) {
        // 获取切点方法上的注解
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        OperateLog annotation = method.getAnnotation(OperateLog.class);
        // 获取用户信息
        LogDO logDO = new LogDO();
        logDO.setType(annotation.type().getName());
        logDO.setOperateName(annotation.operateName());
        
        // 模拟获取当前用户
        String operatorName = operatorService.getOperatorName();
        logDO.setUserName(operatorName);
        
        // 获取当前时间
        LocalDateTime nowTime = LocalDateTime.now();
        String formatNowTime = nowTime.format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss"));
        logDO.setTime(formatNowTime);

        //获取方法参数值
        Object[] args = joinPoint.getArgs();

         Object parameter = getParameter(joinPoint);
        System.out.println("parameter = " + parameter);
        logDO.setOperateContent(annotation.operateContent());
        //保存日志
        recordLogService.insertLog(logDO);
    }



    /**
     * 根据方法和传入的参数获取请求参数
     *
     * 注意这里就需要在参数前面加对应的RequestBody 和 RequestParam 注解
     */
    private Object getParameter(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        Parameter[] parameters = method.getParameters();

        Object[] args = joinPoint.getArgs();
        List<Object> argList = new ArrayList<>(parameters.length);
        for (int i = 0; i < parameters.length; i++) {
            RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
            if (requestBody != null) {
                argList.add(args[i]);
            }
            RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
            if (requestParam != null) {
                Map<String, Object> map = new HashMap<>(1);
                String key = parameters[i].getName();
                if (!StringUtils.isEmpty(requestParam.value())) {
                    key = requestParam.value();
                }
                map.put(key, args[i]);
                argList.add(map);
            }
        }
        if (argList.size() == 0) {
            return null;
        } else if (argList.size() == 1) {
            return argList.get(0);
        } else {
            return argList;
        }
    }

    

}
