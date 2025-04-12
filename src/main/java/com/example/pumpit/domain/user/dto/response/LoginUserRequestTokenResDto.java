package com.example.pumpit.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginUserRequestTokenResDto (
        @Schema(
                description = "로그인 시도 시 accessToken을 발급받기 위한 임시 코드",
                example = "tempCode:1234567890",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String tempCode
) {
}
