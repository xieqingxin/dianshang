package com.ycyx.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ycyx.shop.entity.Menu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单权限 Mapper
 */
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 查询管理员可见菜单列表（目录 + 菜单，用于构建侧边栏）
     */
    @Select("SELECT DISTINCT m.* FROM menu m " +
            "INNER JOIN role_menu rm ON m.id = rm.menu_id " +
            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.menu_type IN ('M','C') AND m.status = 1 AND m.deleted = 0 " +
            "ORDER BY m.order_num")
    List<Menu> selectMenusByUserId(@Param("userId") Long userId);

    /**
     * 查询管理员全部权限标识（用于接口级鉴权）
     */
    @Select("SELECT DISTINCT m.perms FROM menu m " +
            "INNER JOIN role_menu rm ON m.id = rm.menu_id " +
            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.perms <> '' AND m.status = 1 AND m.deleted = 0")
    List<String> selectPermsByUserId(@Param("userId") Long userId);
}