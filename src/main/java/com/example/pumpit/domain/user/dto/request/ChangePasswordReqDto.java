package com.example.pumpit.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordReqDto(
        @NotBlank
        @Schema(
                description = "temp Code",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "mmmaaa1234"
        )
        String code,

        @NotBlank
        @Schema(
                description = "새로운 비밀번호",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "newPassword0000123!"
        )
        String password
) {
}
