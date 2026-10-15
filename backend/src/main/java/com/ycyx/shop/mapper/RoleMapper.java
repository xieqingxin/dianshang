package com.ycyx.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ycyx.shop.entity.Role;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色 Mapper
 */
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 查询管理员拥有的角色列表
     */
    @Select("SELECT r.* FROM role r INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.status = 1 AND r.deleted = 0")
    List<Role> selectRolesByUserId(@Param("userId") Long userId);
}