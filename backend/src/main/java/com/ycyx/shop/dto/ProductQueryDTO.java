package com.ycyx.shop.dto;

import lombok.Data;

/**
 * 商品列表查询参数
 */
@Data
public class ProductQueryDTO {

    private long page = 1;

    private long size = 10;

    private Long categoryId;

    private String originPlace;

    private String keyword;
}