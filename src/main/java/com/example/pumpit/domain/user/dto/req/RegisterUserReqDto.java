package com.example.pumpit.domain.user.dto.req;

import com.example.pumpit.domain.user.enums.RegisterUserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record RegisterUserReqDto(
        @NotNull
        @Schema(description = "회원 가입 시도 타입", example = "KAKAO", requiredMode = Schema.RequiredMode.REQUIRED)
        RegisterUserType type,

        @Schema(
                description = "이메일 가입 시 email",
                example = "example@mail.com",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                nullable = true
        )
        String email,

        @Schema(
                description = "이메일 가입 시 password",
                example = "password1234",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                nullable = true
        )
        String password
) {
}
