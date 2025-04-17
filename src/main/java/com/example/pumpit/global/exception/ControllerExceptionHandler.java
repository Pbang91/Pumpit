package com.example.pumpit.global.exception;

import com.example.pumpit.global.dto.ApiExceptionResDto;
import com.example.pumpit.global.exception.enums.CustomExceptionData;
import com.example.pumpit.global.log.ErrorLog;
import com.example.pumpit.global.log.LogContext;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger("ERROR_LOGGER");
    private final ObjectMapper objectMapper;

    public ControllerExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

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
    public final ResponseEntity<ApiExceptionResDto> handleAllUnhandledException(Exception ex, WebRequest request) {
        logError(request, ex);

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

        logError(request, ex, details);

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
        String details = "요청 형식이 잘못되었습니다. 올바른 JSON 형식을 확인해주세요.";
        logError(request, ex, details);

        ApiExceptionResDto apiExceptionResDto = new ApiExceptionResDto(
                CustomExceptionData.INVALID_PARAMETER.getCode(),
                CustomExceptionData.INVALID_PARAMETER.getDescription(),
                details + ex.getMessage()
        );

        return new ResponseEntity<>(apiExceptionResDto, CustomExceptionData.INVALID_PARAMETER.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        String details = "필수 파라미터가 누락되었습니다. " + ex.getParameterName() + " 파라미터를 확인해주세요.";
        logError(request, ex, details);

        ApiExceptionResDto apiExceptionResDto = new ApiExceptionResDto(
                CustomExceptionData.INVALID_PARAMETER.getCode(),
                CustomExceptionData.INVALID_PARAMETER.getDescription(),
                details
        );

        return new ResponseEntity<>(apiExceptionResDto, CustomExceptionData.INVALID_PARAMETER.getStatus());
    }

    private void logError(WebRequest request, Exception ex) {
        logError(request, ex, ex.getMessage());
    }

    private void logError(WebRequest request, Exception ex, String message) {
        try {
            ObjectMapper customObjectMapper = objectMapper.copy();
            customObjectMapper.configure(JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature(), false);
            String uri = request.getDescription(false).replace("uri=", "");
            ErrorLog errorLog = new ErrorLog(
                    LogContext.getTraceId(),
                    System.currentTimeMillis(),
                    uri,
                    message,
                    getStackTraceAsString(ex),
                    LogContext.getUserId()
            );

            logger.error(customObjectMapper.writeValueAsString(errorLog));
        } catch (Exception e) {
            logger.error("error 로그 실패. 사유: ", e);
        }
    }

    private String getStackTraceAsString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));

        return stringWriter.toString();
    }
}
