package com.ycyx.shop.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ycyx.shop.common.constant.RedisKeyConstant;
import com.ycyx.shop.common.exception.BizException;
import com.ycyx.shop.common.result.PageResult;
import com.ycyx.shop.common.result.ResultCode;
import com.ycyx.shop.common.util.SecurityUtils;
import com.ycyx.shop.dto.OrderCreateDTO;
import com.ycyx.shop.dto.OrderItemDTO;
import com.ycyx.shop.entity.Address;
import com.ycyx.shop.entity.Order;
import com.ycyx.shop.entity.OrderItem;
import com.ycyx.shop.entity.Product;
import com.ycyx.shop.mapper.AddressMapper;
import com.ycyx.shop.mapper.OrderItemMapper;
import com.ycyx.shop.mapper.OrderMapper;
import com.ycyx.shop.mapper.ProductMapper;
import com.ycyx.shop.vo.OrderItemVO;
import com.ycyx.shop.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单服务：创建（幂等+事务+扣库存）、状态流转、查询
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final AddressMapper addressMapper;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 创建订单（事务 + Redis 幂等防重复提交 + 扣减库存防超卖）
     */
    @Transactional(rollbackFor = Exception.class)
    public Long create(OrderCreateDTO dto) {
        Long userId = SecurityUtils.getUserId();
        // 幂等校验：同一请求唯一ID只允许成功一次
        String idempotentKey = RedisKeyConstant.ORDER_IDEMPOTENT + dto.getRequestId();
        Boolean locked = stringRedisTemplate.opsForValue().setIfAbsent(idempotentKey, "1",
                Duration.ofSeconds(RedisKeyConstant.ORDER_IDEMPOTENT_EXPIRE));
        if (Boolean.FALSE.equals(locked)) {
            throw new BizException(ResultCode.ORDER_REPEAT);
        }

        try {
            return doCreate(dto, userId);
        } catch (Exception e) {
            // 失败释放幂等占位，允许用户重试
            stringRedisTemplate.delete(idempotentKey);
            throw e;
        }
    }

    private Long doCreate(OrderCreateDTO dto, Long userId) {
        Address address = addressMapper.selectById(dto.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BizException(ResultCode.ADDRESS_NOT_EXIST);
        }

        // 组装订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setStatus(Order.STATUS_UNPAID);
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(address.getProvince() + address.getCity()
                + address.getDistrict() + address.getDetailAddress());

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemDTO itemDTO : dto.getItems()) {
            Product product = productMapper.selectById(itemDTO.getProductId());
            if (product == null) {
                throw new BizException(ResultCode.PRODUCT_NOT_EXIST);
            }
            if (product.getStatus() == null || product.getStatus() != 1) {
                throw new BizException(ResultCode.PRODUCT_OFF_SHELF);
            }
            // 扣减库存（原子SQL校验库存，防止超卖）
            int rows = productMapper.deductStock(product.getId(), itemDTO.getQuantity());
            if (rows == 0) {
                throw new BizException(ResultCode.STOCK_NOT_ENOUGH);
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setSpec(product.getSpec());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
            orderItems.add(orderItem);
            totalAmount = totalAmount.add(orderItem.getTotalPrice());
        }

        order.setTotalAmount(totalAmount);
        orderMapper.insert(order);

        // 保存订单明细
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderId(order.getId());
            orderItemMapper.insert(orderItem);
        }
        return order.getId();
    }

    /**
     * 用户订单分页列表
     */
    public PageResult<OrderVO> pageList(Integer status, long page, long size) {
        Long userId = SecurityUtils.getUserId();
        Page<Order> p = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        Page<Order> result = orderMapper.selectPage(p, wrapper);
        return PageResult.of(result.getTotal(), toOrderVOList(result.getRecords()));
    }

    /**
     * 订单详情（校验归属）
     */
    public OrderVO detail(Long id) {
        Order order = getOwnedOrder(id);
        return toOrderVO(order);
    }

    /**
     * 管理端订单详情（不校验归属）
     */
    public OrderVO adminDetail(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BizException(ResultCode.ORDER_NOT_EXIST);
        }
        return toOrderVO(order);
    }

    /**
     * 取消订单（仅待支付，恢复库存）
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id) {
        Order order = getOwnedOrder(id);
        if (order.getStatus() != Order.STATUS_UNPAID) {
            throw new BizException(ResultCode.ORDER_STATUS_ERROR);
        }
        order.setStatus(Order.STATUS_CANCELLED);
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 恢复库存
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, id));
        for (OrderItem item : items) {
            productMapper.restoreStock(item.getProductId(), item.getQuantity());
        }
    }

    /**
     * 模拟支付回调：待支付 -> 已支付
     */
    public void pay(Long id) {
        Order order = getOwnedOrder(id);
        if (order.getStatus() != Order.STATUS_UNPAID) {
            throw new BizException(ResultCode.ORDER_STATUS_ERROR);
        }
        order.setStatus(Order.STATUS_PAID);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    /**
     * 管理端订单分页列表
     */
    public PageResult<OrderVO> adminPageList(Integer status, String orderNo, long page, long size) {
        Page<Order> p = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        if (StrUtil.isNotBlank(orderNo)) {
            wrapper.like(Order::getOrderNo, orderNo);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        Page<Order> result = orderMapper.selectPage(p, wrapper);
        return PageResult.of(result.getTotal(), toOrderVOList(result.getRecords()));
    }

    /**
     * 管理端发货：已支付 -> 已发货
     */
    public void ship(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BizException(ResultCode.ORDER_NOT_EXIST);
        }
        if (order.getStatus() != Order.STATUS_PAID) {
            throw new BizException(ResultCode.ORDER_STATUS_ERROR);
        }
        order.setStatus(Order.STATUS_SHIPPED);
        order.setShipTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    private Order getOwnedOrder(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BizException(ResultCode.ORDER_NOT_EXIST);
        }
        if (!order.getUserId().equals(SecurityUtils.getUserId())) {
            throw new BizException(ResultCode.FORBIDDEN);
        }
        return order;
    }

    private String generateOrderNo() {
        return DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmssSSS") + RandomUtil.randomNumbers(4);
    }

    private List<OrderVO> toOrderVOList(List<Order> orders) {
        if (orders.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());
        Map<Long, List<OrderItem>> orderItemsMap = orderItemMapper.selectList(
                        new LambdaQueryWrapper<OrderItem>().in(OrderItem::getOrderId, orderIds))
                .stream().collect(Collectors.groupingBy(OrderItem::getOrderId));
        return orders.stream().map(order -> toOrderVO(order, orderItemsMap)).collect(Collectors.toList());
    }

    private OrderVO toOrderVO(Order order) {
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
        return toOrderVO(order, items.stream().collect(Collectors.groupingBy(OrderItem::getOrderId)));
    }

    private OrderVO toOrderVO(Order order, Map<Long, List<OrderItem>> itemsMap) {
        OrderVO vo = new OrderVO();
        BeanUtil.copyProperties(order, vo);
        vo.setStatusDesc(statusDesc(order.getStatus()));
        List<OrderItem> items = itemsMap.getOrDefault(order.getId(), new ArrayList<>());
        vo.setItems(items.stream().map(item -> {
            OrderItemVO itemVO = new OrderItemVO();
            BeanUtil.copyProperties(item, itemVO);
            return itemVO;
        }).collect(Collectors.toList()));
        return vo;
    }

    public static String statusDesc(Integer status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case Order.STATUS_UNPAID:
                return "待支付";
            case Order.STATUS_PAID:
                return "已支付";
            case Order.STATUS_SHIPPED:
                return "已发货";
            case Order.STATUS_CANCELLED:
                return "已取消";
            default:
                return "";
        }
    }
}