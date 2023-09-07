package com.zhu.mall.model.service.impl;

import com.zhu.mall.model.dao.UserMapper;
import com.zhu.mall.model.pojo.User;
import com.zhu.mall.model.service.UserService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public User getUser() {
        return userMapper.selectByPrimaryKey(1);
    }


}
