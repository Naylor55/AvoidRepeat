package com.naylor.springboot.avoidrepeat.domain.repeat.service;

import java.util.concurrent.TimeUnit;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RepeatTokenServiceImpl implements RepeatTokenService {

    @Autowired
    private RedisTemplate redisTemplate;

 
    
    /**
     * 生成key
     */
    @Override
    public void createToken(String key) {        
        redisTemplate.opsForValue().set(key,key,3,TimeUnit.SECONDS);
    }
    
    @Override
    public Boolean checkToken(String key) {
        return redisTemplate.hasKey(key);
    }


  

 
    
}
