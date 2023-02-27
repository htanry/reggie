package com.example.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，底层是基于AOP实现的，代理了各个处理器方法
 */
//被RestController和Controller注解的类会被代理
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)   //标注对应方法处理哪种异常
    public Response<String> exceptionHandler(SQLIntegrityConstraintViolationException e){
        log.error(e.getMessage());
        //SQLIntegrityConstraintViolationException的报错信息:Duplicate entry 'zhangsan' for key 'idx_username'
        if (e.getMessage().contains("Duplicate entry")){
            return Response.error(e.getMessage().split(" ")[2] + "已存在");
        }
        return Response.error("未知错误");
    }

    /**
     * 可以将异常信息反映到浏览器
     * @param e
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public Response<String> exceptionHandler(CustomException e){
        log.error(e.getMessage());
        return Response.error(e.getMessage());
    }
}
