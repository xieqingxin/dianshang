package com.ycyx.shop.vo;

import lombok.Data;

/**
 * 登录返回视图
 */
@Data
public class LoginVO {

    private String token;

    private Long userId;

    /** 昵称 */
    private String nickname;

    /** 账号（前台为手机号，后台为登录名） */
    private String account;

    private String avatar;

    /** member / admin */
    private String loginType;
}