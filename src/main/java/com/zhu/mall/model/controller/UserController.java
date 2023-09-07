package com.zhu.mall.model.controller;

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
    public String personalPage() {
        return userService.getUser().toString();
    }
}
