package com.example.pumpit.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginUserResDto(
        @Schema(description = "로그인 성공 시 전달받는 인가 토큰", requiredMode = Schema.RequiredMode.REQUIRED)
        String accessToken,

        @Schema(description = "로그인 성공 시 전달받는 리프레시 토큰", requiredMode = Schema.RequiredMode.REQUIRED)
        String refreshToken,

        @Schema(description = "첫 회원가입 시 전달받는 recoveryCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String recoveryCode
) {
}
