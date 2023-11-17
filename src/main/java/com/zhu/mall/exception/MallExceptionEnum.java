package com.zhu.mall.exception;
/**
 * 描述： 异常枚举
 * */
public enum MallExceptionEnum {
    NEED_USER_NAME(10001,"用户名不能为空"),
    NEED_PASSWORD(10002,"密码不能为空"),
    PASSWORD_TOO_SHORT(10003,"密码不能小于8位"),
    NAME_EXISTED(10004,"不允许重名"),
    INSERT_FAILED(10005,"插入失败，请重试"),
    WRONG_PASSWORD(10006,"密码错误"),
    NEED_LOGIN(10007,"用户未登录"),
    UPDATE_FAILED(10008,"更新失败"),
    NEED_ADMIN(10009,"无管理员权限"),
    PARA_NOT_NULL(10010,"参数不能为空"),
    CREATE_FAILED(10011,"新增失败"),
    REQUEST_PARAM_ERROR(10012,"参数错误"),
    DELETE_FAILED(10013,"删除失败"),
    MKDIR_FAILED(10014,"文件夹创建失败"),
    UPLOAD_FAILED(10015,"图片上传失败"),
    NOT_SALE(10016,"商品状态不可售"),
    NOT_ENOUGH(10017,"商品库存不足"),
    CART_EMPTY(10018,"购物车选中商品不能为空"),
    NO_ENUM(10019,"未找到对应的枚举"),
    NO_ORDER(10020,"订单不存在"),
    NO_YOUR_ORDER(10021,"订单不属于你"),
    WRONG_ORDER_STATUS(10022,"订单状态不符"),
    EMAIL_ERROR(10023,"邮箱地址非法"),
    EMAIL_ALREADY_BEEN_REGISTERED(10024,"邮箱已被注册"),
    EMAIL_ALREADY_BEEN_SEND(10025,"email已发送，若无法收到，轻稍后再试"),
    NEED_EMAIL_ADDRESS(10026,"邮箱不能为空"),
    NEED_VERIFICATION_CODE(10027,"验证码不能为空"),
    VERIFICATION_C0DE_ERROR(10028,"验证码错误"),
    TOKEN_EXPIRED(10029,"token过期异常"),
    TOKEN_WRONG(10030,"token解析失败"),
    CANCEL_WRONG_ORDER_STATUS(10031,"订单状态有误，付款后暂不支持取消订单"),
    PAY_WRONG_ORDER_STATUS(10032,"订单状态有误，仅能在未付款时付款"),
    DELIVER_WRONG_ORDER_STATUS(10033,"订单状态有误，仅能在付款后发货"),
    FINISH_WRONG_ORDER_STATUS(10033,"订单状态有误，仅能在发货后完单"),
    SYSTEM_ERROR(20000,"系统异常");
    //异常码
    Integer code;  //异常码
    String msg;    //异常信息

    MallExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
