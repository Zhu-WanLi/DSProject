package com.zhu.mall.service;

import com.github.pagehelper.PageInfo;
import com.zhu.mall.model.request.CreateOrderReq;
import com.zhu.mall.model.vo.OrderStatisticsVO;
import com.zhu.mall.model.vo.OrderVO;

import java.util.Date;
import java.util.List;

/**
 * 描述：      订单Service
 * */
public interface OrderService {
    //返回的是订单号
    String create(CreateOrderReq createOrderReq);

    OrderVO detail(String orderNo);

    PageInfo listForCustomer(Integer pageNum, Integer pageSize);

    void cancel(String orderNo);

    String qrcode(String orderNo);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    void pay(String orderNo);

    void delivered(String orderNo);

    void finish(String orderNo);

    List<OrderStatisticsVO> statistic(Date startDate, Date endDate);
}
