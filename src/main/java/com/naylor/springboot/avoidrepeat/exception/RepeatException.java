package com.naylor.springboot.avoidrepeat.exception;

import com.naylor.springboot.avoidrepeat.constant.ResponseCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RepeatException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RepeatException(ResponseCode responseCode) {
        if (null == responseCode) {
            responseCode = ResponseCode.REPETITIVE_OPERATION;
        }
        this.code = responseCode.getCode();
        this.msg = responseCode.getMsg();
    }

    private Integer code;
    private String msg;



}
