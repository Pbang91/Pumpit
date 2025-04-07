package com.example.pumpit.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class LoginUserRequestTokenResDto {
    @Schema(description = "로그인 시도 시accessToken을 발급받기 위한 임시 코드")
    @Getter
    private String tempCode;

    public LoginUserRequestTokenResDto(String tempCode) {
        this.tempCode = tempCode;
    }
}
