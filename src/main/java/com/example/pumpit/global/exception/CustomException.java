package com.example.pumpit.global.exception;

import com.example.pumpit.global.exception.enums.CustomExceptionData;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final CustomExceptionData errorCode;
    private String details = "";

    public CustomException(CustomExceptionData data) {
        super(data.getDescription());

        this.errorCode = data;
    }

    public CustomException(CustomExceptionData data, String details) {
        super(data.getDescription() + " " + details);

        this.errorCode = data;
        this.details = details;
    }
}
