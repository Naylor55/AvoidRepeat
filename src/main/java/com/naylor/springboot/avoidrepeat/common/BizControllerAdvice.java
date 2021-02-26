package com.naylor.springboot.avoidrepeat.common;

import com.naylor.springboot.avoidrepeat.dto.Response;
import com.naylor.springboot.avoidrepeat.exception.RepeatException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;



@ControllerAdvice
public class BizControllerAdvice {

    /**
     * 捕获重复请求的异常 - RepeatException
     * @param exception
     * @return
     */
    @ResponseBody
    @ExceptionHandler(RepeatException.class)
    public Response serviceExceptionHandler(RepeatException exception){
        Response response=new Response(Integer.valueOf(exception.getCode()),exception.getMsg(),null);
        return response;
    }
}
