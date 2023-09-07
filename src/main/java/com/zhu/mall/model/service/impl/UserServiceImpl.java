package com.zhu.mall.model.service.impl;

import com.zhu.exception.MallException;
import com.zhu.exception.MallExceptionEnum;
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

    @Override
    public void register(String userName, String password) throws MallException {
        User result = userMapper.selectByName(userName);
        if (result != null) {
            throw new MallException(MallExceptionEnum.NAME_EXISTED);
        }

        User user = new User();
        user.setUsername(userName);
        user.setPassword(password);
        int count = userMapper.insertSelective(user);
        if (count == 0) {
            throw new MallException(MallExceptionEnum.INSERT_FAILED);
        }

    }


}
