package com.naylor.springboot.avoidrepeat.Interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naylor.springboot.avoidrepeat.domain.repeat.service.RepeatTokenService;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ApiIdempotentPointcut {

    @Autowired
    RepeatTokenService repeatTokenService;

    @Pointcut("@annotation(ApiIdempotent)")
    public void ApiIdempotentAspect() {
    }

    @Around("ApiIdempotentAspect()")
    public void methodAround(ProceedingJoinPoint point) throws Exception {
        String typeName = point.getSignature().getDeclaringTypeName();
        String methodName = point.getSignature().getName();
        String methodFullName = typeName + "." + methodName;
        Object[] args = point.getArgs();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(args);
        String token = methodFullName + json;
        if (repeatTokenService.checkToken(token)) {
            //重复请求
            String tips="Request Is Repeat";
            System.out.println(tips);
        } else {
            //正常请求
            String tips="Request Is Regular";
            System.out.println(tips);
            repeatTokenService.createToken(token);
        }
    }

}