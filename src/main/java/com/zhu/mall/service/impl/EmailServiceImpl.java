package com.zhu.mall.service.impl;

import com.zhu.mall.common.Constant;
import com.zhu.mall.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

/**
 * 描述：      邮件service实现类
 */

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    JavaMailSender mailSender; //用于发邮件

    @Value("${spring.redis.host}")  //配置文件注入redis的ip
    String redisIp;

    @Value("${spring.redis.port}")  //配置文件注入redis的port
    Integer redisPort;

    @Value("${spring.redis.password}")  //配置文件注入redis的密码
    String password;

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(Constant.EMAIL_FROM);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setText(text);
        mailSender.send(simpleMailMessage);
    }


    /**
     * 将验证码存入redis数据库
     * 如果数据库中有数据，返回false，避免多次发送
     */
    @Override
    public boolean savaEmailToRedis(String emailAddress, String verificationCode) {
        //找不到
       /* Config config = new Config();
        config.useSingleServer()
                .setTimeout(1000000)
                .setAddress("rediss://192.168.229.128:6379");
        RedissonClient client = Redisson.create(config);
        RBucket<String> bucket = client.getBucket(emailAddress);*/

        String value = null;
        //设置服务器IP和端口
        Jedis jedis = new Jedis(redisIp, redisPort);
        try {
            jedis.auth(password);
            jedis.select(0);
            value = jedis.get("emailAddress");

            //从数据库中取数据
            //这个值是否存在
            if (value == null) {
                //数据不存在，第一次发送
                //放入数据，指定过期时间，单位为秒
                jedis.set("emailAddress", verificationCode);
                jedis.expire("emailAddress",66);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        //数据存在，重复发
        return false;
    }

    @Override
    public boolean checkEmailAndCode(String emailAddress, String verificationCode){
        //设置服务器IP和端口
        Jedis jedis = new Jedis(redisIp, redisPort);
        try {
            jedis.auth(password);
            jedis.select(0);
            if (jedis.get("emailAddress") == verificationCode) {
                //redis中没有验证码
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        //数据不匹配
        return false;
    }

}
