package com.ycyx.shop.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 商品新增/修改请求
 */
@Data
public class ProductSaveDTO {

    private Long id;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @NotBlank(message = "商品名称不能为空")
    private String name;

    @NotBlank(message = "产地不能为空")
    private String originPlace;

    private String originDetail;

    private String spec;

    @NotNull(message = "单价不能为空")
    private BigDecimal price;

    @NotNull(message = "库存不能为空")
    private Integer stock;

    private String mainImage;

    private String description;

    private Integer isSeasonal;

    private Integer isHot;

    private Integer status;
}