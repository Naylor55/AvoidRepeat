package com.naylor.springboot.avoidrepeat.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naylor.springboot.avoidrepeat.constant.AvoidRepeatPrefix;
import com.naylor.springboot.avoidrepeat.constant.ResponseCode;
import com.naylor.springboot.avoidrepeat.domain.repeat.service.RepeatTokenService;
import com.naylor.springboot.avoidrepeat.exception.RepeatException;

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
    public Object methodAround(ProceedingJoinPoint point) throws Throwable {
        String typeName = point.getSignature().getDeclaringTypeName();
        String methodName = point.getSignature().getName();
        String methodFullName = typeName + "." + methodName;
        String argsJson = JSON.toJSONString(point.getArgs(), SerializerFeature.WriteClassName);
        String json = argsJson.replaceAll("\"", "");
        String token = methodFullName + json;
        System.out.println("Token is :" + token);
        if (repeatTokenService.checkToken(AvoidRepeatPrefix.Avoid_Repeat_User + token)) {
            // 重复请求
            String tips = "Request Is Repeat";
            System.out.println(tips);
            throw new RepeatException(ResponseCode.REPETITIVE_OPERATION);
        } else {
            // 正常请求
            String tips = "Request Is Normal";
            System.out.println(tips);
            repeatTokenService.createToken(AvoidRepeatPrefix.Avoid_Repeat_User + token);
            return point.proceed();
        }

    }

}