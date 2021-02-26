package com.naylor.springboot.avoidrepeat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/**
 * 响应 实体类
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private int status;
    private String msg;
    private Object data;
}
