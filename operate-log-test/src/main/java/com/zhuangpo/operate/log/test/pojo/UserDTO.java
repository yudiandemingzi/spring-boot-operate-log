package com.zhuangpo.operate.log.test.pojo;

import lombok.Data;
import lombok.ToString;

/**
 *  用户对象
 *
 * @author xub
 * @since 2023/8/2 上午9:54
 */
@Data
@ToString
public class UserDTO {

    /**
     * 主键
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;
}
