package com.ycyx.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ycyx.shop.common.constant.RedisKeyConstant;
import com.ycyx.shop.common.exception.BizException;
import com.ycyx.shop.common.result.ResultCode;
import com.ycyx.shop.common.util.SecurityUtils;
import com.ycyx.shop.dto.CartAddDTO;
import com.ycyx.shop.dto.CartUpdateDTO;
import com.ycyx.shop.entity.Product;
import com.ycyx.shop.mapper.ProductMapper;
import com.ycyx.shop.vo.CartItemVO;
import com.ycyx.shop.vo.CartVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 购物车服务：数据全量存储于 Redis（Hash 结构，field=商品ID, value=数量）
 * 高性能读写，减轻数据库压力
 */
@Service
@RequiredArgsConstructor
public class CartService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ProductMapper productMapper;

    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_STOCK_NOT_ENOUGH = 2;
    public static final int STATUS_INVALID = 3;

    /**
     * 加入购物车
     */
    public void add(CartAddDTO dto) {
        Long userId = SecurityUtils.getUserId();
        Product product = checkProduct(dto.getProductId());
        String key = cartKey(userId);
        String field = String.valueOf(dto.getProductId());

        Object prevObj = stringRedisTemplate.opsForHash().get(key, field);
        int prev = prevObj == null ? 0 : Integer.parseInt(prevObj.toString());
        int newQuantity = prev + dto.getQuantity();
        if (newQuantity > product.getStock()) {
            throw new BizException(ResultCode.STOCK_NOT_ENOUGH);
        }
        stringRedisTemplate.opsForHash().put(key, field, String.valueOf(newQuantity));
    }

    /**
     * 修改数量
     */
    public void updateQuantity(CartUpdateDTO dto) {
        Long userId = SecurityUtils.getUserId();
        Product product = checkProduct(dto.getProductId());
        if (dto.getQuantity() > product.getStock()) {
            throw new BizException(ResultCode.STOCK_NOT_ENOUGH);
        }
        stringRedisTemplate.opsForHash().put(cartKey(userId), String.valueOf(dto.getProductId()),
                String.valueOf(dto.getQuantity()));
    }

    /**
     * 删除购物车商品
     */
    public void delete(Long productId) {
        stringRedisTemplate.opsForHash().delete(cartKey(SecurityUtils.getUserId()), String.valueOf(productId));
    }

    /**
     * 清空购物车
     */
    public void clear() {
        stringRedisTemplate.delete(cartKey(SecurityUtils.getUserId()));
    }

    /**
     * 查询购物车（实时同步商品状态：下架/库存不足自动标记）
     */
    public CartVO list() {
        String key = cartKey(SecurityUtils.getUserId());
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);

        List<CartItemVO> items = new ArrayList<>();
        int totalCount = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        if (!entries.isEmpty()) {
            List<Long> productIds = entries.keySet().stream()
                    .map(k -> Long.parseLong(k.toString())).collect(Collectors.toList());
            Map<Long, Product> products = productMapper.selectBatchIds(productIds).stream()
                    .collect(Collectors.toMap(Product::getId, Function.identity()));

            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                Long productId = Long.parseLong(entry.getKey().toString());
                int quantity = Integer.parseInt(entry.getValue().toString());
                Product product = products.get(productId);

                CartItemVO vo = new CartItemVO();
                vo.setProductId(productId);
                vo.setQuantity(quantity);

                if (product == null || product.getStatus() == null || product.getStatus() != 1) {
                    vo.setStatus(STATUS_INVALID);
                } else {
                    vo.setProductName(product.getName());
                    vo.setProductImage(product.getMainImage());
                    vo.setSpec(product.getSpec());
                    vo.setPrice(product.getPrice());
                    vo.setStock(product.getStock());
                    vo.setOriginDetail(product.getOriginDetail());
                    if (quantity > product.getStock()) {
                        vo.setStatus(STATUS_STOCK_NOT_ENOUGH);
                    } else {
                        vo.setStatus(STATUS_NORMAL);
                        vo.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
                        totalCount += quantity;
                        totalAmount = totalAmount.add(vo.getTotalPrice());
                    }
                }
                items.add(vo);
            }
        }

        CartVO cartVO = new CartVO();
        cartVO.setItems(items);
        cartVO.setTotalCount(totalCount);
        cartVO.setTotalAmount(totalAmount);
        return cartVO;
    }

    private Product checkProduct(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BizException(ResultCode.PRODUCT_NOT_EXIST);
        }
        if (product.getStatus() == null || product.getStatus() != 1) {
            throw new BizException(ResultCode.PRODUCT_OFF_SHELF);
        }
        return product;
    }

    private String cartKey(Long userId) {
        return RedisKeyConstant.CART + userId;
    }
}