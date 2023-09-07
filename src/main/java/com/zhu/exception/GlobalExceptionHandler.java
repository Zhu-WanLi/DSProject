package com.zhu.exception;

import com.zhu.mall.model.common.ApiRestResponse;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;

/**
* 描述：      处理统一异常
*/
@ControllerAdvice    //拦截异常
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)   //规定处理哪种异常
    @ResponseBody                        //返回json数据
    public Object handleException(Exception e){
        log.error("Default Exception:",e);
        return ApiRestResponse.error(MallExceptionEnum.SYSTEM_ERROR);
    }

    @ExceptionHandler(MallException.class)   //规定处理哪种异常
    @ResponseBody
    public Object handleMallException(MallException e){
        log.error("MallException: ",e);
        return ApiRestResponse.error(e.getCode(),e.getMessage());
    }
}
