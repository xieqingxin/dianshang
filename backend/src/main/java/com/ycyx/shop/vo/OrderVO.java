package com.ycyx.shop.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单视图
 */
@Data
public class OrderVO {

    private Long id;

    private String orderNo;

    private Long userId;

    private BigDecimal totalAmount;

    private Integer status;

    private String statusDesc;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private LocalDateTime payTime;

    private LocalDateTime shipTime;

    private LocalDateTime cancelTime;

    private LocalDateTime createTime;

    private List<OrderItemVO> items;
}