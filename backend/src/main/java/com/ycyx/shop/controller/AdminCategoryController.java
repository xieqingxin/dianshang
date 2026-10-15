package com.ycyx.shop.controller;

import com.ycyx.shop.common.result.Result;
import com.ycyx.shop.dto.CategoryDTO;
import com.ycyx.shop.service.CategoryService;
import com.ycyx.shop.vo.CategoryVO;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 后台分类管理接口
 */
@Tag(name = "后台分类管理", description = "水果分类增删改查")
@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "分类列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('category:list')")
    public Result<List<CategoryVO>> list() {
        return Result.success(categoryService.listAll());
    }

    @Operation(summary = "新增分类")
    @PostMapping
    @PreAuthorize("hasAuthority('category:add')")
    public Result<Void> add(@Validated @RequestBody CategoryDTO dto) {
        categoryService.add(dto);
        return Result.success();
    }

    @Operation(summary = "修改分类")
    @PutMapping
    @PreAuthorize("hasAuthority('category:edit')")
    public Result<Void> update(@Validated @RequestBody CategoryDTO dto) {
        categoryService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('category:delete')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        categoryService.delete(id);
        return Result.success();
    }
}