package com.zhu.mall.model.vo;

import java.util.Date;

/**
 * 描述：      每日订单统计返回VO
 * */
public class OrderStatisticsVO {
    private Date days;
    private int amount;

    public Date getDays() {
        return days;
    }

    public void setDays(Date days) {
        this.days = days;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
