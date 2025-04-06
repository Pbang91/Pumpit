package com.example.pumpit.global.exception;

import com.example.pumpit.global.dto.ApiExceptionResDto;
import com.example.pumpit.global.exception.enums.CustomerExceptionData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public final ResponseEntity<ApiExceptionResDto> handleCustomException(CustomException ex) {
        CustomerExceptionData errorCode = ex.getErrorCode();

        ApiExceptionResDto apiExceptionResDto = new ApiExceptionResDto(
                errorCode.getCode(),
                errorCode.getDescription(),
                ex.getDetails()
        );

        return new ResponseEntity<>(apiExceptionResDto, errorCode.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ApiExceptionResDto> handleException(Exception ex) {
        ApiExceptionResDto apiExceptionResDto = new ApiExceptionResDto(
                CustomerExceptionData.INTERVAL_SERVER_ERROR.getCode(),
                CustomerExceptionData.INTERVAL_SERVER_ERROR.getDescription(),
                ex.getMessage()
        );

        return new ResponseEntity<>(apiExceptionResDto, CustomerExceptionData.INTERVAL_SERVER_ERROR.getStatus());
    }
}
