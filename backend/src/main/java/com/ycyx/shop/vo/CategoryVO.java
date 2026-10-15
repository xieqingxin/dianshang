package com.ycyx.shop.vo;

import lombok.Data;

/**
 * 分类视图
 */
@Data
public class CategoryVO {

    private Long id;

    private String name;

    private String icon;

    private Integer sort;

    private Integer status;
}