package com.naylor.springboot.avoidrepeat.Interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.naylor.springboot.avoidrepeat.domain.repeat.service.RepeatTokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


public class ApiIdempotentInterceptor{}
// public class ApiIdempotentInterceptor implements HandlerInterceptor {
//     @Autowired
//     private RepeatTokenService tokenService;

//     @Override
//     public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//             throws Exception {
//         if (!(handler instanceof HandlerMethod)) {
//             return true;
//         }
//         HandlerMethod handlerMethod = (HandlerMethod) handler;
//         Method method = handlerMethod.getMethod();
//         ApiIdempotent methodAnnotation = method.getAnnotation(ApiIdempotent.class);
//         if (null != methodAnnotation) {
//             Parameter[] parameterArray=  method.getParameters();
//             for (Parameter parameter : parameterArray) {
//                 String parameterType=parameter.getType().toString();
//                 String parameterName=parameter.getName().toString();
                
//             }
//             // 校验通过放行，校验不通过全局异常捕获后输出返回结果
//             tokenService.checkToken(request);
//         }
//         return true;
//     }

//     @Override
//     public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
//             ModelAndView modelAndView) throws Exception {
//     }

//     @Override
//     public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
//             throws Exception {
//     }

// }
