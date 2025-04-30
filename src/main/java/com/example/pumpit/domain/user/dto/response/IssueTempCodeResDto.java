package com.example.pumpit.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record IssueTempCodeResDto(
        @Schema(
                description = "임시 코드",
                example = "1234567890",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String tempCode
) {
}
