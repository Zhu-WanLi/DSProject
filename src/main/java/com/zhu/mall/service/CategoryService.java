package com.zhu.mall.service;

import com.github.pagehelper.PageInfo;
import com.zhu.mall.model.request.AddCategoryReq;

public interface CategoryService {
    void add(AddCategoryReq addCategoryReq);

    void delete(Integer id);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);
}
