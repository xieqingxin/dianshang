package com.ycyx.shop.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单明细视图
 */
@Data
public class OrderItemVO {

    private Long productId;

    private String productName;

    private String productImage;

    private String spec;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal totalPrice;
}