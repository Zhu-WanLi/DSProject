package com.zhu.mall.controller;

import com.zhu.mall.common.ApiRestResponse;
import com.zhu.mall.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 描述：      管理员订单Controller
 */
@RestController
@RequestMapping("/admin/order")
public class OrderAdminController {
    @Autowired
    OrderService orderService;

    @GetMapping("/list")
    @ApiOperation("管理员订单列表")
    public ApiRestResponse listForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return ApiRestResponse.success(orderService.listForAdmin(pageNum, pageSize));
    }

    /**
     * 管理员发货
     * */
    @PostMapping("/delivered")
    @ApiOperation("管理员发货")
    public ApiRestResponse delivered(@RequestParam String orderNo) {
        orderService.delivered(orderNo);
        return ApiRestResponse.success();
    }

    /**
     * 订单统计
     * */
    @GetMapping("/statistics")
    @ApiOperation("订单统计")
    public ApiRestResponse statistics(@RequestParam("startDate") @DateTimeFormat(pattern = "yy-MM-dd") Date startDate,
                                      @RequestParam("endDate") @DateTimeFormat(pattern = "yy-MM-dd") Date endDate) {
        return ApiRestResponse.success(orderService.statistic(startDate,endDate));
    }
}
