package com.zhuangpo.operate.log.core.pojo;

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
     * 是否成功 true:是，flase:否
     */
    private Boolean status;

    /**
     * 错误原因
     */
    private String errMsg;
}
