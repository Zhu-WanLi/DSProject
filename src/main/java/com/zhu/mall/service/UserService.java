package com.zhu.mall.service;

import com.zhu.mall.exception.MallException;
import com.zhu.mall.model.pojo.User;

public interface UserService {
    User getUser();//测试用
    void register(String userName, String password, String emailAddress) throws MallException;

    User login(String username, String password) throws MallException;

    void updateInformation(User user) throws MallException;

    boolean checkAdminRole(User user);

    boolean checkEmailRegistered(String emailAddress);
}
