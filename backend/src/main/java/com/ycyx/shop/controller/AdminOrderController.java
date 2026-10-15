package com.ycyx.shop.controller;

import com.ycyx.shop.common.annotation.OperateLog;
import com.ycyx.shop.common.result.PageResult;
import com.ycyx.shop.common.result.Result;
import com.ycyx.shop.service.OrderService;
import com.ycyx.shop.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台订单管理接口
 */
@Tag(name = "后台订单管理", description = "订单列表/详情/发货")
@RestController
@RequestMapping("/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @Operation(summary = "订单分页列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('order:list')")
    public Result<PageResult<OrderVO>> page(@RequestParam(required = false) Integer status,
                                            @RequestParam(required = false) String orderNo,
                                            @RequestParam(defaultValue = "1") long page,
                                            @RequestParam(defaultValue = "10") long size) {
        return Result.success(orderService.adminPageList(status, orderNo, page, size));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('order:list')")
    public Result<OrderVO> detail(@PathVariable("id") Long id) {
        return Result.success(orderService.adminDetail(id));
    }

    @Operation(summary = "订单发货")
    @OperateLog("订单发货")
    @PutMapping("/{id}/ship")
    @PreAuthorize("hasAuthority('order:ship')")
    public Result<Void> ship(@PathVariable("id") Long id) {
        orderService.ship(id);
        return Result.success();
    }
}