package com.zhu.mall.model.controller;

import com.mysql.cj.util.StringUtils;
import com.zhu.exception.MallException;
import com.zhu.exception.MallExceptionEnum;
import com.zhu.mall.model.common.ApiRestResponse;
import com.zhu.mall.model.common.Constant;
import com.zhu.mall.model.pojo.User;
import com.zhu.mall.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/test")
    @ResponseBody
    public User personalPage() {
        return userService.getUser();
    }


    /*
    *    注册
    * */
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


    /*
    *      用户登录接口
    * */
    @PostMapping("/login")
    @ResponseBody
    public ApiRestResponse login(@RequestParam("userName") String userName,
                                 @RequestParam("password") String password,
                                 HttpSession session) throws MallException {
        //账号空校验
        if(StringUtils.isNullOrEmpty(userName)){
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        //密码空校验
        if(StringUtils.isNullOrEmpty(password)){
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }

        //返回数据库中的数据，封装到user对象中
        User user = userService.login(userName, password);

        //保存用户信息不保存密码
        user.setPassword(null);
        session.setAttribute(Constant.MALL_USER,user);
        return ApiRestResponse.success(user);
    }

    /*
    *    管理员登录接口
    * */
    @PostMapping("/adminLogin")
    @ResponseBody
    public ApiRestResponse adminLogin(@RequestParam("userName") String userName,
                                 @RequestParam("password") String password,
                                 HttpSession session) throws MallException {
        //账号空校验
        if(StringUtils.isNullOrEmpty(userName)){
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        //密码空校验
        if(StringUtils.isNullOrEmpty(password)){
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }

        //返回数据库中的数据，封装到user对象中
        User user = userService.login(userName, password);

        //校验是否是管理员
        if (userService.checkAdminRole(user)) {
            user.setPassword(null);
            session.setAttribute(Constant.MALL_USER,user);
            return ApiRestResponse.success(user);
        } else {
            return ApiRestResponse.error(MallExceptionEnum.NEED_ADMIN);
        }
        //保存用户信息不保存密码

    }

    @PostMapping("/user/logout")
    @ResponseBody
    public ApiRestResponse logout(HttpSession session){
        session.removeAttribute(Constant.MALL_USER);
        return ApiRestResponse.success();
    }

    /*
     *      更新个性签名
     * */
    @PostMapping("/user/update")
    @ResponseBody
    public ApiRestResponse updateUserInfo(HttpSession session ,@RequestParam String signature) throws MallException {
        User currentUser = (User)session.getAttribute(Constant.MALL_USER);
        if (currentUser == null) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_LOGIN);
        }
        User user = new User();
        user.setId(currentUser.getId());
        user.setPersonalizedSignature(signature);
        userService.updateInformation(user);
        return ApiRestResponse.success();
    }
}
