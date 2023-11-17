package com.zhu.mall.model.query;

import java.util.Date;

/**
 * 描述：      订单统计查询query
 * */
public class OrderStatisticsQuery {
    private Date startDate;
    private Date endDate;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
