package com.ycyx.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ycyx.shop.common.exception.BizException;
import com.ycyx.shop.common.result.ResultCode;
import com.ycyx.shop.common.util.JwtUtil;
import com.ycyx.shop.dto.AdminLoginDTO;
import com.ycyx.shop.entity.Role;
import com.ycyx.shop.entity.SysUser;
import com.ycyx.shop.mapper.RoleMapper;
import com.ycyx.shop.mapper.SysUserMapper;
import com.ycyx.shop.security.JwtAuthenticationFilter;
import com.ycyx.shop.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 后台管理员认证服务
 */
@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final SysUserMapper sysUserMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 管理员账号密码登录
     */
    public LoginVO login(AdminLoginDTO dto) {
        SysUser sysUser = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername()));
        if (sysUser == null) {
            throw new BizException(ResultCode.USER_NOT_EXIST);
        }
        if (!passwordEncoder.matches(dto.getPassword(), sysUser.getPassword())) {
            throw new BizException(ResultCode.USER_PASSWORD_ERROR);
        }
        if (sysUser.getStatus() == null || sysUser.getStatus() != 1) {
            throw new BizException(ResultCode.USER_DISABLED);
        }

        // 主角色标识
        List<Role> roles = roleMapper.selectRolesByUserId(sysUser.getId());
        String roleKey = roles.isEmpty() ? "" : roles.get(0).getRoleKey();

        String token = jwtUtil.generateToken(sysUser.getId(), sysUser.getUsername(),
                JwtAuthenticationFilter.LOGIN_TYPE_ADMIN, roleKey);

        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(sysUser.getId());
        vo.setNickname(sysUser.getNickname());
        vo.setAccount(sysUser.getUsername());
        vo.setAvatar(sysUser.getAvatar());
        vo.setLoginType(JwtAuthenticationFilter.LOGIN_TYPE_ADMIN);
        return vo;
    }
}