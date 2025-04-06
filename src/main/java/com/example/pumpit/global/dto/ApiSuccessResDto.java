package com.example.pumpit.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ApiSuccessResDto<T>(
    @Schema(description = "응답 데이터", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull T data
) {
}
