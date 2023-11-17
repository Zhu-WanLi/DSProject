package com.zhu.mall.model.dao;

import com.zhu.mall.model.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByName(String userName);

    User selectLogin(@Param("username") String username, @Param("password") String password);
    //多个参数要进行指定

    User selectOneByEmailAddress(String emailAddress);
}