package com.zhu.mall.model.dao;

import com.zhu.mall.model.pojo.Cart;
import com.zhu.mall.model.vo.CartVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CancellationException;

@Repository
public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    List<CartVO> selectList(@Param("userId")Integer userId);
    Cart selectCartByUserIdAndProductId(@Param("userId")Integer userId,
                                        @Param("productId") Integer productId);

    int selectOrNot(@Param("userId")Integer userId, @Param("productId") Integer productId,
                     @Param("selected")Integer selected);
}