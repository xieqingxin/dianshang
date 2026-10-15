package com.ycyx.shop.controller;

import com.ycyx.shop.common.result.Result;
import com.ycyx.shop.dto.LoginDTO;
import com.ycyx.shop.dto.RegisterDTO;
import com.ycyx.shop.dto.SmsLoginDTO;
import com.ycyx.shop.service.MemberAuthService;
import com.ycyx.shop.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台会员认证接口
 */
@Tag(name = "前台认证", description = "前台会员注册/登录/验证码")
@RestController
@RequestMapping("/member/auth")
@RequiredArgsConstructor
public class MemberAuthController {

    private final MemberAuthService memberAuthService;

    @Operation(summary = "发送短信验证码（开发环境直接返回验证码）")
    @PostMapping("/sms-code")
    public Result<String> sendSmsCode(@RequestParam("phone") String phone) {
        return Result.success(memberAuthService.sendSmsCode(phone));
    }

    @Operation(summary = "注册")
    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody RegisterDTO dto) {
        memberAuthService.register(dto);
        return Result.success();
    }

    @Operation(summary = "手机号密码登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody LoginDTO dto) {
        return Result.success(memberAuthService.loginByPassword(dto));
    }

    @Operation(summary = "手机号验证码登录")
    @PostMapping("/login/sms")
    public Result<LoginVO> loginBySms(@Validated @RequestBody SmsLoginDTO dto) {
        return Result.success(memberAuthService.loginBySms(dto));
    }
}