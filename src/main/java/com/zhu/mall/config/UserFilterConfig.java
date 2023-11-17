package com.zhu.mall.config;

import com.zhu.mall.filter.AdminFilter;
import com.zhu.mall.filter.UserFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* 描述：       UserFilter的配置
* */
@Configuration
public class UserFilterConfig {
    @Bean
    public UserFilter userFilter() {
        return new UserFilter();
    }

    @Bean(name = "userFilterConf")
    public FilterRegistrationBean userFilterConf() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(userFilter());

        //设置拦截路径
        filterRegistrationBean.addUrlPatterns("/cart/*");
        filterRegistrationBean.addUrlPatterns("/order/*");
        //filterRegistrationBean.addUrlPatterns("/user/update");

        //设置拦截器name
        filterRegistrationBean.setName("userFilterConf");
        return filterRegistrationBean;
    }
}
