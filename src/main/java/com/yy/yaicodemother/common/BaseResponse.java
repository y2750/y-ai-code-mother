package com.yy.yaicodemother.common;

import com.yy.yaicodemother.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

@Data

public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

/**
 * 构造一个带有状态码和数据的BaseResponse实例
 * @param code 响应状态码，表示请求的处理结果
 * @param data 响应数据，泛型类型，可以是任意类型的数据
 */
    public BaseResponse(int code, T data) {
        // 调用另一个构造函数，传入空字符串作为消息参数
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
