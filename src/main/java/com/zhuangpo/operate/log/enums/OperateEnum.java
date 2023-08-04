package com.zhuangpo.operate.log.enums;

/**
 *  操作类型
 * 
 * @author xub
 * @since 2023/8/4 上午10:00
 */
public enum OperateEnum {

    UNKNOWN("未知"),
    INSERT("新增"),
    UPDATE("更新"),
    DELETE("删除"),
    SELECT("查询"),
    ;

    private final String name;

    OperateEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
