package com.zhu.mall.service;

/**
 * 描述：      邮件service
 * */
public interface EmailService {
    void sendSimpleMessage(String to,String subject,String text);

    boolean savaEmailToRedis(String emailAddress, String verificationCode);

    boolean checkEmailAndCode(String emailAddress, String verificationCode);
}
