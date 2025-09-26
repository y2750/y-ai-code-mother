package com.yy.yaicodemother.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求实体类
 * 实现了Serializable接口，支持序列化操作
 * 使用@Data注解自动生成getter、setter等方法
 */
@Data
public class UserLoginRequest implements Serializable {

    /**
     * 序列化版本UID，用于在反序列化时验证版本一致性
     */
    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;
}
