package com.zhuangpo.operate.log.core.enums;

/**
 *  注解函数枚举
 * 
 * @author xub
 * @since 2023/9/1 上午9:58
 */
public enum FunctionNameEnum {

    GET_USERNAME_BY_USERID("getUserNameByUserId"),
    DEFAULT_NAME("defaultName"),
            ;
    private final String name;

    FunctionNameEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
