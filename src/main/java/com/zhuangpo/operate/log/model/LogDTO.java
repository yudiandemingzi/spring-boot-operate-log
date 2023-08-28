package com.zhuangpo.operate.log.model;

import lombok.Data;
import lombok.ToString;

/**
 *  日志实体类
 * 
 * @author xub
 * @since 2023/8/2 上午9:54
 */
@Data
@ToString
public class LogDTO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 操作方式(更新、删除、新增）
     */
    private String type;

    /**
     * 操作事件名称
     */
    private String operateName;

    /**
     * 操作内容
     */
    private String operateContent;
    
    /**
     * 操作时间
     */
    private String time;
}
