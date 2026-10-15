package com.ycyx.shop.controller;

import com.ycyx.shop.common.result.Result;
import com.ycyx.shop.dto.AddressDTO;
import com.ycyx.shop.dto.UserProfileDTO;
import com.ycyx.shop.service.AddressService;
import com.ycyx.shop.service.UserService;
import com.ycyx.shop.vo.AddressVO;
import com.ycyx.shop.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台会员接口：个人信息 + 收货地址
 */
@Tag(name = "会员中心", description = "个人信息与收货地址管理")
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final UserService userService;
    private final AddressService addressService;

    @Operation(summary = "查询个人信息")
    @GetMapping("/profile")
    public Result<UserVO> profile() {
        return Result.success(userService.getCurrentUser());
    }

    @Operation(summary = "修改个人信息")
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody UserProfileDTO dto) {
        userService.updateProfile(dto);
        return Result.success();
    }

    @Operation(summary = "地址列表")
    @GetMapping("/address")
    public Result<List<AddressVO>> addressList() {
        return Result.success(addressService.list());
    }

    @Operation(summary = "新增地址")
    @PostMapping("/address")
    public Result<Void> addAddress(@Validated @RequestBody AddressDTO dto) {
        addressService.add(dto);
        return Result.success();
    }

    @Operation(summary = "修改地址")
    @PutMapping("/address")
    public Result<Void> updateAddress(@Validated @RequestBody AddressDTO dto) {
        addressService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除地址")
    @DeleteMapping("/address/{id}")
    public Result<Void> deleteAddress(@PathVariable("id") Long id) {
        addressService.delete(id);
        return Result.success();
    }

    @Operation(summary = "设置默认地址")
    @PutMapping("/address/{id}/default")
    public Result<Void> setDefault(@PathVariable("id") Long id) {
        addressService.setDefault(id);
        return Result.success();
    }
}