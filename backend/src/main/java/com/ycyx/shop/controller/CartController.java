package com.ycyx.shop.controller;

import com.ycyx.shop.common.result.Result;
import com.ycyx.shop.dto.CartAddDTO;
import com.ycyx.shop.dto.CartUpdateDTO;
import com.ycyx.shop.service.CartService;
import com.ycyx.shop.vo.CartVO;
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

/**
 * 购物车接口（Redis 存储）
 */
@Tag(name = "购物车", description = "加入/修改/删除/清空购物车")
@RestController
@RequestMapping("/member/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "查询购物车")
    @GetMapping
    public Result<CartVO> list() {
        return Result.success(cartService.list());
    }

    @Operation(summary = "加入购物车")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody CartAddDTO dto) {
        cartService.add(dto);
        return Result.success();
    }

    @Operation(summary = "修改数量")
    @PutMapping
    public Result<Void> updateQuantity(@Validated @RequestBody CartUpdateDTO dto) {
        cartService.updateQuantity(dto);
        return Result.success();
    }

    @Operation(summary = "删除购物车商品")
    @DeleteMapping("/{productId}")
    public Result<Void> delete(@PathVariable("productId") Long productId) {
        cartService.delete(productId);
        return Result.success();
    }

    @Operation(summary = "清空购物车")
    @DeleteMapping
    public Result<Void> clear() {
        cartService.clear();
        return Result.success();
    }
}