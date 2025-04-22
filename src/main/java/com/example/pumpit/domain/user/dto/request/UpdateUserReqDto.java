package com.example.pumpit.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateUserReqDto(
        @Schema(description = "사용자 활동명", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String nickName
) {
}
