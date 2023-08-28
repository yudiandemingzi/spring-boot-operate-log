package com.zhuangpo.operate.log.model;

import lombok.Data;

/**
 *  用户实体类
 * 
 * @author xub
 * @since 2023/8/2 上午9:53
 */
public class UserDTO {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户姓名
     */
    private String userName;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
