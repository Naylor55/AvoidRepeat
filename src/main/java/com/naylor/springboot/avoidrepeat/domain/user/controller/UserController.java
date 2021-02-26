package com.naylor.springboot.avoidrepeat.domain.user.controller;

import com.naylor.springboot.avoidrepeat.aop.ApiIdempotent;
import com.naylor.springboot.avoidrepeat.dto.Response;
import com.naylor.springboot.avoidrepeat.dto.User;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@Api(tags = "用户")
@RestController
@RequestMapping("/user")
public class UserController {



    @ApiIdempotent
    @PostMapping("/")
    public Response CreateUser(@RequestBody User user) {
        Response response = new Response();
        response.setStatus(5).setMsg("create-ok");
        return response;
    }
}
