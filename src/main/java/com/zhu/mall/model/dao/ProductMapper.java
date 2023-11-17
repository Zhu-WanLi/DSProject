package com.zhu.mall.model.dao;

import com.zhu.mall.model.pojo.Product;
import com.zhu.mall.model.query.ProductListQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    Product selectByName(String name);

    int batchUpdateSellStatus(@Param("ids") Integer[] ids,@Param("sellStatus") Integer sellStatus);

    List<Product> selectListForAdmin();

    List<Product> selectList(@Param("query") ProductListQuery query);
}

/*   User selectByName(String userName);

    User selectLogin(@Param("username") String username,@Param("password") String password);
                    //多个参数要进行指定


                     <select id="selectByName" parameterType="java.lang.String" resultMap="BaseResultMap">
    select
    <include refid="Base_Column_List"/>
    from imooc_mall_user
    where username = #{username,jdbcType=VARCHAR}
  </select>

  <select id="selectLogin" parameterType="map" resultMap="BaseResultMap">
    select
    <include refid="Base_Column_List"/>
    from imooc_mall_user
    where username = #{username,jdbcType=VARCHAR}
    and password = #{password,jdbcType=VARCHAR}
  </select>*/