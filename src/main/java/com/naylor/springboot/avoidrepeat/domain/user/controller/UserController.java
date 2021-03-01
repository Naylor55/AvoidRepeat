package com.naylor.springboot.avoidrepeat.domain.user.controller;

import com.naylor.springboot.avoidrepeat.aop.ApiIdempotent;
import com.naylor.springboot.avoidrepeat.dto.ResponseData;
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
    public User createUser(@RequestBody User user) {
        user.setId(user.getId()+1);
        user.setName(user.getName() + user.getId());
        return user;
    }
}
