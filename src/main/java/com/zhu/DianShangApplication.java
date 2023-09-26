package com.zhu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan(basePackages = "com.zhu.mall.model.dao")
@EnableSwagger2  //自动生成API文档
@EnableCaching   //缓存
public class DianShangApplication {

    public static void main(String[] args) {
        SpringApplication.run(DianShangApplication.class, args);
    }

}
