package com.naylor.springboot.avoidrepeat.constant;


/**
 * 响应编码枚举
 */
public enum ResponseCode {
    // 通用模块 1xxxx
    ILLEGAL_ARGUMENT(10000, "参数不合法"),
    REPETITIVE_OPERATION(10001, "请勿重复操作"),
    ;

    private Integer code;
    private String msg;


    ResponseCode(Integer code, String msg) {
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
