package com.zhu.mall.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mysql.cj.util.StringUtils;
import com.zhu.mall.exception.MallException;
import com.zhu.mall.exception.MallExceptionEnum;
import com.zhu.mall.common.ApiRestResponse;
import com.zhu.mall.common.Constant;
import com.zhu.mall.model.pojo.User;
import com.zhu.mall.service.EmailService;
import com.zhu.mall.service.UserService;
import com.zhu.mall.utils.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;


@Controller
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    EmailService emailService;

    @GetMapping("/test")
    @ResponseBody
    public String personalPage() {
        return "1";
    }


    /**
     * 注册
     */
    @PostMapping("/register")
    @ResponseBody
    public ApiRestResponse register(@RequestParam("userName") String userName,
                                    @RequestParam("password") String password,
                                    @RequestParam("emailAddress") String emailAddress,
                                    @RequestParam("verificationCode") String verificationCode)
            throws MallException {
        //邮箱空校验
        if (StringUtils.isNullOrEmpty(emailAddress)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_EMAIL_ADDRESS);
        }
        //检查邮箱地址是否有注册
        boolean emailPassed = userService.checkEmailRegistered(emailAddress);
        if (emailPassed) {
            return ApiRestResponse.error(MallExceptionEnum.EMAIL_ALREADY_BEEN_REGISTERED);
        }
        //验证码空校验
        if (StringUtils.isNullOrEmpty(verificationCode)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_VERIFICATION_CODE);
        }
        //账号空校验
        if (StringUtils.isNullOrEmpty(userName)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        //密码空校验
        if (StringUtils.isNullOrEmpty(password)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }
        //密码不能小于8位
        if (password.length() < 8) {
            return ApiRestResponse.error(MallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        //校验邮箱和验证码是否匹配
        boolean checkEmailAndCode = emailService.checkEmailAndCode(emailAddress, verificationCode);
        if (checkEmailAndCode) {
            return ApiRestResponse.error(MallExceptionEnum.VERIFICATION_C0DE_ERROR);
        }

        userService.register(userName, password, emailAddress);
        return ApiRestResponse.success();
    }


    /**
     * 用户登录接口
     */
    @PostMapping("/login")
    @ResponseBody
    public ApiRestResponse login(@RequestParam("userName") String userName,
                                 @RequestParam("password") String password,
                                 HttpSession session) throws MallException {
        //账号空校验
        if (StringUtils.isNullOrEmpty(userName)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        //密码空校验
        if (StringUtils.isNullOrEmpty(password)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }

        //返回数据库中的数据，封装到user对象中
        User user = userService.login(userName, password);

        //保存用户信息不保存密码
        user.setPassword(null);
        session.setAttribute(Constant.MALL_USER, user);
        return ApiRestResponse.success(user);
    }

    /**
     * 管理员登录接口
     */
    @PostMapping("/adminLogin")
    @ResponseBody
    public ApiRestResponse adminLogin(@RequestParam("userName") String userName,
                                      @RequestParam("password") String password,
                                      HttpSession session) throws MallException {
        //账号空校验
        if (StringUtils.isNullOrEmpty(userName)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        //密码空校验
        if (StringUtils.isNullOrEmpty(password)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }

        //返回数据库中的数据，封装到user对象中
        User user = userService.login(userName, password);

        //校验是否是管理员
        if (userService.checkAdminRole(user)) {
            user.setPassword(null);
            session.setAttribute(Constant.MALL_USER, user);
            return ApiRestResponse.success(user);
        } else {
            return ApiRestResponse.error(MallExceptionEnum.NEED_ADMIN);
        }
        //保存用户信息不保存密码

    }

    @PostMapping("/user/logout")
    @ResponseBody
    public ApiRestResponse logout(HttpSession session) {
        session.removeAttribute(Constant.MALL_USER);
        return ApiRestResponse.success();
    }

    /**
     * 更新个性签名
     */
    @PostMapping("/user/update")
    @ResponseBody
    public ApiRestResponse updateUserInfo(HttpSession session, @RequestParam String signature) throws MallException {
        User currentUser = (User) session.getAttribute(Constant.MALL_USER);
        if (currentUser == null) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_LOGIN);
        }
        User user = new User();
        user.setId(currentUser.getId());
        user.setPersonalizedSignature(signature);
        userService.updateInformation(user);
        return ApiRestResponse.success();
    }

    /**
     * 发送邮件
     */
    @PostMapping("/user/sendEmail")
    @ResponseBody
    public ApiRestResponse sendEmail(@RequestParam("emailAddress") String emailAddress) {
        //检查邮件地址是否有效，检查是否已注册
        boolean validEmail = EmailUtil.isValidEmail(emailAddress);
        if (validEmail) {
            //检查邮箱地址是否有注册
            boolean emailPassed = userService.checkEmailRegistered(emailAddress);
            if (emailPassed) {
                return ApiRestResponse.error(MallExceptionEnum.EMAIL_ALREADY_BEEN_REGISTERED);
            } else {
                String verificationCode = EmailUtil.genVerificationCode();

                boolean savaEmailToRedis = emailService.savaEmailToRedis(emailAddress, verificationCode);

                //检查redis数据库中是否有数据，判断是否重复发送
                if (savaEmailToRedis) {
                    //发邮件
                    emailService.sendSimpleMessage(emailAddress, Constant.EMAIL_SUBJECT,
                            "欢迎注册，您的验证码是:" + verificationCode);
                    return ApiRestResponse.success();
                } else {
                    return ApiRestResponse.error(MallExceptionEnum.EMAIL_ALREADY_BEEN_SEND);
                }
            }
        } else {
            return ApiRestResponse.error(MallExceptionEnum.EMAIL_ERROR);
        }
    }

    @PostMapping("/loginWithJwt")
    @ResponseBody
    public ApiRestResponse loginWithJwt(@RequestParam("userName") String userName,
                                        @RequestParam("password") String password)
            throws MallException {
        //账号空校验
        if (StringUtils.isNullOrEmpty(userName)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_USER_NAME);
        }
        //密码空校验
        if (StringUtils.isNullOrEmpty(password)) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_PASSWORD);
        }

        //返回数据库中的数据，封装到user对象中
        User user = userService.login(userName, password);

        //保存用户信息不保存密码
        user.setPassword(null);

        //algorithm:算法
        Algorithm algorithm = Algorithm.HMAC256(Constant.JWT_KEY);
        //生成JwtToken
        String token = JWT.create().withClaim(Constant.USER_NAME, user.getUsername())
                .withClaim(Constant.USER_ID, user.getId())
                .withClaim(Constant.USER_ROLE, user.getRole())
                .withExpiresAt(new Date(System.currentTimeMillis() + Constant.EXPIRE_TIME))  //过期时间
                .sign(algorithm);//签名，验证是否被更改，放置被篡改
        return ApiRestResponse.success(token);
    }

}
