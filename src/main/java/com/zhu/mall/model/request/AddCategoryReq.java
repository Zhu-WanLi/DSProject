package com.zhu.mall.model.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AddCategoryReq {
    @Size(min = 2,max = 5,message = "name的长度在2-5之间")
    @NotNull(message = "name不能为空")
    private String name;

    @NotNull(message = "type不能为空")  //默认提示信息为：不能为null。这里使用自定义
    @Max(3)
    private Integer type;

    @NotNull(message = "parentId不能为空")
    private Integer parentId;

    @NotNull(message = "orderNum不能为空")
    private Integer orderNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }
}
