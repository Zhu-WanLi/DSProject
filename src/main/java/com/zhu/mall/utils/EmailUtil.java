package com.zhu.mall.utils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.*;

/**
 * 描述：      邮箱工具
 */
public class EmailUtil {

    /**
     * 随机生成字符
     */
    public static String genVerificationCode() {
        List<String> verificationChars = Arrays.asList(new String[]{"1", "2", "3", "", "4", "5", "6", "7",
                "8", "9", "0","a","b","c"});
        Collections.shuffle(verificationChars);
        String result = "";
        for (int i = 0; i < 6; i++) {
            result += verificationChars.get(i);
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(EmailUtil.genVerificationCode());
    }
    //emailAddress是否合法校验
    public static boolean isValidEmail(String emailAddress) {
        boolean result = true;
        try {
            InternetAddress internetAddress = new InternetAddress(emailAddress);
            //调用校验方法
            internetAddress.validate();
        } catch (AddressException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
}
