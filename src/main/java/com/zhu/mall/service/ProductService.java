package com.zhu.mall.service;

/*
* 描述：       商品Service
* */

import com.github.pagehelper.PageInfo;
import com.zhu.mall.model.pojo.Product;
import com.zhu.mall.model.request.AddProductReq;
import com.zhu.mall.model.request.ProductListReq;

public interface ProductService {

    void add(AddProductReq addProductReq);

    void update(Product updateProduct);

    void delete(Integer id);

    void batchUpdateSellStatus(Integer[] ids, Integer sellStatus);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    Product detail(Integer id);

    PageInfo list(ProductListReq productListReq);
}
