package com.zhu.mall.controller;

import com.zhu.mall.common.ApiRestResponse;
import com.zhu.mall.filter.UserFilter;
import com.zhu.mall.model.vo.CartVO;
import com.zhu.mall.service.CartService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* 描述：       购物车Controller
 * */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    CartService cartService;

    @GetMapping("/list")
    @ApiOperation("购物车列表")
    public ApiRestResponse list(){
        //内部获取用户ID，防止横向越权（操作别人的账户）
        List<CartVO> cartVOList = cartService.list(UserFilter.currentUser.getId());
        return ApiRestResponse.success(cartVOList);
    }
    @PostMapping("/add")
    @ApiOperation("添加商品到购物车")
    public ApiRestResponse add(@RequestParam Integer productId,@RequestParam Integer count){
        List<CartVO> cartList = cartService.add(UserFilter.currentUser.getId(), productId, count);
        return ApiRestResponse.success(cartList);
    }

    @PostMapping("/update")
    @ApiOperation("更新购物车")
    public ApiRestResponse update(@RequestParam Integer productId,@RequestParam Integer count){
        List<CartVO> cartList = cartService.update(UserFilter.currentUser.getId(), productId, count);
        return ApiRestResponse.success(cartList);
    }

    @PostMapping("/delete")
    @ApiOperation("删除购物车商品")
    public ApiRestResponse delete(@RequestParam Integer productId){
        List<CartVO> cartList = cartService.delete(UserFilter.currentUser.getId(), productId);
        return ApiRestResponse.success(cartList);
    }

}
