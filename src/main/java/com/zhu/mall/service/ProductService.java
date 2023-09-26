package com.zhu.mall.service;

/*
* 描述：       商品Service
* */

import com.zhu.mall.model.pojo.Product;
import com.zhu.mall.model.request.AddProductReq;

public interface ProductService {

    void add(AddProductReq addProductReq);

    void update(Product updateProduct);

    void delete(Integer id);
}
