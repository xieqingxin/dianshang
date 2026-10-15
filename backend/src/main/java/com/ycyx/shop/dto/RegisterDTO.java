package com.ycyx.shop.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 注册请求
 */
@Data
public class RegisterDTO {

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "验证码不能为空")
    private String code;

    private String nickname;
}