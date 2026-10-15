package com.ycyx.shop.common.config;

import com.ycyx.shop.security.JwtAuthenticationFilter;
import com.ycyx.shop.security.RestAccessDeniedHandler;
import com.ycyx.shop.security.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置：JWT 无状态认证 + RBAC 权限控制
 */
@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /** 匿名可访问的接口 */
    private static final String[] WHITE_LIST = {
            "/member/auth/**",
            "/admin/auth/login",
            "/category/list",
            "/doc.html", "/webjars/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**",
            "/favicon.ico", "/error"
    };

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors().and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests(auth -> auth
                // 匿名白名单
                .requestMatchers(WHITE_LIST).permitAll()
                // 前台商品浏览、上传图片访问
                .requestMatchers(HttpMethod.GET, "/product/**").permitAll()
                .requestMatchers("/upload/**").permitAll()
                // 后台接口需管理员角色
                .requestMatchers("/admin/**").hasAnyAuthority("ROLE_super_admin", "ROLE_admin")
                // 其余需认证
                .anyRequest().authenticated()
            )
            .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .accessDeniedHandler(restAccessDeniedHandler)
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}