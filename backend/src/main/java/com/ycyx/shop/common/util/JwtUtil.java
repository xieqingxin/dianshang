package com.ycyx.shop.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JWT 令牌工具类：生成与解析无状态令牌
 */
@Component
public class JwtUtil {

    @Value("${ycyx.jwt.secret}")
    private String secret;

    @Value("${ycyx.jwt.expire}")
    private Long expire;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成令牌
     *
     * @param userId    用户ID
     * @param username  用户名/手机号
     * @param loginType 登录类型：member-前台用户, admin-管理员
     * @param role      角色标识
     */
    public String generateToken(Long userId, String username, String loginType, String role) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("userId", userId)
                .claim("username", username)
                .claim("loginType", loginType)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expire))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析令牌，失败抛出 JwtException
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
    }

    /**
     * 校验令牌是否有效，无效返回 true 兼容过滤逻辑（后续在具体处判断）
     */
    public boolean validate(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long getUserId(String token) {
        return parseToken(token).get("userId", Long.class);
    }

    public String getUsername(String token) {
        return parseToken(token).get("username", String.class);
    }
}