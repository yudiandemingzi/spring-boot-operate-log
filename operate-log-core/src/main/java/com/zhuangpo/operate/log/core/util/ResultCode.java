package com.zhuangpo.operate.log.core.util;

import lombok.Getter;

/**
 * 枚举了一些常用API操作码
 *
 * @author xub
 * @date 2023/8/30 上午8:46
 */
@Getter
public enum ResultCode {

    SUCCESS(0, "操作成功"),

    FAILED(500, "内部服务器错误"),

    NOT_FOUND(404,  "请求资源不存在"),

    FORBIDDEN(403,  "没有相关权限"),
    ;

    private final Integer code;
    private final String message;

    ResultCode(Integer code,  String message) {
        this.code = code;
        this.message = message;
    }
}
