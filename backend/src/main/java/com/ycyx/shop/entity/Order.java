package com.ycyx.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@TableName("orders")
public class Order {

    /** 待支付 */
    public static final int STATUS_UNPAID = 0;
    /** 已支付 */
    public static final int STATUS_PAID = 1;
    /** 已发货 */
    public static final int STATUS_SHIPPED = 2;
    /** 已取消 */
    public static final int STATUS_CANCELLED = 3;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long userId;

    private BigDecimal totalAmount;

    private Integer status;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private LocalDateTime payTime;

    private LocalDateTime shipTime;

    private LocalDateTime cancelTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
}