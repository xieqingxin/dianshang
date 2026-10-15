package com.ycyx.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体
 */
@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long categoryId;

    private String name;

    /** 产地省份：广东/海南 */
    private String originPlace;

    /** 产地详细：广东·湛江 */
    private String originDetail;

    /** 规格：如5斤装 */
    private String spec;

    /** 单价 */
    private BigDecimal price;

    /** 库存 */
    private Integer stock;

    /** 销量 */
    private Integer sales;

    /** 商品主图 */
    private String mainImage;

    /** 产地/口感描述 */
    private String description;

    /** 是否时令：1是 0否 */
    private Integer isSeasonal;

    /** 是否热点（需缓存）：1是 0否 */
    private Integer isHot;

    /** 上架状态：1上架 0下架 */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
}