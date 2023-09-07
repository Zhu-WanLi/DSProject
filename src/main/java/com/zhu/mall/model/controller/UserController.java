package com.zhu.mall.model.controller;

import com.mysql.cj.util.StringUtils;
import com.zhu.exception.MallException;
import com.zhu.exception.MallExceptionEnum;
import com.zhu.mall.model.common.ApiRestResponse;
import com.zhu.mall.model.pojo.User;
import com.zhu.mall.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/test")
    @ResponseBody
    public User personalPage() {
        return userService.getUser();
    }

    @PostMapping("/register")
    @ResponseBody
    public ApiRestResponse register(@RequestParam("userName") String userName,
                                    @RequestParam("password") String password) throws MallException {
        //账号空校验
        if(StringUtils.isNullOrEmpty(userName)){
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        //密码空校验
        if(StringUtils.isNullOrEmpty(password)){
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }

        //密码不能小于8位
        if(password.length()<8){
            return ApiRestResponse.error(MallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        userService.register(userName,password);
        return ApiRestResponse.success();
    }
}
