package com.ycyx.shop.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 分类新增/修改请求
 */
@Data
public class CategoryDTO {

    private Long id;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private String icon;

    private Integer sort;

    private Integer status;
}