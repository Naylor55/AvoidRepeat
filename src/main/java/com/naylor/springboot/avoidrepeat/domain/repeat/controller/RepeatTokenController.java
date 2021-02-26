package com.naylor.springboot.avoidrepeat.domain.repeat.controller;

import javax.servlet.http.HttpServletRequest;

import com.naylor.springboot.avoidrepeat.domain.repeat.service.RepeatTokenService;
import com.naylor.springboot.avoidrepeat.dto.Response;
import com.naylor.springboot.avoidrepeat.dto.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@Api(tags = "防止重复提交")
@RestController
@RequestMapping("/repeat")
public class RepeatTokenController {




    /**
     * 检查 token 是否存在
     * @param request
     * @return
     */
    @ApiIdempotent
    @PostMapping("/verify")
    public Response checktoken(@RequestBody User user){
        Response response=new Response();
        response.setStatus(1);
        response.setMsg("成功");
        return response;
    }

   
}
