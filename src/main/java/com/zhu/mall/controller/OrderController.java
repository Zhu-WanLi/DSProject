package com.zhu.mall.controller;

import com.zhu.mall.common.ApiRestResponse;
import com.zhu.mall.model.request.CreateOrderReq;
import com.zhu.mall.model.vo.OrderVO;
import com.zhu.mall.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 描述：      订单Controller
 */

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    @PostMapping("/create")
    @ApiOperation("创建订单")
    public ApiRestResponse create(@RequestBody CreateOrderReq createOrderReq) {
        String orderNo = orderService.create(createOrderReq);
        return ApiRestResponse.success(orderNo);
    }

    @GetMapping("/detail")
    @ApiOperation("查看订单详情")
    public ApiRestResponse detail(@RequestParam String orderNo) {
        OrderVO orderVO = orderService.detail(orderNo);
        return ApiRestResponse.success(orderVO);
    }

    @GetMapping("/list")
    @ApiOperation("前台订单列表")
    public ApiRestResponse detail(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return ApiRestResponse.success(orderService.listForCustomer(pageNum, pageSize));
    }

    /**
     * 前台取消订单
     */
    @PostMapping("/cancel")
    @ApiOperation("前台取消订单")
    public ApiRestResponse cancel(@RequestParam String orderNo) {
        orderService.cancel(orderNo);
        return ApiRestResponse.success();
    }

    /**
     * 生成支付二维码
     */
    @PostMapping("/qrcode")
    @ApiOperation("生成支付二维码")
    public ApiRestResponse qrcode(@RequestParam String orderNo) {
        return ApiRestResponse.success(orderService.qrcode(orderNo));
    }

    /**
     * 支付接口
     */
    @GetMapping("/pay")
    @ApiOperation("支付接口")
    public ApiRestResponse pay(@RequestParam String orderNo) {
        orderService.pay(orderNo);
        return ApiRestResponse.success();
    }

    /**
     * 订单完结
     */
    @PostMapping("/finish")
    @ApiOperation("订单完结")
    public ApiRestResponse finish(@RequestParam String orderNo) {
        orderService.finish(orderNo);
        return ApiRestResponse.success();
    }
}
