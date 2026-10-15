package com.ycyx.shop.dto;

import lombok.Data;

/**
 * 个人资料修改请求
 */
@Data
public class UserProfileDTO {

    private String nickname;

    private String avatar;
}