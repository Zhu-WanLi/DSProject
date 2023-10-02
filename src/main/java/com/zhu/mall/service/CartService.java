package com.zhu.mall.service;

import com.zhu.mall.model.vo.CartVO;

import java.util.List;

/**
 * 描述：      购物车Service
 * */
public interface CartService {
    List<CartVO> list(Integer userId);

    //每次前端添加完购物车的内容会变化，与其再调接口，不如直接返回list
    List<CartVO> add(Integer userId, Integer productId, Integer count);

    List<CartVO> update(Integer userId, Integer productId, Integer count);

    List<CartVO> delete(Integer userId, Integer productId);

    List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected);

    List<CartVO> selectOrNotAll(Integer userId, Integer selected);
}
