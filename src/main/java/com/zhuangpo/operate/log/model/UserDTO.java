package com.zhuangpo.operate.log.model;

import lombok.Data;

/**
 *  用户实体类
 * 
 * @author xub
 * @since 2023/8/2 上午9:53
 */
@Data
public class UserDTO {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户姓名
     */
    private String userName;
}
