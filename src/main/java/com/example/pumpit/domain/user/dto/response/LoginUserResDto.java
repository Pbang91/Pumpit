package com.example.pumpit.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class LoginUserResDto {
    @Schema(description = "로그인 성공 시 전달받는 인가 토큰", requiredMode = Schema.RequiredMode.REQUIRED)
    public final String accessToken;

    @Schema(description = "로그인 성공 시 전달받는 리프레시 토큰", requiredMode = Schema.RequiredMode.REQUIRED)
    public final String refreshToken;

    public LoginUserResDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
