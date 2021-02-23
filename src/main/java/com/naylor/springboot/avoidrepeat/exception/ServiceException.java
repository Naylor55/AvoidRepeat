package com.naylor.springboot.avoidrepeat.exception;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class ServiceException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    private String code;
    private String msg;
}
