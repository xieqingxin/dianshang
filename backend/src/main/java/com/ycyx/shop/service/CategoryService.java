package com.ycyx.shop.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ycyx.shop.common.exception.BizException;
import com.ycyx.shop.common.result.ResultCode;
import com.ycyx.shop.dto.CategoryDTO;
import com.ycyx.shop.entity.Category;
import com.ycyx.shop.entity.Product;
import com.ycyx.shop.mapper.CategoryMapper;
import com.ycyx.shop.mapper.ProductMapper;
import com.ycyx.shop.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品分类服务
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    /**
     * 前台分类列表（仅启用）
     */
    public List<CategoryVO> listForFront() {
        List<Category> list = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getStatus, 1)
                        .orderByAsc(Category::getSort));
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 后台分类列表（全部）
     */
    public List<CategoryVO> listAll() {
        List<Category> list = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort));
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    public void add(CategoryDTO dto) {
        Category category = new Category();
        BeanUtil.copyProperties(dto, category);
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        categoryMapper.insert(category);
    }

    public void update(CategoryDTO dto) {
        Category category = categoryMapper.selectById(dto.getId());
        if (category == null) {
            throw new BizException(ResultCode.CATEGORY_NOT_EXIST);
        }
        BeanUtil.copyProperties(dto, category, "id");
        categoryMapper.updateById(category);
    }

    public void delete(Long id) {
        Long count = productMapper.selectCount(
                new LambdaQueryWrapper<Product>().eq(Product::getCategoryId, id));
        if (count != null && count > 0) {
            throw new BizException("该分类下存在商品，无法删除");
        }
        categoryMapper.deleteById(id);
    }

    private CategoryVO toVO(Category category) {
        CategoryVO vo = new CategoryVO();
        BeanUtil.copyProperties(category, vo);
        return vo;
    }
}