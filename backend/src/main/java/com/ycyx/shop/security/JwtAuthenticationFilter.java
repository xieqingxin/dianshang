package com.ycyx.shop.security;

import cn.hutool.core.util.StrUtil;
import com.ycyx.shop.common.util.JwtUtil;
import com.ycyx.shop.entity.Menu;
import com.ycyx.shop.entity.Role;
import com.ycyx.shop.entity.SysUser;
import com.ycyx.shop.mapper.MenuMapper;
import com.ycyx.shop.mapper.RoleMapper;
import com.ycyx.shop.mapper.SysUserMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JWT 认证过滤器：从请求头解析令牌，构建认证信息并注入 SecurityContext
 * 无状态认证，保证可分布式部署
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String LOGIN_TYPE_MEMBER = "member";
    public static final String LOGIN_TYPE_ADMIN = "admin";

    private final JwtUtil jwtUtil;
    private final SysUserMapper sysUserMapper;
    private final RoleMapper roleMapper;
    private final MenuMapper menuMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (StrUtil.isNotEmpty(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.validate(token)) {
                LoginUser loginUser = buildLoginUser(token);
                if (loginUser != null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 根据令牌构建登录用户（含权限）
     */
    private LoginUser buildLoginUser(String token) {
        Claims claims = jwtUtil.parseToken(token);
        Long userId = claims.get("userId", Long.class);
        String username = claims.get("username", String.class);
        String loginType = claims.get("loginType", String.class);
        if (userId == null || StrUtil.isEmpty(loginType)) {
            return null;
        }

        List<String> authorities = new ArrayList<>();
        if (LOGIN_TYPE_ADMIN.equals(loginType)) {
            SysUser sysUser = sysUserMapper.selectById(userId);
            if (sysUser == null || sysUser.getStatus() == null || sysUser.getStatus() != 1) {
                return null;
            }
            // 角色标识
            List<Role> roles = roleMapper.selectRolesByUserId(userId);
            for (Role role : roles) {
                authorities.add("ROLE_" + role.getRoleKey());
            }
            // 接口权限标识
            authorities.addAll(menuMapper.selectPermsByUserId(userId));
            return new LoginUser(userId, username, sysUser.getPassword(), loginType, sysUser.getStatus(), authorities);
        }
        // 前台会员
        authorities.add("ROLE_member");
        return new LoginUser(userId, username, "", loginType, 1, authorities);
    }
}