package com.zhuangpo.operate.log.core.util;

import lombok.Data;

/**
 *  通用返回对象
 *
 * @author xub
 * @date 2023/8/30 上午8:53
 */
@Data
public class Result<T> {
    private Integer code;
    private String message;
    private Boolean success;
    private T data;

    protected Result() {
    }

    protected Result(Integer code, Boolean success, String message, T data) {
        this.code = code;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     */
    public static <T> Result<T> success() {
        return new Result<T>(ResultCode.SUCCESS.getCode(), true, ResultCode.SUCCESS.getMessage(), null);
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>(ResultCode.SUCCESS.getCode(), true, ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功返回结果
     *
     * @param data    获取的数据
     * @param message 提示信息
     */
    public static <T> Result<T> success(T data, String message) {
        return new Result<T>(ResultCode.SUCCESS.getCode(), true, message, data);
    }

    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> Result<T> failed(String message) {
        return new Result<T>(ResultCode.FAILED.getCode(), false, message, null);
    }
}
