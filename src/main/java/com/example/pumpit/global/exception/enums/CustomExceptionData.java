package com.example.pumpit.global.exception.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CustomExceptionData {
    USER_UNAUTHORIZED("0001", "인가되지 않은 사용자입니다", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("0002", "사용자를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    USER_DUPLICATED("0003", "이미 존재하는 사용자입니다", HttpStatus.CONFLICT),
    USER_PASSWORD_NOT_MATCH("0004", "비밀번호가 일치하지 않습니다", HttpStatus.UNAUTHORIZED),
    JWT_TOKEN_INVALID("0005", "유효하지 않은 JWT 토큰입니다", HttpStatus.UNAUTHORIZED),
    INVALID_PARAMETER("0006", "잘못된 요청입니다", HttpStatus.BAD_REQUEST),
    AUTH_CODE_NOT_FOUND("0007", "인증 코드가 존재하지 않습니다", HttpStatus.NOT_FOUND),
    INTERVAL_SERVER_ERROR("9999", "서버 내부 오류입니다", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String description;
    private final HttpStatus status;

    CustomExceptionData(String code, String description, HttpStatus status) {
        this.code = code;
        this.description = description;
        this.status = status;
    }
}
