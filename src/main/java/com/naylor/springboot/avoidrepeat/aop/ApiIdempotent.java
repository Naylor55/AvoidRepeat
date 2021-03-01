package com.naylor.springboot.avoidrepeat.aop;


import java.lang.annotation.*;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiIdempotent {
}
