package com.ycyx.shop.service;

import com.ycyx.shop.common.util.SecurityUtils;
import com.ycyx.shop.entity.Menu;
import com.ycyx.shop.mapper.MenuMapper;
import com.ycyx.shop.vo.MenuTreeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单服务：查询当前管理员可见菜单树
 */
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuMapper menuMapper;

    public List<MenuTreeVO> getCurrentMenuTree() {
        List<Menu> menus = menuMapper.selectMenusByUserId(SecurityUtils.getUserId());
        return buildTree(menus);
    }

    /**
     * 将扁平菜单列表组装为树形结构
     */
    private List<MenuTreeVO> buildTree(List<Menu> menus) {
        Map<Long, List<Menu>> grouped = menus.stream()
                .collect(Collectors.groupingBy(Menu::getParentId));
        return buildChildren(0L, grouped);
    }

    private List<MenuTreeVO> buildChildren(Long parentId, Map<Long, List<Menu>> grouped) {
        List<Menu> children = grouped.get(parentId);
        if (children == null) {
            return new ArrayList<>();
        }
        List<MenuTreeVO> result = new ArrayList<>();
        for (Menu menu : children) {
            MenuTreeVO vo = new MenuTreeVO();
            vo.setId(menu.getId());
            vo.setParentId(menu.getParentId());
            vo.setMenuName(menu.getMenuName());
            vo.setMenuType(menu.getMenuType());
            vo.setPath(menu.getPath());
            vo.setComponent(menu.getComponent());
            vo.setIcon(menu.getIcon());
            vo.setOrderNum(menu.getOrderNum());
            vo.setChildren(buildChildren(menu.getId(), grouped));
            result.add(vo);
        }
        return result;
    }
}