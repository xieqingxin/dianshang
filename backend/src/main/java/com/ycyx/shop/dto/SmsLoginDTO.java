package com.ycyx.shop.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 手机号验证码登录请求
 */
@Data
public class SmsLoginDTO {

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "验证码不能为空")
    private String code;
}