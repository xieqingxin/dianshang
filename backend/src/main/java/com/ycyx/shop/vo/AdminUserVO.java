package com.ycyx.shop.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员信息视图
 */
@Data
public class AdminUserVO {

    private Long id;

    private String username;

    private String nickname;

    private String avatar;

    private Integer status;

    private LocalDateTime createTime;
}