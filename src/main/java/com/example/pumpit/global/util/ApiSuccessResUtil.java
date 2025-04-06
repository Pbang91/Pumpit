package com.example.pumpit.global.util;

import com.example.pumpit.global.dto.ApiSuccessResDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiSuccessResUtil {
    public static <T> ResponseEntity<ApiSuccessResDto<T>> success(T data, HttpStatus status) {
        ApiSuccessResDto<T> body = new ApiSuccessResDto<T>(data);

        return ResponseEntity.status(status).body(body);
    }
}
