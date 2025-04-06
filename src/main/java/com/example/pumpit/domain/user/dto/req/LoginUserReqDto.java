package com.example.pumpit.domain.user.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginUserReqDto(
        @Schema(description = "email 로그인 시 사용하는 email", nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String email,

        @Schema(description = "email 로그인 시 사용하는 password", nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String password,

        @Schema(description = "oauth2 로그인 시 사용하는 accessToken", nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String accessToken
) {
}
