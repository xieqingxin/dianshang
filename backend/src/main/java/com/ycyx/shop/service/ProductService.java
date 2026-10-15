package com.ycyx.shop.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ycyx.shop.common.constant.RedisKeyConstant;
import com.ycyx.shop.common.exception.BizException;
import com.ycyx.shop.common.result.PageResult;
import com.ycyx.shop.common.result.ResultCode;
import com.ycyx.shop.dto.ProductQueryDTO;
import com.ycyx.shop.dto.ProductSaveDTO;
import com.ycyx.shop.entity.Category;
import com.ycyx.shop.entity.Product;
import com.ycyx.shop.mapper.CategoryMapper;
import com.ycyx.shop.mapper.ProductMapper;
import com.ycyx.shop.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 商品服务：前台浏览 + 后台管理 + 热点缓存
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 前台商品分页列表（仅上架商品）
     */
    public PageResult<ProductVO> pageList(ProductQueryDTO query) {
        return doPage(query, true);
    }

    /**
     * 后台商品分页列表（全部状态）
     */
    public PageResult<ProductVO> adminPageList(ProductQueryDTO query) {
        return doPage(query, false);
    }

    private PageResult<ProductVO> doPage(ProductQueryDTO query, boolean onlyOnSale) {
        Page<Product> p = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (onlyOnSale) {
            wrapper.eq(Product::getStatus, 1);
        }
        if (query.getCategoryId() != null) {
            wrapper.eq(Product::getCategoryId, query.getCategoryId());
        }
        if (StrUtil.isNotBlank(query.getOriginPlace())) {
            wrapper.eq(Product::getOriginPlace, query.getOriginPlace());
        }
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.like(Product::getName, query.getKeyword());
        }
        wrapper.orderByDesc(Product::getIsHot).orderByDesc(Product::getCreateTime);

        Page<Product> result = productMapper.selectPage(p, wrapper);
        Map<Long, String> categoryNames = categoryMapper.selectList(null).stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));
        List<ProductVO> vos = result.getRecords().stream()
                .map(prod -> toVO(prod, categoryNames.get(prod.getCategoryId())))
                .collect(Collectors.toList());
        return PageResult.of(result.getTotal(), vos);
    }

    /**
     * 商品详情（热点商品走 Redis 缓存，防止热点穿透数据库）
     */
    public ProductVO getDetail(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BizException(ResultCode.PRODUCT_NOT_EXIST);
        }
        // 热点商品：先查缓存
        if (product.getIsHot() != null && product.getIsHot() == 1) {
            String key = RedisKeyConstant.HOT_PRODUCT + id;
            String json = stringRedisTemplate.opsForValue().get(key);
            if (StrUtil.isNotBlank(json)) {
                return JSONUtil.toBean(json, ProductVO.class);
            }
            ProductVO vo = toVO(product, getCategoryName(product.getCategoryId()));
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(vo),
                    Duration.ofMinutes(RedisKeyConstant.HOT_PRODUCT_EXPIRE));
            return vo;
        }
        return toVO(product, getCategoryName(product.getCategoryId()));
    }

    public void add(ProductSaveDTO dto) {
        Product product = new Product();
        BeanUtil.copyProperties(dto, product);
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        if (product.getSales() == null) {
            product.setSales(0);
        }
        productMapper.insert(product);
    }

    public void update(ProductSaveDTO dto) {
        Product product = productMapper.selectById(dto.getId());
        if (product == null) {
            throw new BizException(ResultCode.PRODUCT_NOT_EXIST);
        }
        BeanUtil.copyProperties(dto, product, "id");
        productMapper.updateById(product);
        // 更新商品时删除缓存，保证最终一致性
        deleteHotCache(dto.getId());
    }

    public void delete(Long id) {
        productMapper.deleteById(id);
        deleteHotCache(id);
    }

    /**
     * 商品上架/下架
     */
    public void onOff(Long id, Integer status) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BizException(ResultCode.PRODUCT_NOT_EXIST);
        }
        product.setStatus(status);
        productMapper.updateById(product);
        deleteHotCache(id);
    }

    /**
     * 修改库存
     */
    public void updateStock(Long id, Integer stock) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BizException(ResultCode.PRODUCT_NOT_EXIST);
        }
        product.setStock(stock);
        productMapper.updateById(product);
        deleteHotCache(id);
    }

    private void deleteHotCache(Long id) {
        stringRedisTemplate.delete(RedisKeyConstant.HOT_PRODUCT + id);
    }

    private String getCategoryName(Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        return category == null ? "" : category.getName();
    }

    private ProductVO toVO(Product product, String categoryName) {
        ProductVO vo = new ProductVO();
        BeanUtil.copyProperties(product, vo);
        vo.setCategoryName(categoryName);
        return vo;
    }
}