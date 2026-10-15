package com.ycyx.shop.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 管理员登录请求
 */
@Data
public class AdminLoginDTO {

    @NotBlank(message = "账号不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}