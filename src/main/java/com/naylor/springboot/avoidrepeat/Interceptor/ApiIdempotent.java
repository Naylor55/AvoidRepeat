package com.naylor.springboot.avoidrepeat.Interceptor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiIdempotent {
}
