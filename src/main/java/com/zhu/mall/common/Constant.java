package com.zhu.mall.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
}
