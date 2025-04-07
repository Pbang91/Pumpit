package com.example.pumpit.global.exception;

import com.example.pumpit.global.dto.ApiExceptionResDto;
import com.example.pumpit.global.exception.enums.CustomExceptionData;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public final ResponseEntity<ApiExceptionResDto> handleCustomException(CustomException ex) {
        CustomExceptionData errorCode = ex.getErrorCode();

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
                CustomExceptionData.INTERVAL_SERVER_ERROR.getCode(),
                CustomExceptionData.INTERVAL_SERVER_ERROR.getDescription(),
                ex.getMessage()
        );

        return new ResponseEntity<>(apiExceptionResDto, CustomExceptionData.INTERVAL_SERVER_ERROR.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + " : " + fieldError.getDefaultMessage())
                .toList();

        String details = String.join(", ", errors);

        ApiExceptionResDto apiExceptionResDto = new ApiExceptionResDto(
                CustomExceptionData.INVALID_PARAMETER.getCode(),
                CustomExceptionData.INVALID_PARAMETER.getDescription(),
                details
        );

        return new ResponseEntity<>(apiExceptionResDto, CustomExceptionData.INVALID_PARAMETER.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        ApiExceptionResDto apiExceptionResDto = new ApiExceptionResDto(
                CustomExceptionData.INVALID_PARAMETER.getCode(),
                CustomExceptionData.INVALID_PARAMETER.getDescription(),
                "요청 형식이 잘못되었습니다. 올바른 JSON 형식을 확인해주세요." + ex.getMessage()
        );

        return new ResponseEntity<>(apiExceptionResDto, CustomExceptionData.INVALID_PARAMETER.getStatus());
    }
}
