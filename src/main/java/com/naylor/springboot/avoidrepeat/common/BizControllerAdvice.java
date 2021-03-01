package com.naylor.springboot.avoidrepeat.common;

import java.net.ConnectException;

import com.naylor.springboot.avoidrepeat.dto.ResponseData;
import com.naylor.springboot.avoidrepeat.exception.RepeatException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class BizControllerAdvice {

    /**
     * 捕获重复请求的异常 - RepeatException
     * 
     * @param exception
     * @return
     */
    @ResponseBody
    @ExceptionHandler(RepeatException.class)
    public ResponseEntity<ResponseData> serviceExceptionHandler(RepeatException exception) {
        ResponseData response = new ResponseData(Integer.valueOf(exception.getCode()), exception.getMsg(),
                exception.getStackTrace());
        return new ResponseEntity<ResponseData>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 捕获网络连接的异常 - ConnectException 这里会捕获 Redis 连接不上的异常
     * 
     * @param exception
     * @return
     */
    @ResponseBody
    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<ResponseData> connectExceptionHandler(ConnectException exception) {
        return new ResponseEntity<ResponseData>(new ResponseData(0, exception.getMessage(), exception.getStackTrace()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 捕获常规异常 -Exception
     * 
     * @param exception
     * @return
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseData> exceptionHandler(Exception exception) {
        return new ResponseEntity<ResponseData>(new ResponseData(0, exception.getMessage(), exception.getStackTrace()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
