package com.zhu.mall.service;

import com.zhu.mall.exception.MallException;
import com.zhu.mall.model.pojo.User;

public interface UserService {
    User getUser();

    void register(String userName, String password) throws MallException;

    User login(String username, String password) throws MallException;

    void updateInformation(User user) throws MallException;

    boolean checkAdminRole(User user);
}
