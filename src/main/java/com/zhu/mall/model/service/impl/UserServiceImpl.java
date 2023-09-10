package com.zhu.mall.model.service.impl;

import com.zhu.exception.MallException;
import com.zhu.exception.MallExceptionEnum;
import com.zhu.mall.model.dao.UserMapper;
import com.zhu.mall.model.pojo.User;
import com.zhu.mall.model.service.UserService;
import com.zhu.mall.model.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

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
        try {
            user.setPassword(MD5Utils.getMD5Str(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        int count = userMapper.insertSelective(user);
        if (count == 0) {
            throw new MallException(MallExceptionEnum.INSERT_FAILED);
        }
    }

    @Override
    public User login(String username, String password) throws MallException {
        String md5Password = null;
        try {
            md5Password = MD5Utils.getMD5Str(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        User user = userMapper.selectLogin(username, md5Password);
        if (user == null){
            throw new MallException(MallExceptionEnum.WRONG_PASSWORD);
        }
        return user;
    }

    @Override
    public void updateInformation(User user) throws MallException {
        int count = userMapper.updateByPrimaryKeySelective(user);
        if(count != 1){
            throw new MallException(MallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public boolean checkAdminRole(User user){
        return user.getRole().equals(2);
    }
}
