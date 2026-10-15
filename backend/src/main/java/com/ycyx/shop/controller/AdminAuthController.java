package com.ycyx.shop.controller;

import com.ycyx.shop.common.result.Result;
import com.ycyx.shop.dto.AdminLoginDTO;
import com.ycyx.shop.service.AdminAuthService;
import com.ycyx.shop.service.MenuService;
import com.ycyx.shop.vo.LoginVO;
import com.ycyx.shop.vo.MenuTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 后台管理员认证接口
 */
@Tag(name = "后台认证", description = "管理员登录/菜单权限")
@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;
    private final MenuService menuService;

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody AdminLoginDTO dto) {
        return Result.success(adminAuthService.login(dto));
    }

    @Operation(summary = "获取当前管理员的菜单树")
    @GetMapping("/menus")
    public Result<List<MenuTreeVO>> menus() {
        return Result.success(menuService.getCurrentMenuTree());
    }
}