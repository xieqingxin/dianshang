package com.ycyx.shop.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ycyx.shop.entity.SysUser;
import com.ycyx.shop.entity.SysUserRole;
import com.ycyx.shop.entity.User;
import com.ycyx.shop.mapper.SysUserMapper;
import com.ycyx.shop.mapper.SysUserRoleMapper;
import com.ycyx.shop.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 启动时初始化预置账号（密码 BCrypt 加密，无法静态写入 SQL）
 * 预置账号：管理员 admin/123456，普通用户 13800138000/123456
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Long SUPER_ADMIN_ROLE_ID = 1L;

    private final SysUserMapper sysUserMapper;
    private final UserMapper userMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initAdmin();
        initMember();
    }

    private void initAdmin() {
        Long count = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, "admin"));
        if (count != null && count == 0) {
            SysUser admin = new SysUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setNickname("超级管理员");
            admin.setStatus(1);
            sysUserMapper.insert(admin);

            SysUserRole relation = new SysUserRole();
            relation.setUserId(admin.getId());
            relation.setRoleId(SUPER_ADMIN_ROLE_ID);
            sysUserRoleMapper.insert(relation);
            log.info("已初始化后台管理员账号: admin / 123456");
        }
    }

    private void initMember() {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getPhone, "13800138000"));
        if (count != null && count == 0) {
            User member = new User();
            member.setPhone("13800138000");
            member.setPassword(passwordEncoder.encode("123456"));
            member.setNickname("测试用户");
            member.setStatus(1);
            userMapper.insert(member);
            log.info("已初始化前台测试账号: 13800138000 / 123456");
        }
    }
}