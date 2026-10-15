package com.ycyx.shop.controller;

import com.ycyx.shop.common.annotation.OperateLog;
import com.ycyx.shop.common.result.PageResult;
import com.ycyx.shop.common.result.Result;
import com.ycyx.shop.dto.OrderCreateDTO;
import com.ycyx.shop.service.OrderService;
import com.ycyx.shop.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台订单接口
 */
@Tag(name = "前台订单", description = "下单/查询/取消/模拟支付")
@RestController
@RequestMapping("/member/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "创建订单")
    @OperateLog("创建订单")
    @PostMapping
    public Result<Long> create(@Validated @RequestBody OrderCreateDTO dto) {
        return Result.success(orderService.create(dto));
    }

    @Operation(summary = "订单分页列表")
    @GetMapping("/page")
    public Result<PageResult<OrderVO>> page(@RequestParam(required = false) Integer status,
                                            @RequestParam(defaultValue = "1") long page,
                                            @RequestParam(defaultValue = "10") long size) {
        return Result.success(orderService.pageList(status, page, size));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    public Result<OrderVO> detail(@PathVariable("id") Long id) {
        return Result.success(orderService.detail(id));
    }

    @Operation(summary = "取消订单")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable("id") Long id) {
        orderService.cancel(id);
        return Result.success();
    }

    @Operation(summary = "模拟支付")
    @OperateLog("模拟支付")
    @PutMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable("id") Long id) {
        orderService.pay(id);
        return Result.success();
    }
}