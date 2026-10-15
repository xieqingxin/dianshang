package com.ycyx.shop.controller;

import com.ycyx.shop.common.result.PageResult;
import com.ycyx.shop.common.result.Result;
import com.ycyx.shop.dto.ProductQueryDTO;
import com.ycyx.shop.service.ProductService;
import com.ycyx.shop.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台商品接口
 */
@Tag(name = "前台商品", description = "商品浏览")
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "商品分页列表")
    @GetMapping("/list")
    public Result<PageResult<ProductVO>> list(ProductQueryDTO query) {
        return Result.success(productService.pageList(query));
    }

    @Operation(summary = "商品详情")
    @GetMapping("/{id}")
    public Result<ProductVO> detail(@PathVariable("id") Long id) {
        return Result.success(productService.getDetail(id));
    }
}