package com.ycyx.shop.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会员信息视图
 */
@Data
public class UserVO {

    private Long id;

    private String phone;

    private String nickname;

    private String avatar;

    private Integer status;

    private LocalDateTime createTime;
}