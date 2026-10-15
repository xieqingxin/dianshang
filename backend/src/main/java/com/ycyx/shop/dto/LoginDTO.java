package com.ycyx.shop.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 手机号密码登录请求
 */
@Data
public class LoginDTO {

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "密码不能为空")
    private String password;
}