package com.zhuangpo.operate.log.core.custom;


/**
 *  自定义函数的默认实现，增加一层是为了屏蔽底层与上层直接接触
 * 
 * @author xub
 * @since 2023/8/30 下午3:58
 */
public class DefaultFunctionServiceImpl implements IFunctionService {

    private final CustomFunctionFactory customFunctionFactory;

    public DefaultFunctionServiceImpl(CustomFunctionFactory customFunctionFactory) {
        this.customFunctionFactory = customFunctionFactory;
    }

    @Override
    public String apply(String functionName, Object value) {
        CustomFunction function = customFunctionFactory.getFunction(functionName);
        if (function == null) {
            return value.toString();
        }
        return function.apply(value);
    }

    @Override
    public boolean executeBefore(String functionName) {
        CustomFunction function = customFunctionFactory.getFunction(functionName);
        return function != null && function.executeBefore();
    }
}
