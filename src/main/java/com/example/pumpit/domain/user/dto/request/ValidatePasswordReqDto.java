package com.example.pumpit.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ValidatePasswordReqDto(
        @NotBlank
        @Schema(
                description = "기존 비밀번호",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "oldPassword0000123!"
        )
        String password
) {
}
