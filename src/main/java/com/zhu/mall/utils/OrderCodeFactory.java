package com.zhu.mall.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 描述：      生成订单No工具类
 */
public class OrderCodeFactory {
    private static String getDateTime() {
        DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    private static int getRandom(Long n) {
        Random random = new Random();
        //获取随机五位数
        return (int) ((random.nextDouble() * 90000) + 10000);
    }

    public static String getOrderCode(Long userId){ //暂时用不到这个参数
        return getDateTime() + getRandom(userId);
    }
}
