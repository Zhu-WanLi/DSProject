package com.zhu.mall.utils;

import com.zhu.mall.common.Constant;
import org.apache.tomcat.util.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/*
* 描述：   MD5工具
* */
public class MD5Utils {
    public static String getMD5Str(String strValue) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return Base64.encodeBase64String(md5.digest((strValue + Constant.SALT).getBytes()));
    }

    public static void main(String[] args) {
        try {
            String md5Str = MD5Utils.getMD5Str("1234");
            System.out.println(md5Str);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
