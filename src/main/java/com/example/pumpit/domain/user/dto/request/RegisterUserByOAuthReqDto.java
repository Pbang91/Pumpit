package com.example.pumpit.domain.user.dto.request;

import com.example.pumpit.domain.user.enums.RegisterUserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record RegisterUserByOAuthReqDto(
        @NotNull
        @Schema(description = "회원 가입 시도 타입", example = "KAKAO", requiredMode = Schema.RequiredMode.REQUIRED)
        RegisterUserType type
) {
}
