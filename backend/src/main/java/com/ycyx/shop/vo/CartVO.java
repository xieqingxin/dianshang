package com.ycyx.shop.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车视图
 */
@Data
public class CartVO {

    private List<CartItemVO> items;

    /** 有效商品总件数 */
    private Integer totalCount;

    /** 有效商品总金额 */
    private BigDecimal totalAmount;
}