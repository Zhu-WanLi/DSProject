package com.zhu.exception;

public class MallException extends Exception{
    private final Integer code;
    private final String message;

    public MallException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    public MallException(MallExceptionEnum ex) {
        this(ex.getCode(), ex.getMsg());
    }
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
