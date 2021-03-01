package com.naylor.springboot.avoidrepeat.domain.repeat.controller;

import com.naylor.springboot.avoidrepeat.aop.ApiIdempotent;

import com.naylor.springboot.avoidrepeat.dto.ResponseData;
import com.naylor.springboot.avoidrepeat.dto.User;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@Api(tags = "防止重复提交")
@RestController
@RequestMapping("/repeat")
public class RepeatTokenController {


    @PostMapping("/hello")
    public String hello() {
        return "Hello,World!";
    }

    /**
     * 检查 token 是否存在
     * 
     * @param request
     * @return
     */


}
