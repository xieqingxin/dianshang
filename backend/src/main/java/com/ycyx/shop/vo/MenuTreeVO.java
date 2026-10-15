package com.ycyx.shop.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单树视图
 */
@Data
public class MenuTreeVO {

    private Long id;

    private Long parentId;

    private String menuName;

    private String menuType;

    private String path;

    private String component;

    private String icon;

    private Integer orderNum;

    private List<MenuTreeVO> children = new ArrayList<>();
}