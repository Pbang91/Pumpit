package com.example.pumpit.global.exception.annotation;

import com.example.pumpit.global.exception.enums.CustomerExceptionData;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ApiExceptionData {
    CustomerExceptionData errorCode();
    String details() default "";
}
