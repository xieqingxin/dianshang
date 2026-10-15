package com.ycyx.shop.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品视图
 */
@Data
public class ProductVO {

    private Long id;

    private Long categoryId;

    private String categoryName;

    private String name;

    private String originPlace;

    private String originDetail;

    private String spec;

    private BigDecimal price;

    private Integer stock;

    private Integer sales;

    private String mainImage;

    private String description;

    private Integer isSeasonal;

    private Integer isHot;

    private Integer status;
}