package com.ycyx.shop.controller;

import com.ycyx.shop.common.result.PageResult;
import com.ycyx.shop.common.result.Result;
import com.ycyx.shop.dto.ProductQueryDTO;
import com.ycyx.shop.dto.ProductSaveDTO;
import com.ycyx.shop.service.ProductService;
import com.ycyx.shop.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台商品管理接口
 */
@Tag(name = "后台商品管理", description = "商品增删改查/上下架/库存")
@RestController
@RequestMapping("/admin/product")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @Operation(summary = "商品分页列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('product:list')")
    public Result<PageResult<ProductVO>> page(ProductQueryDTO query) {
        return Result.success(productService.adminPageList(query));
    }

    @Operation(summary = "新增商品")
    @PostMapping
    @PreAuthorize("hasAuthority('product:add')")
    public Result<Void> add(@Validated @RequestBody ProductSaveDTO dto) {
        productService.add(dto);
        return Result.success();
    }

    @Operation(summary = "修改商品")
    @PutMapping
    @PreAuthorize("hasAuthority('product:edit')")
    public Result<Void> update(@Validated @RequestBody ProductSaveDTO dto) {
        productService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('product:delete')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        productService.delete(id);
        return Result.success();
    }

    @Operation(summary = "商品上架/下架")
    @PutMapping("/{id}/status/{status}")
    @PreAuthorize("hasAuthority('product:onoff')")
    public Result<Void> onOff(@PathVariable("id") Long id, @PathVariable("status") Integer status) {
        productService.onOff(id, status);
        return Result.success();
    }

    @Operation(summary = "修改库存")
    @PutMapping("/{id}/stock")
    @PreAuthorize("hasAuthority('product:edit')")
    public Result<Void> updateStock(@PathVariable("id") Long id, @RequestParam("stock") Integer stock) {
        productService.updateStock(id, stock);
        return Result.success();
    }
}