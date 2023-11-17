package com.zhu.mall.filter;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.zhu.mall.common.Constant;
import com.zhu.mall.exception.MallException;
import com.zhu.mall.exception.MallExceptionEnum;
import com.zhu.mall.model.pojo.User;
import com.zhu.mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * 描述：      用户过滤器
 * */
public class UserFilter implements Filter {
    public static User currentUser;

    @Autowired
    UserService userService;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader(Constant.JWT_TOKEN);//这里面的值是自己指定的，前后端约定好的
        if (token == null) {
            PrintWriter out = new HttpServletResponseWrapper((HttpServletResponse) servletResponse).getWriter();
            out.write("{\n" +
                    "    \"status\": 10007,\n" +
                    "    \"msg\": \"NEED_LOGIN\",\n" +
                    "    \"data\": null\n" +
                    "}");
            out.flush();
            out.close();
            return;
        }
        Algorithm algorithm = Algorithm.HMAC256(Constant.JWT_KEY);
        //verifier:校验
        JWTVerifier verifier = JWT.require(algorithm).build();
        try{
            DecodedJWT jwt = verifier.verify(token); //校验token（用户）
            currentUser = new User();
            currentUser.setId(jwt.getClaim(Constant.USER_ID).asInt());
            currentUser.setUsername(jwt.getClaim(Constant.USER_NAME).asString());
            currentUser.setRole(jwt.getClaim(Constant.USER_ROLE).asInt());
        } catch (TokenExpiredException e){  //token过期
            throw new MallException(MallExceptionEnum.TOKEN_EXPIRED);
        }catch (JWTDecodeException e) {
            //解码失败，抛异常
            throw new MallException(MallExceptionEnum.TOKEN_WRONG);
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
