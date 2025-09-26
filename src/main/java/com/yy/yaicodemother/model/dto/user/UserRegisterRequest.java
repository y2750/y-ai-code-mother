package com.yy.yaicodemother.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: UserRegisterRequest
 * Package: com.yy.yaicodemother.model.dto
 * Description:
 *
 * @Author
 * @Create 2025/9/23 13:38
 * @Version 1.0
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;
}

