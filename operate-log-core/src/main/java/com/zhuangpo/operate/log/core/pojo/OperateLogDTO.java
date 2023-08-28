package com.zhuangpo.operate.log.core.pojo;

import com.zhuangpo.operate.log.core.enums.OperateEnum;
import lombok.Data;
import lombok.ToString;

/**
 * OperateLog注解 对应 实体
 *
 * @author xub
 * @since 2023/8/28 下午2:42
 */
@ToString
@Data
public class OperateLogDTO {

    /**
     * 操作者
     */
    private String operator;

    /**
     * 操作方式
     */
    private OperateEnum type;

    /**
     * 关联的业务id(订单号、业务编号)
     */
    private String bizNo;

    /**
     * 操作事件名称(接口名称)
     */
    private String operateName;

    /**
     * 比较详细的一条操作日志 比如某某订单已经发货
     */
    private String operateContent;

    /**
     * 记录条件 默认 true
     */
    private String condition;
}
