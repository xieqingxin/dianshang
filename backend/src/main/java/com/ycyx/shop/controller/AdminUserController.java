package com.ycyx.shop.controller;

import com.ycyx.shop.common.result.PageResult;
import com.ycyx.shop.common.result.Result;
import com.ycyx.shop.service.UserService;
import com.ycyx.shop.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台用户管理接口（管理前台会员）
 */
@Tag(name = "后台用户管理", description = "会员用户列表与状态管理")
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @Operation(summary = "用户分页列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('user:list')")
    public Result<PageResult<UserVO>> page(@RequestParam(defaultValue = "1") long page,
                                           @RequestParam(defaultValue = "10") long size,
                                           @RequestParam(required = false) String keyword) {
        return Result.success(userService.pageUsers(page, size, keyword));
    }

    @Operation(summary = "禁用/启用用户")
    @PutMapping("/{id}/status/{status}")
    @PreAuthorize("hasAuthority('user:status')")
    public Result<Void> updateStatus(@PathVariable("id") Long id, @PathVariable("status") Integer status) {
        userService.updateUserStatus(id, status);
        return Result.success();
    }
}