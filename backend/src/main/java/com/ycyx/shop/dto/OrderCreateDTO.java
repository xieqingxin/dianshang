package com.ycyx.shop.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建订单请求
 */
@Data
public class OrderCreateDTO {

    @NotNull(message = "收货地址不能为空")
    private Long addressId;

    /** 幂等唯一ID（前端生成，防止重复下单） */
    @NotBlank(message = "请求唯一ID不能为空")
    private String requestId;

    @Valid
    @NotEmpty(message = "商品不能为空")
    private List<OrderItemDTO> items;
}