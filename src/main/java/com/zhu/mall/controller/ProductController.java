package com.zhu.mall.controller;

import com.github.pagehelper.PageInfo;
import com.zhu.mall.common.ApiRestResponse;
import com.zhu.mall.model.pojo.Product;
import com.zhu.mall.model.request.ProductListReq;
import com.zhu.mall.service.ProductService;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
* 描述：       前台商品Controller
* */
@RestController
public class ProductController {
    @Autowired
    ProductService productService;
    @ApiOperation("商品详情")
    @GetMapping("/product/detail")
    public ApiRestResponse detail(Integer id){
        Product product = productService.detail(id);
        return ApiRestResponse.success(product);
    }

    @ApiOperation("商品详情")
    @GetMapping("/product/list")
    public ApiRestResponse detail(ProductListReq productListReq){
        PageInfo products = productService.list(productListReq);
        return ApiRestResponse.success(products);
    }

}
