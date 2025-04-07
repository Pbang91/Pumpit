package com.example.pumpit.global.exception.annotation;

import com.example.pumpit.global.exception.enums.CustomExceptionData;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ApiExceptionData {
    CustomExceptionData errorCode();
    String details() default "";
}
