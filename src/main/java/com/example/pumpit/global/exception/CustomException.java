package com.example.pumpit.global.exception;

import com.example.pumpit.global.exception.enums.CustomerExceptionData;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final CustomerExceptionData errorCode;
    private String details = "";

    public CustomException(CustomerExceptionData data) {
        super(data.getDescription());

        this.errorCode = data;
    }

    public CustomException(CustomerExceptionData data, String details) {
        super(data.getDescription() + " " + details);

        this.errorCode = data;
        this.details = details;
    }
}
