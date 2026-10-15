package com.ycyx.shop.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车项视图
 */
@Data
public class CartItemVO {

    private Long productId;

    private String productName;

    private String productImage;

    private String spec;

    private BigDecimal price;

    private Integer quantity;

    private Integer stock;

    private String originDetail;

    /** 状态: 1正常 2库存不足 3已下架/失效 */
    private Integer status;

    /** 小计金额 */
    private BigDecimal totalPrice;
}