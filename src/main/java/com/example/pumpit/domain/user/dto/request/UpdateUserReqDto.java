package com.example.pumpit.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateUserReqDto(
        @Schema(description = "사용자 닉네임", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String nickName,

        @Schema(description = "사용자 모바일 번호", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
        String phone
) {
}
