package com.zhu.mall.common;


import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

/*
* 描述：    常量
* */
@Component
public class Constant {
    public static final String SALT="zWl8hiu3.,{PdsD]";
    public static final String MALL_USER = "mall_user";

    public static String FILE_UPLOAD_DIR;    //上传文件的地址

    //静态变量无法直接注入，要用set方法注入
    @Value("${file.upload.dir}")
    public void setFileUploadDir(String fileUploadDir) {
        FILE_UPLOAD_DIR = fileUploadDir;
    }

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price desc","price asc");
    }

    public interface SaleStatus{
        int NOT_SALE = 0; //商品下架状态
        int SALE = 1; //商品上架状态
    }

    public interface Cart{
        int NOT_CHECKED = 0; //商品未选中状态
        int CHECKED = 1; //商品选中状态
    }
}
