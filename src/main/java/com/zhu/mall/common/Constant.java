package com.zhu.mall.common;


import com.google.common.collect.Sets;
import com.zhu.mall.exception.MallException;
import com.zhu.mall.exception.MallExceptionEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 描述：    常量
 */
@Component
public class Constant {
    public static final String SALT = "zWl8hiu3.,{PdsD]";
    public static final String MALL_USER = "mall_user";
    public static final String EMAIL_SUBJECT = "您的验证码";
    public static final String EMAIL_FROM = "1585978378@qq.com";
    public static final String WATER_MARK_JPG = "watermark.jpg"; //水印
    public static final Integer IMAGE_SIZE = 400;
    public static final Float IMAGE_OPACITY = 1F;

    public static String FILE_UPLOAD_DIR;    //上传文件的地址

    //静态变量无法直接注入，要用set方法注入
    @Value("${file.upload.dir}")
    public void setFileUploadDir(String fileUploadDir) {
        FILE_UPLOAD_DIR = fileUploadDir;
    }

    public interface ProductListOrderBy {
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price desc", "price asc");
    }

    public interface SaleStatus {
        int NOT_SALE = 0; //商品下架状态
        int SALE = 1; //商品上架状态
    }

    public interface Cart {
        int NOT_CHECKED = 0; //商品未选中状态
        int CHECKED = 1; //商品选中状态
    }


    /**
     * 描述：      订单状态枚举类
     */
    public enum OrderStatusEnum {
        CANCELED(0, "用户已取消"),
        NOT_PAID(10, "未付款"),
        PAID(20, "已付款"),
        DELIVERED(30, "已发货"),
        FINISHED(40, "交易完成");

        private int code;
        private String value;

        //用code拿到信息
        public static OrderStatusEnum codeOf(int code) {
            for (OrderStatusEnum orderStatusEnum : values()) {
                if (orderStatusEnum.getCode() == code) {
                    return orderStatusEnum;
                }
            }
            throw new MallException(MallExceptionEnum.NO_ENUM);
        }

        OrderStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static final String JWT_KEY = "MALL";  //Jwt秘钥
    public static final String JWT_TOKEN = "jwt_token";  //前后端约定的值

    public static final String USER_NAME = "user_name";
    public static final String USER_ID = "user_id";
    public static final String USER_ROLE = "user_role";
    public static final Long EXPIRE_TIME = 60 * 1000 * 60 * 24 * 100l; //单位是毫秒
}
