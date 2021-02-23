package com.naylor.springboot.avoidrepeat.common;

import com.naylor.springboot.avoidrepeat.dto.Response;
import com.naylor.springboot.avoidrepeat.exception.ServiceException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class BizControllerAdvice {
    @ResponseBody
    @ExceptionHandler(ServiceException.class)
    public Response serviceExceptionHandler(ServiceException exception){
        Response response=new Response(Integer.valueOf(exception.getCode()),exception.getMsg(),null);
        return response;
    }
}
