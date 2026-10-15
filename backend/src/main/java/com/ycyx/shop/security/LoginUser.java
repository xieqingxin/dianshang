package com.ycyx.shop.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录用户信息（实现 UserDetails，供 SecurityContext 使用）
 * 前台会员与后台管理员共用，通过 loginType 区分
 */
@Getter
public class LoginUser implements UserDetails {

    private final Long userId;
    private final String username;
    private final String password;
    private final String loginType;
    private final Integer status;
    private final List<String> permissions;

    public LoginUser(Long userId, String username, String password, String loginType,
                     Integer status, List<String> permissions) {
        this.userId = userId;
        this.username = username;
        this.password = password == null ? "" : password;
        this.loginType = loginType;
        this.status = status == null ? 1 : status;
        this.permissions = permissions;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == 1;
    }
}