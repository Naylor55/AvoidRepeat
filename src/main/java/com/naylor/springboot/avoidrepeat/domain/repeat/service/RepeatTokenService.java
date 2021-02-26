package com.naylor.springboot.avoidrepeat.domain.repeat.service;

import javax.servlet.http.HttpServletRequest;

import com.naylor.springboot.avoidrepeat.dto.Response;




public interface RepeatTokenService {
    public void createToken(String key);
    public Boolean checkToken(String key);
}
