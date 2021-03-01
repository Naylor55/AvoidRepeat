package com.naylor.springboot.avoidrepeat.domain.repeat.service;





public interface RepeatTokenService {
    public void createToken(String key);
    public Boolean checkToken(String key);
}
